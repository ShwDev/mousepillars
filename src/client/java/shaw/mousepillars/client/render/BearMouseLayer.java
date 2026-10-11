package shaw.mousepillars.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

import shaw.mousepillars.registry.ModDisplayBlocks;

/**
 * Dibuja el modelo del oso (un modelo de bloque JSON exportado de Blockbench) en lugar del jugador.
 * Es estático: no mueve patas ni brazos. Para animarlo habría que pasar a un EntityModel con ModelParts.
 */
public class BearMouseLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
	/** Marca puesta en el estado de render por BearMouseAppearance: "este jugador se dibuja como oso". */
	public static final RenderStateDataKey<Boolean> IS_MOUSE = RenderStateDataKey.create(() -> "mousepillars is mouse");

	private static final BlockDisplayContext CONTEXT = BlockDisplayContext.create();

	private final BlockModelResolver resolver;
	private final BlockModelRenderState bearState = new BlockModelRenderState();
	private boolean resolved = false;

	public BearMouseLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent, BlockModelResolver resolver) {
		super(parent);
		this.resolver = resolver;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, AvatarRenderState state, float limbAngle, float limbDistance) {
		if (!Boolean.TRUE.equals(state.getData(IS_MOUSE))) {
			return;
		}

		if (!this.resolved) {
			this.resolver.update(this.bearState, ModDisplayBlocks.BEAR_DISPLAY.defaultBlockState(), CONTEXT);
			this.resolved = true;
		}

		poseStack.pushPose();
		// El renderer de entidades entrega la pila volteada (x e y invertidas) y desplazada 1.501 hacia arriba.
		// Deshacemos ambas cosas para trabajar con ejes normales y con los pies del modelo en el suelo.
		// Si el oso sale mirando hacia atrás, añade aquí: poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(-0.5F, -1.501F, -0.5F);
		this.bearState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}
}
