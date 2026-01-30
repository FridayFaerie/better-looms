package fridayfaerie.betterlooms.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

	@Shadow
	protected T handler;

	@Shadow
	protected int titleX;

	@Shadow
	protected int titleY;

	@Shadow
	protected int playerInventoryTitleX;

	@Shadow
	protected int backgroundHeight;

	@Shadow
	protected int playerInventoryTitleY;

	@Inject(method = "init", at = @At("TAIL"))
	private void betterlooms$moveSlots(CallbackInfo ci) {
		if (handler instanceof LoomScreenHandler) {
			for (Slot slot : handler.slots) {
				SlotAccessor acc = (SlotAccessor) slot;

				switch (slot.id) {
					case 0 -> {
						acc.setX(146);
						acc.setY(101);
					}   // banner
					case 1 -> {
						acc.setX(8);
						acc.setY(59);
					}   // dye
					case 2 -> {
						acc.setX(8);
						acc.setY(84);
					}   // pattern
					case 3 -> {
						acc.setX(8);
						acc.setY(34);
					}
					default -> {
						acc.setY(acc.getY()+44);
					}
				}
			}
		}
	}

}