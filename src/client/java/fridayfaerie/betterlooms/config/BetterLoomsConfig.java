package fridayfaerie.betterlooms.config;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "better-looms")
public class BetterLoomsConfig implements ConfigData {

    public boolean enable = true;

    public boolean enableInstantcomplete = true;

    @ConfigEntry.BoundedDiscrete(max = 100)
    public int i1;
    @ConfigEntry.BoundedDiscrete(max = 100)
    public int i2;
    public int a1 = 0;
    public int a2 = 0;
    public int a3 = 0;
    public int a4 = 0;
    public int a5 = 0;
    public int a6 = 0;
    public int a7 = 0;
    public int a8 = 0;
    public int a9 = 0;
    public int a10 = 0;
    public int a11 = 0;
    public int a12 = 0;
}
