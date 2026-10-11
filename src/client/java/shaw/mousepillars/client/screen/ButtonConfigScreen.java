package shaw.mousepillars.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import shaw.mousepillars.blockentity.LinkButtonBlockEntity;
import shaw.mousepillars.blockentity.PillarBlockEntity;
import shaw.mousepillars.network.UpdateButtonConfigPayload;

/** GUI del botón de control (solo Creativo): únicamente la ID de vinculación. */
public class ButtonConfigScreen extends Screen {
	private static final int WIDGET_WIDTH = 200;

	private final BlockPos pos;
	private String linkIdText;
	private EditBox linkIdBox;

	public ButtonConfigScreen(BlockPos pos) {
		super(Component.translatable("screen.mousepillars.button.title"));
		this.pos = pos;

		String initialLinkId = "";
		Minecraft client = Minecraft.getInstance();
		if (client.level != null && client.level.getBlockEntity(pos) instanceof LinkButtonBlockEntity button) {
			initialLinkId = button.getLinkId();
		}
		this.linkIdText = initialLinkId;
	}

	@Override
	protected void init() {
		int left = this.width / 2 - WIDGET_WIDTH / 2;
		int top = this.height / 2 - 30;

		this.linkIdBox = new EditBox(this.font, left, top, WIDGET_WIDTH, 20, Component.translatable("screen.mousepillars.link_id"));
		this.linkIdBox.setMaxLength(PillarBlockEntity.MAX_LINK_ID_LENGTH);
		this.linkIdBox.setValue(this.linkIdText);
		this.linkIdBox.setResponder(text -> this.linkIdText = text);
		this.addRenderableWidget(this.linkIdBox);

		this.addRenderableWidget(Button.builder(Component.translatable("screen.mousepillars.apply"), button -> {
			ClientPlayNetworking.send(new UpdateButtonConfigPayload(this.pos, this.linkIdText));
			this.onClose();
		}).bounds(left, top + 35, WIDGET_WIDTH, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		int left = this.width / 2 - WIDGET_WIDTH / 2;
		int top = this.height / 2 - 30;
		graphics.text(this.font, this.title.getString(), left, top - 30, 0xFFFFFFFF, true);
		graphics.text(this.font, Component.translatable("screen.mousepillars.link_id").getString(), left, top - 12, 0xFFAAAAAA, true);
	}
}
