package fridayfaerie.betterlooms.mixin.client;

import fridayfaerie.betterlooms.screen.FancyLoomScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.LoomScreenHandler;
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
@Mixin(HandledScreens.class)
public class HandledScreensMixin {

	@ModifyArgs(
			method = "<clinit>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreens;register(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/client/gui/screen/ingame/HandledScreens$Provider;)V"
			)
	)
	private static void replaceLoomProvider(Args args) {
		ScreenHandlerType<?> type = args.get(0);
		if (type == ScreenHandlerType.LOOM) {
			args.set(1, new HandledScreens.Provider<LoomScreenHandler, FancyLoomScreen>() {
				@Override
				public FancyLoomScreen create(LoomScreenHandler handler, PlayerInventory inv, Text title) {
					return new FancyLoomScreen(handler, inv, title);
//					return new LoomScreen(handler, inv, title);
				}
			});
		}
	}
}
