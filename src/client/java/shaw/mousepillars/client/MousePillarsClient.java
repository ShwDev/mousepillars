package shaw.mousepillars.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;

import shaw.mousepillars.client.render.BearMouseAppearance;
import shaw.mousepillars.client.render.BearMouseLayer;
import shaw.mousepillars.client.render.MouseAppearances;
import shaw.mousepillars.client.screen.ButtonConfigScreen;
import shaw.mousepillars.client.screen.PillarConfigScreen;
import shaw.mousepillars.logic.MouseState;
import shaw.mousepillars.network.OpenConfigScreenPayload;

public class MousePillarsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Servidor -> cliente: abrir la GUI de configuración.
		ClientPlayNetworking.registerGlobalReceiver(OpenConfigScreenPayload.TYPE, (payload, context) -> {
			Minecraft client = context.client();
			client.gui.setScreen(payload.button()
					? new ButtonConfigScreen(payload.pos())
					: new PillarConfigScreen(payload.pos()));
		});

		// Sin inventario: si se abre cualquier pantalla de contenedor (inventario, creativo, cofre...) siendo ratón, se cierra.
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof AbstractContainerScreen<?> && isLocalPlayerMouse(client)) {
				client.execute(() -> {
					if (client.gui.screen() == screen) {
						client.gui.setScreen(null);
					}
				});
			}
		});

		// Sin hotbar: se oculta la barra y el texto del ítem sostenido mientras seas ratón.
		hideWhileMouse(VanillaHudElements.HOTBAR);
		hideWhileMouse(VanillaHudElements.HELD_ITEM_TOOLTIP);

		// Aspecto del ratón: el oso de prueba. Para volver al modelo de jugador encogido, usa DefaultMouseAppearance.
		MouseAppearances.set(new BearMouseAppearance());
		LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityRenderer instanceof AvatarRenderer avatarRenderer) {
				registrationHelper.register(new BearMouseLayer(avatarRenderer, context.getBlockModelResolver()));
			}
		});
	}

	private static void hideWhileMouse(Identifier vanillaElement) {
		HudElementRegistry.replaceElement(vanillaElement, original -> (graphics, deltaTracker) -> {
			if (isLocalPlayerMouse(Minecraft.getInstance())) {
				return;
			}
			original.extractRenderState(graphics, deltaTracker);
		});
	}

	private static boolean isLocalPlayerMouse(Minecraft client) {
		return client.player != null && MouseState.isMouse(client.player);
	}
}
