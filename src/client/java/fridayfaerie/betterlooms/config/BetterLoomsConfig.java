package fridayfaerie.betterlooms.config;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "better-looms")
public class BetterLoomsConfig implements ConfigData {

    public boolean enableInstantcomplete = true;
}
