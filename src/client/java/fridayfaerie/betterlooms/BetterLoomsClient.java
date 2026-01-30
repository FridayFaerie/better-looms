package fridayfaerie.betterlooms;

import fridayfaerie.betterlooms.config.BetterLoomsConfig;
import fridayfaerie.betterlooms.screen.FancyLoomScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class BetterLoomsClient implements ClientModInitializer {
	public static BetterLoomsConfig CONFIG;

	@Override
	public void onInitializeClient() {
		AutoConfig.register(BetterLoomsConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(BetterLoomsConfig.class).getConfig();
	}
}