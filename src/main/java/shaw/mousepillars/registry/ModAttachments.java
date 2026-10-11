package shaw.mousepillars.registry;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.network.codec.ByteBufCodecs;

import shaw.mousepillars.MousePillars;

public final class ModAttachments {
	public static final AttachmentType<Boolean> MOUSE_FORM = AttachmentRegistry.create(
			MousePillars.id("mouse_form"),
			builder -> builder.syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
	);

	private ModAttachments() {
	}

	public static void init() {
		// Fuerza la carga de la clase (y por tanto el registro del attachment).
	}
}
