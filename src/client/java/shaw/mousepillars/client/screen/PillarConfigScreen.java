package shaw.mousepillars.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import shaw.mousepillars.blockentity.PillarBlockEntity;
import shaw.mousepillars.network.UpdatePillarConfigPayload;

/**
 * GUI del pilar (solo Creativo). Es una Screen de cliente pura, sin ScreenHandler: no hay slots ni ítems,
 * solo tres valores. Los valores iniciales se leen del BlockEntity del cliente (ya sincronizado con el chunk)
 * y los cambios viajan al servidor en un único paquete al pulsar "Aplicar".
 */
public class PillarConfigScreen extends Screen {
	private static final int WIDGET_WIDTH = 200;

	private final BlockPos pos;

	// El estado vive en campos (no en los widgets) para sobrevivir a init(), que se vuelve a ejecutar al cambiar el tamaño de ventana.
	private boolean enabled;
	private String rangeText;
	private String linkIdText;

	private Button toggleButton;
	private EditBox rangeBox;
	private EditBox linkIdBox;

	public PillarConfigScreen(BlockPos pos) {
		super(Component.translatable("screen.mousepillars.pillar.title"));
		this.pos = pos;

		boolean initialEnabled = false;
		int initialRange = 5;
		String initialLinkId = "";
		Minecraft client = Minecraft.getInstance();
		if (client.level != null && client.level.getBlockEntity(pos) instanceof PillarBlockEntity pillar) {
			initialEnabled = pillar.isEnabled();
			initialRange = pillar.getRange();
			initialLinkId = pillar.getLinkId();
		}
		this.enabled = initialEnabled;
		this.rangeText = Integer.toString(initialRange);
		this.linkIdText = initialLinkId;
	}

	@Override
	protected void init() {
		int left = this.width / 2 - WIDGET_WIDTH / 2;
		int top = this.height / 2 - 60;

		this.toggleButton = Button.builder(stateLabel(), button -> {
			this.enabled = !this.enabled;
			button.setMessage(stateLabel());
		}).bounds(left, top, WIDGET_WIDTH, 20).build();
		this.addRenderableWidget(this.toggleButton);

		this.rangeBox = new EditBox(this.font, left, top + 40, WIDGET_WIDTH, 20, Component.translatable("screen.mousepillars.range"));
		this.rangeBox.setMaxLength(2);
		this.rangeBox.setValue(this.rangeText);
		this.rangeBox.setResponder(text -> this.rangeText = text);
		this.addRenderableWidget(this.rangeBox);

		this.linkIdBox = new EditBox(this.font, left, top + 80, WIDGET_WIDTH, 20, Component.translatable("screen.mousepillars.link_id"));
		this.linkIdBox.setMaxLength(PillarBlockEntity.MAX_LINK_ID_LENGTH);
		this.linkIdBox.setValue(this.linkIdText);
		this.linkIdBox.setResponder(text -> this.linkIdText = text);
		this.addRenderableWidget(this.linkIdBox);

		this.addRenderableWidget(Button.builder(Component.translatable("screen.mousepillars.apply"), button -> {
			this.apply();
			this.onClose();
		}).bounds(left, top + 115, WIDGET_WIDTH, 20).build());
	}

	private Component stateLabel() {
		return Component.translatable(this.enabled ? "screen.mousepillars.state_on" : "screen.mousepillars.state_off");
	}

	private void apply() {
		int range = PillarBlockEntity.MIN_RANGE;
		try {
			range = Integer.parseInt(this.rangeText.replaceAll("\\D", ""));
		} catch (NumberFormatException ignored) {
			// campo vacío: se queda en el mínimo; el servidor además vuelve a acotar el valor
		}
		ClientPlayNetworking.send(new UpdatePillarConfigPayload(this.pos, this.enabled, range, this.linkIdText));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		int left = this.width / 2 - WIDGET_WIDTH / 2;
		int top = this.height / 2 - 60;
		graphics.text(this.font, this.title.getString(), left, top - 30, 0xFFFFFFFF, true);
		graphics.text(this.font, Component.translatable("screen.mousepillars.range").getString(), left, top + 28, 0xFFAAAAAA, true);
		graphics.text(this.font, Component.translatable("screen.mousepillars.link_id").getString(), left, top + 68, 0xFFAAAAAA, true);
	}
}
