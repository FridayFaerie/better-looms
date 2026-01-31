package fridayfaerie.betterlooms.mixin.client;

import fridayfaerie.betterlooms.BetterLoomsClient;
import fridayfaerie.betterlooms.screen.FancyLoomScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
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
			HandledScreens.Provider provider = new HandledScreens.Provider() {
				@Override
				public Screen create(net.minecraft.screen.ScreenHandler handler, PlayerInventory inv, Text title) {
					LoomScreenHandler loomHandler = (LoomScreenHandler) handler;
					if (BetterLoomsClient.CONFIG != null && BetterLoomsClient.CONFIG.enable) {
						return new FancyLoomScreen(loomHandler, inv, title);
					} else {
						return new LoomScreen(loomHandler, inv, title);
					}
				}
			};
			args.set(1, provider);
		}
	}
}
