package fridayfaerie.betterlooms;

import fridayfaerie.betterlooms.config.BetterLoomsConfig;
import fridayfaerie.betterlooms.screen.FancyLoomScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

public class BetterLoomsClient implements ClientModInitializer {
	public static BetterLoomsConfig CONFIG;


	public static final Map<FancyLoomScreen.ColorOrPattern, KeyBinding> KEY_BINDINGS = new EnumMap<>(FancyLoomScreen.ColorOrPattern.class);
	public static final Map<FancyLoomScreen.ColorOrPattern, KeyBinding> CTRL_BINDINGS = new EnumMap<>(FancyLoomScreen.ColorOrPattern.class);
	public static final Map<FancyLoomScreen.ColorOrPattern, KeyBinding> SHIFT_BINDINGS = new EnumMap<>(FancyLoomScreen.ColorOrPattern.class);
	public static final Map<FancyLoomScreen.ColorOrPattern, KeyBinding> ALT_BINDINGS = new EnumMap<>(FancyLoomScreen.ColorOrPattern.class);


	public static KeyBinding binding1;
	KeyBinding.Category bannerKeyboardCategory = KeyBinding.Category.create(Identifier.of("better-looms", "banner_keyboard_category"));


	@Override
	public void onInitializeClient() {
		AutoConfig.register(BetterLoomsConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(BetterLoomsConfig.class).getConfig();

		for (FancyLoomScreen.ColorOrPattern action : FancyLoomScreen.ColorOrPattern.values()){
			String id = "key.better-looms." + action.name().toLowerCase();
			int defaultKey = defaultKeys(action);
			int defaultCtrl = defaultCtrls(action);
			int defaultShift = defaultShifts(action);
			int defaultAlt = defaultAlts(action);

			KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
					id, InputUtil.Type.KEYSYM, defaultKey, bannerKeyboardCategory
			));
			KeyBinding ctrlBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
					id+"_ctrl", InputUtil.Type.KEYSYM, defaultCtrl, bannerKeyboardCategory
			));
			KeyBinding shiftBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
					id+"_shift", InputUtil.Type.KEYSYM, defaultShift, bannerKeyboardCategory
			));
			KeyBinding altBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
					id+"_alt", InputUtil.Type.KEYSYM, defaultAlt, bannerKeyboardCategory
			));
			KEY_BINDINGS.put(action,keyBinding);
			CTRL_BINDINGS.put(action,ctrlBinding);
			SHIFT_BINDINGS.put(action,shiftBinding);
			ALT_BINDINGS.put(action,altBinding);
		}
	}

	private static int defaultKeys(FancyLoomScreen.ColorOrPattern action) {
		return switch (action) {
			case COLOR_01 -> GLFW.GLFW_KEY_U;
			case COLOR_02 -> GLFW.GLFW_KEY_I;
			case COLOR_03 -> GLFW.GLFW_KEY_O;
			case COLOR_04 -> GLFW.GLFW_KEY_P;
			case COLOR_05 -> GLFW.GLFW_KEY_LEFT_BRACKET;
			case COLOR_06 -> GLFW.GLFW_KEY_RIGHT_BRACKET;
			case COLOR_07 -> GLFW.GLFW_KEY_H;
			case COLOR_08 -> GLFW.GLFW_KEY_J;
			case COLOR_09 -> GLFW.GLFW_KEY_K;
			case COLOR_10 -> GLFW.GLFW_KEY_L;
			case COLOR_11 -> GLFW.GLFW_KEY_SEMICOLON;
			case COLOR_12 -> GLFW.GLFW_KEY_N;
			case COLOR_13 -> GLFW.GLFW_KEY_M;
			case COLOR_14 -> GLFW.GLFW_KEY_COMMA;
			case COLOR_15 -> GLFW.GLFW_KEY_PERIOD;
			case COLOR_16 -> GLFW.GLFW_KEY_SLASH;
			case PATTERN_01-> GLFW.GLFW_KEY_Q;
			case PATTERN_02-> GLFW.GLFW_KEY_W;
			case PATTERN_03-> GLFW.GLFW_KEY_B;
			case PATTERN_04-> GLFW.GLFW_KEY_V;
			case PATTERN_05-> GLFW.GLFW_KEY_Z;
			case PATTERN_06 -> GLFW.GLFW_KEY_D;
			case PATTERN_07 -> GLFW.GLFW_KEY_W;
			case PATTERN_08 -> GLFW.GLFW_KEY_G;
			case PATTERN_09 -> GLFW.GLFW_KEY_G;
			case PATTERN_10 -> GLFW.GLFW_KEY_G;
			case PATTERN_11 -> GLFW.GLFW_KEY_G;
			case PATTERN_12 -> GLFW.GLFW_KEY_V;
			case PATTERN_13 -> GLFW.GLFW_KEY_X;
			case PATTERN_14 -> GLFW.GLFW_KEY_X;
			case PATTERN_15 -> GLFW.GLFW_KEY_Y;
			case PATTERN_16 -> GLFW.GLFW_KEY_Y;
			case PATTERN_17 -> GLFW.GLFW_KEY_V;
			case PATTERN_18 -> GLFW.GLFW_KEY_E;
			case PATTERN_19 -> GLFW.GLFW_KEY_E;
			case PATTERN_20 -> GLFW.GLFW_KEY_E;
			case PATTERN_21 -> GLFW.GLFW_KEY_E;
			case PATTERN_22 -> GLFW.GLFW_KEY_C;
			case PATTERN_23 -> GLFW.GLFW_KEY_C;
			case PATTERN_24 -> GLFW.GLFW_KEY_V;
			case PATTERN_25 -> GLFW.GLFW_KEY_Z;
			case PATTERN_26 -> GLFW.GLFW_KEY_D;
			case PATTERN_27 -> GLFW.GLFW_KEY_F;
			case PATTERN_28 -> GLFW.GLFW_KEY_F;
			case PATTERN_29 -> GLFW.GLFW_KEY_F;
			case PATTERN_30 -> GLFW.GLFW_KEY_F;
			case PATTERN_31 -> GLFW.GLFW_KEY_D;
			case PATTERN_32 -> GLFW.GLFW_KEY_R;
			case PATTERN_33 -> GLFW.GLFW_KEY_T;
			case PATTERN_34 -> GLFW.GLFW_KEY_T;
			case PATTERN_35 -> GLFW.GLFW_KEY_T;
			case PATTERN_36 -> GLFW.GLFW_KEY_R;
			case PATTERN_37 -> GLFW.GLFW_KEY_T;
			case PATTERN_38 -> GLFW.GLFW_KEY_R;
			case PATTERN_39 -> GLFW.GLFW_KEY_R;
			case PATTERN_40 -> GLFW.GLFW_KEY_S;
			case PATTERN_41 -> GLFW.GLFW_KEY_S;
			case PATTERN_42 -> GLFW.GLFW_KEY_A;
			case PATTERN_43 -> GLFW.GLFW_KEY_A;
		};
	}
	private static int defaultCtrls(FancyLoomScreen.ColorOrPattern action) {
		return switch (action) {
			case COLOR_01 -> -1;
			case COLOR_02 -> -1;
			case COLOR_03 -> -1;
			case COLOR_04 -> -1;
			case COLOR_05 -> -1;
			case COLOR_06 -> -1;
			case COLOR_07 -> -1;
			case COLOR_08 -> -1;
			case COLOR_09 -> -1;
			case COLOR_10 -> -1;
			case COLOR_11 -> -1;
			case COLOR_12 -> -1;
			case COLOR_13 -> -1;
			case COLOR_14 -> -1;
			case COLOR_15 -> -1;
			case COLOR_16 -> -1;
			case PATTERN_01 -> -1;
			case PATTERN_02 -> -1;
			case PATTERN_03 -> -1;
			case PATTERN_04 -> -1;
			case PATTERN_05 -> -1;
			case PATTERN_06 -> -1;
			case PATTERN_07 -> -1;
			case PATTERN_08 -> -1;
			case PATTERN_09 -> -1;
			case PATTERN_10 -> -1;
			case PATTERN_11 -> -1;
			case PATTERN_12 -> -1;
			case PATTERN_13 -> -1;
			case PATTERN_14 -> -1;
			case PATTERN_15 -> -1;
			case PATTERN_16 -> -1;
			case PATTERN_17 -> -1;
			case PATTERN_18 -> -1;
			case PATTERN_19 -> -1;
			case PATTERN_20 -> -1;
			case PATTERN_21 -> -1;
			case PATTERN_22 -> -1;
			case PATTERN_23 -> -1;
			case PATTERN_24 -> -1;
			case PATTERN_25 -> -1;
			case PATTERN_26 -> -1;
			case PATTERN_27 -> -1;
			case PATTERN_28 -> -1;
			case PATTERN_29 -> -1;
			case PATTERN_30 -> -1;
			case PATTERN_31 -> -1;
			case PATTERN_32 -> -1;
			case PATTERN_33 -> -1;
			case PATTERN_34 -> -1;
			case PATTERN_35 -> -1;
			case PATTERN_36 -> -1;
			case PATTERN_37 -> -1;
			case PATTERN_38 -> -1;
			case PATTERN_39 -> -1;
			case PATTERN_40 -> -1;
			case PATTERN_41 -> -1;
			case PATTERN_42 -> -1;
			case PATTERN_43 -> -1;
        };
	}
	private static int defaultShifts(FancyLoomScreen.ColorOrPattern action) {
		return switch (action) {
			case COLOR_01 -> -1;
			case COLOR_02 -> -1;
			case COLOR_03 -> -1;
			case COLOR_04 -> -1;
			case COLOR_05 -> -1;
			case COLOR_06 -> -1;
			case COLOR_07 -> -1;
			case COLOR_08 -> -1;
			case COLOR_09 -> -1;
			case COLOR_10 -> -1;
			case COLOR_11 -> -1;
			case COLOR_12 -> -1;
			case COLOR_13 -> -1;
			case COLOR_14 -> -1;
			case COLOR_15 -> -1;
			case COLOR_16 -> -1;
			case PATTERN_01 -> -1;
			case PATTERN_02 -> -1;
			case PATTERN_03 -> -1;
			case PATTERN_04 -> -1;
			case PATTERN_05 -> -1;
			case PATTERN_06 -> -1;
			case PATTERN_07 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_08 -> -1;
			case PATTERN_09 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_10 -> -1;
			case PATTERN_11 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_12 -> -1;
			case PATTERN_13 -> -1;
			case PATTERN_14 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_15 -> -1;
			case PATTERN_16 -> -1;
			case PATTERN_17 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_18 -> -1;
			case PATTERN_19 -> -1;
			case PATTERN_20 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_21 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_22 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_23 -> -1;
			case PATTERN_24 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_25 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_26 -> -1;
			case PATTERN_27 -> -1;
			case PATTERN_28 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_29 -> -1;
			case PATTERN_30 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_31 -> -1;
			case PATTERN_32 -> -1;
			case PATTERN_33 -> -1;
			case PATTERN_34 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_35 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_36 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_37 -> -1;
			case PATTERN_38 -> GLFW.GLFW_KEY_LEFT_SHIFT;
			case PATTERN_39 -> -1;
			case PATTERN_40 -> -1;
			case PATTERN_41 -> -1;
			case PATTERN_42 -> -1;
			case PATTERN_43 -> -1;
		};
	}	private static int defaultAlts(FancyLoomScreen.ColorOrPattern action) {
		return switch (action) {
			case COLOR_01 -> -1;
			case COLOR_02 -> -1;
			case COLOR_03 -> -1;
			case COLOR_04 -> -1;
			case COLOR_05 -> -1;
			case COLOR_06 -> -1;
			case COLOR_07 -> -1;
			case COLOR_08 -> -1;
			case COLOR_09 -> -1;
			case COLOR_10 -> -1;
			case COLOR_11 -> -1;
			case COLOR_12 -> -1;
			case COLOR_13 -> -1;
			case COLOR_14 -> -1;
			case COLOR_15 -> -1;
			case COLOR_16 -> -1;
			case PATTERN_01 -> -1;
			case PATTERN_02 -> -1;
			case PATTERN_03 -> -1;
			case PATTERN_04 -> -1;
			case PATTERN_05 -> -1;
			case PATTERN_06 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_07 -> -1;
			case PATTERN_08 -> -1;
			case PATTERN_09 -> -1;
			case PATTERN_10 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_11 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_12 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_13 -> -1;
			case PATTERN_14 -> -1;
			case PATTERN_15 -> -1;
			case PATTERN_16 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_17 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_18 -> -1;
			case PATTERN_19 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_20 -> -1;
			case PATTERN_21 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_22 -> -1;
			case PATTERN_23 -> -1;
			case PATTERN_24 -> -1;
			case PATTERN_25 -> -1;
			case PATTERN_26 -> -1;
			case PATTERN_27 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_28 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_29 -> -1;
			case PATTERN_30 -> -1;
			case PATTERN_31 -> -1;
			case PATTERN_32 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_33 -> -1;
			case PATTERN_34 -> -1;
			case PATTERN_35 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_36 -> -1;
			case PATTERN_37 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_38 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_39 -> -1;
			case PATTERN_40 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_41 -> -1;
			case PATTERN_42 -> GLFW.GLFW_KEY_LEFT_ALT;
			case PATTERN_43 -> -1;
		};
	}
}
