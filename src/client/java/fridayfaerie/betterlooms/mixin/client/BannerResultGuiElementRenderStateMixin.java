package fridayfaerie.betterlooms.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BannerResultGuiElementRenderState.class)
public abstract class BannerResultGuiElementRenderStateMixin {

    @ModifyReturnValue(method = "scale", at = @At("RETURN"))
    private float loomBiggerScale(float original) {
        return 40.0F;
    }
}