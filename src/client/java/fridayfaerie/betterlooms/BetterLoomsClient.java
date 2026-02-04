package fridayfaerie.betterlooms;

import fridayfaerie.betterlooms.config.BetterLoomsConfig;
import fridayfaerie.betterlooms.screen.FancyLoomScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterLoomsClient implements ClientModInitializer {
	public static BetterLoomsConfig CONFIG;


	public static final Map<DyeColor, KeyBinding> COLOR_KEY_BINDINGS = new EnumMap<>(DyeColor.class);
	public static final Map<DyeColor, KeyBinding> COLOR_CTRL_BINDINGS = new EnumMap<>(DyeColor.class);
	public static final Map<DyeColor, KeyBinding> COLOR_SHIFT_BINDINGS = new EnumMap<>(DyeColor.class);
	public static final Map<DyeColor, KeyBinding> COLOR_ALT_BINDINGS = new EnumMap<>(DyeColor.class);

	List<RegistryEntry.Reference<BannerPattern>> allPatterns = List.of();
//	public static final KeyBinding[] patternKeyBindings = new KeyBinding[64];
//	public static final KeyBinding[] patternCtrlBindings = new KeyBinding[64];
//	public static final KeyBinding[] patternShiftBindings = new KeyBinding[64];
//	public static final KeyBinding[] patternAltBindings = new KeyBinding[64];

	public static final Map<Integer, KeyBinding> PATTERN_KEY_BINDINGS = new HashMap<>();
	public static final Map<Integer, KeyBinding> PATTERN_CTRL_BINDINGS = new HashMap<>();
	public static final Map<Integer, KeyBinding> PATTERN_SHIFT_BINDINGS = new HashMap<>();
	public static final Map<Integer, KeyBinding> PATTERN_ALT_BINDINGS = new HashMap<>();

	public static KeyBinding binding1;
	KeyBinding.Category bannerKeyboardColors = KeyBinding.Category.create(Identifier.of("better-looms", "banner_keyboard_colors"));
	KeyBinding.Category bannerKeyboardPatterns = KeyBinding.Category.create(Identifier.of("better-looms", "banner_keyboard_patterns"));


	@Override
	public void onInitializeClient() {
		AutoConfig.register(BetterLoomsConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(BetterLoomsConfig.class).getConfig();

		for (DyeColor color : DyeColor.values()){
			String id = "key.better-looms." + color.name().toLowerCase();
			KeyBinding colorKeyBinding = registerKey(id,defaultColorKeys(color),bannerKeyboardColors);
			KeyBinding colorCtrlBinding = registerKey(id+"_ctrl",defaultColorCtrls(color),bannerKeyboardColors);
			KeyBinding colorShiftBinding = registerKey(id+"_shift",defaultColorShifts(color),bannerKeyboardColors);
			KeyBinding colorAltBinding = registerKey(id+"_alt",defaultColorAlts(color),bannerKeyboardColors);
			COLOR_KEY_BINDINGS.put(color,colorKeyBinding);
			COLOR_CTRL_BINDINGS.put(color,colorCtrlBinding);
			COLOR_SHIFT_BINDINGS.put(color,colorShiftBinding);
			COLOR_ALT_BINDINGS.put(color,colorAltBinding);
		}

		// Does not work for servers with different banner patterns but ah well
		// I didn't really want to read banners on the fly
		for (int pattern = 0 ; pattern < 43; pattern++) {
			String id = "key.better-looms.pattern_" + String.format("%02d",pattern);
			KeyBinding patternKeyBinding = registerKey(id,defaultPatternKeys(pattern),bannerKeyboardPatterns);
			KeyBinding patternCtrlBinding = registerKey(id+"_ctrl",defaultPatternCtrls(pattern),bannerKeyboardPatterns);
			KeyBinding patternShiftBinding = registerKey(id+"_shift",defaultPatternShifts(pattern),bannerKeyboardPatterns);
			KeyBinding patternAltBinding = registerKey(id+"_alt",defaultPatternAlts(pattern),bannerKeyboardPatterns);
			PATTERN_KEY_BINDINGS.put(pattern,patternKeyBinding);
			PATTERN_CTRL_BINDINGS.put(pattern,patternCtrlBinding);
			PATTERN_SHIFT_BINDINGS.put(pattern,patternShiftBinding);
			PATTERN_ALT_BINDINGS.put(pattern,patternAltBinding);
		}

//		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
//			var registry = handler.getRegistryManager().getOptional(RegistryKeys.BANNER_PATTERN);
//			registry.ifPresent(r -> {
//				List<RegistryEntry.Reference<BannerPattern>> allPatterns = r.streamEntries().toList();
//				for (int pattern = 0; pattern < allPatterns.size(); pattern++){
//					String id = "key.better-looms.pattern_" + pattern;
//					KeyBinding keyBinding = registerKey(id,defaultPatternKeys(pattern),bannerKeyboardPatterns);
//					KeyBinding ctrlBinding = registerKey(id+"_ctrl",defaultPatternCtrls(pattern),bannerKeyboardPatterns);
//					KeyBinding shiftBinding = registerKey(id+"_shift",defaultPatternShifts(pattern),bannerKeyboardPatterns);
//					KeyBinding altBinding = registerKey(id+"_alt",defaultPatternAlts(pattern),bannerKeyboardPatterns);
//				}
//			});
//		});
	}

	private static KeyBinding registerKey(String id, int key, KeyBinding.Category category) {
		return KeyBindingHelper.registerKeyBinding(new KeyBinding(
				id, InputUtil.Type.KEYSYM, key, category
		));
	}


	private static int defaultColorKeys(DyeColor color) {
		return switch (color) {
			case WHITE      -> GLFW.GLFW_KEY_U;
			case ORANGE     -> GLFW.GLFW_KEY_I;
			case MAGENTA    -> GLFW.GLFW_KEY_O;
			case LIGHT_BLUE -> GLFW.GLFW_KEY_P;
			case YELLOW     -> GLFW.GLFW_KEY_LEFT_BRACKET;
			case LIME       -> GLFW.GLFW_KEY_RIGHT_BRACKET;
			case PINK       -> GLFW.GLFW_KEY_H;
			case GRAY       -> GLFW.GLFW_KEY_J;
			case LIGHT_GRAY -> GLFW.GLFW_KEY_K;
			case CYAN       -> GLFW.GLFW_KEY_L;
			case PURPLE     -> GLFW.GLFW_KEY_SEMICOLON;
			case BLUE       -> GLFW.GLFW_KEY_N;
			case BROWN      -> GLFW.GLFW_KEY_M;
			case GREEN      -> GLFW.GLFW_KEY_COMMA;
			case RED        -> GLFW.GLFW_KEY_PERIOD;
			case BLACK      -> GLFW.GLFW_KEY_SLASH;
			default 		-> -1;
    };
  }
  private static int defaultPatternKeys(int pattern){
    return switch (pattern) {
			case 0 -> GLFW.GLFW_KEY_Q;
			case 1 -> GLFW.GLFW_KEY_W;
			case 2 -> GLFW.GLFW_KEY_B;
			case 3 -> GLFW.GLFW_KEY_V;
			case 4 -> GLFW.GLFW_KEY_Z;
			case 5 -> GLFW.GLFW_KEY_D;
			case 6 -> GLFW.GLFW_KEY_W;
			case 7 -> GLFW.GLFW_KEY_G;
			case 8 -> GLFW.GLFW_KEY_G;
			case 9 -> GLFW.GLFW_KEY_G;
			case 10 -> GLFW.GLFW_KEY_G;
			case 11 -> GLFW.GLFW_KEY_V;
			case 12 -> GLFW.GLFW_KEY_X;
			case 13 -> GLFW.GLFW_KEY_X;
			case 14 -> GLFW.GLFW_KEY_Y;
			case 15 -> GLFW.GLFW_KEY_Y;
			case 16 -> GLFW.GLFW_KEY_V;
			case 17 -> GLFW.GLFW_KEY_E;
			case 18 -> GLFW.GLFW_KEY_E;
			case 19 -> GLFW.GLFW_KEY_E;
			case 20 -> GLFW.GLFW_KEY_E;
			case 21 -> GLFW.GLFW_KEY_C;
			case 22 -> GLFW.GLFW_KEY_C;
			case 23 -> GLFW.GLFW_KEY_V;
			case 24 -> GLFW.GLFW_KEY_Z;
			case 25 -> GLFW.GLFW_KEY_D;
			case 26 -> GLFW.GLFW_KEY_F;
			case 27 -> GLFW.GLFW_KEY_F;
			case 28 -> GLFW.GLFW_KEY_F;
			case 29 -> GLFW.GLFW_KEY_F;
			case 30 -> GLFW.GLFW_KEY_D;
			case 31 -> GLFW.GLFW_KEY_R;
			case 32 -> GLFW.GLFW_KEY_T;
			case 33 -> GLFW.GLFW_KEY_T;
			case 34 -> GLFW.GLFW_KEY_T;
			case 35 -> GLFW.GLFW_KEY_R;
			case 36 -> GLFW.GLFW_KEY_T;
			case 37 -> GLFW.GLFW_KEY_R;
			case 38 -> GLFW.GLFW_KEY_R;
			case 39 -> GLFW.GLFW_KEY_S;
			case 40 -> GLFW.GLFW_KEY_S;
			case 41 -> GLFW.GLFW_KEY_A;
			case 42 -> GLFW.GLFW_KEY_A;
        	default -> -1;
    };
	}
	private static int defaultColorCtrls(DyeColor color) {
		return switch (color) {
			case WHITE      -> -1;
			case ORANGE     -> -1;
			case MAGENTA    -> -1;
			case LIGHT_BLUE -> -1;
			case YELLOW     -> -1;
			case LIME       -> -1;
			case PINK       -> -1;
			case GRAY       -> -1;
			case LIGHT_GRAY -> -1;
			case CYAN       -> -1;
			case PURPLE     -> -1;
			case BLUE       -> -1;
			case BROWN      -> -1;
			case GREEN      -> -1;
			case RED        -> -1;
			case BLACK      -> -1;
			default -> -1;
		};
	}
	private static int defaultPatternCtrls(int pattern) {
		return switch (pattern) {
			case 1 -> -1;
			case 2 -> -1;
			case 3 -> -1;
			case 4 -> -1;
			case 5 -> -1;
			case 6 -> -1;
			case 7 -> -1;
			case 8 -> -1;
			case 9 -> -1;
			case 10 -> -1;
			case 11 -> -1;
			case 12 -> -1;
			case 13 -> -1;
			case 14 -> -1;
			case 15 -> -1;
			case 16 -> -1;
			case 17 -> -1;
			case 18 -> -1;
			case 19 -> -1;
			case 20 -> -1;
			case 21 -> -1;
			case 22 -> -1;
			case 23 -> -1;
			case 24 -> -1;
			case 25 -> -1;
			case 26 -> -1;
			case 27 -> -1;
			case 28 -> -1;
			case 29 -> -1;
			case 30 -> -1;
			case 31 -> -1;
			case 32 -> -1;
			case 33 -> -1;
			case 34 -> -1;
			case 35 -> -1;
			case 36 -> -1;
			case 37 -> -1;
			case 38 -> -1;
			case 39 -> -1;
			case 40 -> -1;
			case 41 -> -1;
			case 42 -> -1;
			case 43 -> -1;
			default -> -1;
        };
	}
	private static int defaultColorShifts(DyeColor color) {
		return switch (color) {
			case WHITE      -> -1;
			case ORANGE     -> -1;
			case MAGENTA    -> -1;
			case LIGHT_BLUE -> -1;
			case YELLOW     -> -1;
			case LIME       -> -1;
			case PINK       -> -1;
			case GRAY       -> -1;
			case LIGHT_GRAY -> -1;
			case CYAN       -> -1;
			case PURPLE     -> -1;
			case BLUE       -> -1;
			case BROWN      -> -1;
			case GREEN      -> -1;
			case RED        -> -1;
			case BLACK      -> -1;
			default 		-> -1;
		};
	}
	private static int defaultPatternShifts(int pattern) {
		return switch (pattern) {
			case 1 -> -1;
			case 2 -> -1;
			case 3 -> -1;
			case 4 -> -1;
			case 5 -> -1;
			case 6 -> -1;
			case 7 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 8 -> -1;
			case 9 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 10 -> -1;
			case 11 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 12 -> -1;
			case 13 -> -1;
			case 14 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 15 -> -1;
			case 16 -> -1;
			case 17 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 18 -> -1;
			case 19 -> -1;
			case 20 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 21 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 22 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 23 -> -1;
			case 24 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 25 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 26 -> -1;
			case 27 -> -1;
			case 28 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 29 -> -1;
			case 30 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 31 -> -1;
			case 32 -> -1;
			case 33 -> -1;
			case 34 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 35 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 36 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 37 -> -1;
			case 38 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case 39 -> -1;
			case 40 -> -1;
			case 41 -> -1;
			case 42 -> -1;
			case 43 -> -1;
			default -> -1;
		};
	}
	private static int defaultColorAlts(DyeColor color) {
		return switch (color) {
			case WHITE      -> -1;
			case ORANGE     -> -1;
			case MAGENTA    -> -1;
			case LIGHT_BLUE -> -1;
			case YELLOW     -> -1;
			case LIME       -> -1;
			case PINK       -> -1;
			case GRAY       -> -1;
			case LIGHT_GRAY -> -1;
			case CYAN       -> -1;
			case PURPLE     -> -1;
			case BLUE       -> -1;
			case BROWN      -> -1;
			case GREEN      -> -1;
			case RED        -> -1;
			case BLACK      -> -1;
			default 		-> -1;
		};
	}
	private static int defaultPatternAlts(int pattern) {
		return switch (pattern) {
			case 1 -> -1;
			case 2 -> -1;
			case 3 -> -1;
			case 4 -> -1;
			case 5 -> -1;
			case 6 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 7 -> -1;
			case 8 -> -1;
			case 9 -> -1;
			case 10 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 11 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 12 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 13 -> -1;
			case 14 -> -1;
			case 15 -> -1;
			case 16 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 17 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 18 -> -1;
			case 19 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 20 -> -1;
			case 21 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 22 -> -1;
			case 23 -> -1;
			case 24 -> -1;
			case 25 -> -1;
			case 26 -> -1;
			case 27 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 28 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 29 -> -1;
			case 30 -> -1;
			case 31 -> -1;
			case 32 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 33 -> -1;
			case 34 -> -1;
			case 35 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 36 -> -1;
			case 37 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 38 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 39 -> -1;
			case 40 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 41 -> -1;
			case 42 -> GLFW.GLFW_KEY_LEFT_ALT;
			case 43 -> -1;
			default -> -1;
		};
	}
}
