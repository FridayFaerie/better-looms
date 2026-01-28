package fridayfaerie.betterlooms.mixin.client;

import fridayfaerie.betterlooms.screen.FancyLoomScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

	@Shadow
	protected T handler;

	@Inject(method = "init", at = @At("TAIL"))
	private void betterlooms$moveSlots(CallbackInfo ci) {
		if (handler instanceof LoomScreenHandler) {
			repositionLoomSlots(handler);
		}
	}

	private void repositionLoomSlots(ScreenHandler handler) {
		for (Slot slot : handler.slots) {
			SlotAccessor acc = (SlotAccessor) slot;

			switch (slot.id) {
				case 0 -> { acc.setX(20); acc.setY(20); }   // banner
				case 1 -> { acc.setX(44); acc.setY(20); }   // dye
				case 2 -> { acc.setX(68); acc.setY(20); }   // pattern
				case 3 -> { acc.setX(140); acc.setY(50); }  // output
			}
		}
	}
}
