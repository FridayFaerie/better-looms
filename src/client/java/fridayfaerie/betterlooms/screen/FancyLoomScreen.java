package fridayfaerie.betterlooms.screen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.model.BannerFlagBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BannerPatternTags;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FancyLoomScreen extends HandledScreen<LoomScreenHandler> {
    private static final Identifier BANNER_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/banner");
    private static final Identifier DYE_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/dye");
    private static final Identifier PATTERN_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/banner_pattern");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/loom/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/loom/scroller_disabled");
    private static final Identifier PATTERN_SELECTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_selected");
    private static final Identifier PATTERN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_highlighted");
    private static final Identifier PATTERN_TEXTURE = Identifier.ofVanilla("container/loom/pattern");
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/loom/error");
    private static final Identifier TEXTURE = Identifier.of("better-looms", "textures/gui/fancy_loom_gui.png");
    private static final int PATTERN_LIST_COLUMNS = 4;
    private static final int PATTERN_LIST_ROWS = 4;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int PATTERN_ENTRY_SIZE = 14;
    private static final int SCROLLBAR_AREA_HEIGHT = 56;
    private static final int PATTERN_LIST_OFFSET_X = 60;
    private static final int PATTERN_LIST_OFFSET_Y = 13;
    private static final float field_59943 = 64.0F;
    private static final float field_59944 = 21.0F;
    private static final float field_59945 = 40.0F;
    private BannerFlagBlockModel bannerField;
    @Nullable
    private BannerPatternsComponent bannerPatterns;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int visibleTopRow;
    private DyeColor selectedDye = DyeColor.WHITE;
    private List<RegistryEntry.Reference<BannerPattern>> allPatterns = List.of();


    public FancyLoomScreen(LoomScreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
        screenHandler.setInventoryChangeListener(this::onInventoryChanged);
        this.titleY -= 2;
    }

    @Override
    protected void init() {
        super.init();

        var networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler != null) {
            var registry = networkHandler
                    .getRegistryManager()
                    .getOptional(RegistryKeys.BANNER_PATTERN);

            registry.ifPresent(r ->
                    allPatterns = r.streamEntries().toList()
            );
        }


        this.x = 152;
        this.y = 2;
        this.backgroundWidth = 176;
        this.backgroundHeight = 206;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 94;

        ModelPart modelPart = this.client.getLoadedEntityModels().getModelPart(EntityModelLayers.STANDING_BANNER_FLAG);
        this.bannerField = new BannerFlagBlockModel(modelPart);


        int buttonX = this.x + 32;
        int buttonY = this.y + 13;

        int index = 0;
        for (DyeColor dyeColor : DyeColor.values()) {
            final int x = buttonX + (index % 2) * 13;
            final int y = buttonY + (index / 2) * 13;
            index++;

            this.addDrawableChild(
                    ButtonWidget.builder(Text.empty(), button -> {
                        int dyeSlot = findDyeSlot(dyeColor);
                        if (dyeSlot == -1) return;

                        // Move dye into loom slot 1
                        swapSlots(dyeSlot, 1);

                        selectedDye = dyeColor;
                    }).dimensions(x, y, 13, 13).build()
            );
        }


    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private int getRows() {
        return MathHelper.ceilDiv(this.handler.getBannerPatterns().size(), 4);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth+30, this.backgroundHeight, 256, 256);

        Slot slot = this.handler.getBannerSlot();
        Slot slot2 = this.handler.getDyeSlot();
        Slot slot3 = this.handler.getPatternSlot();
        Slot slot4 = this.handler.getOutputSlot();
        if (!slot.hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BANNER_SLOT_TEXTURE, i + slot.x, j + slot.y, 16, 16);
        }

        if (!slot2.hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DYE_SLOT_TEXTURE, i + slot2.x, j + slot2.y, 16, 16);
        }

        if (!slot3.hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PATTERN_SLOT_TEXTURE, i + slot3.x, j + slot3.y, 16, 16);
        }

        int k = (int)(41.0F * this.scrollPosition);
        Identifier identifier = this.canApplyDyePattern ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i + 119, j + 13 + k, 12, 15);
        if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
             DyeColor dyeColor = ((BannerItem)slot4.getStack().getItem()).getColor();
//            DyeColor dyeColor = selectedDye;
            int l = i + 141;
            int m = j + 8;
            context.addBannerResult(this.bannerField, dyeColor, this.bannerPatterns, l, m, l + 20, m + 40);
        } else if (this.hasTooManyPatterns) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + slot4.x - 5, j + slot4.y - 5, 26, 26);
        }

        if (this.canApplyDyePattern) {
            int n = i + 60;
            int l = j + 13;
            List<RegistryEntry.Reference<BannerPattern>> list = this.allPatterns;
//            List<RegistryEntry<BannerPattern>> list = this.allPatterns;
//            List<RegistryEntry<BannerPattern>> list = this.handler.getBannerPatterns();

            label64:
            for (int o = 0; o < 7; o++) {
                for (int p = 0; p < 4; p++) {
                    int q = o + this.visibleTopRow;
                    int r = q * 4 + p;
                    if (r >= list.size()) {
                        break label64;
                    }

                    int s = n + p * 14;
                    int t = l + o * 14;
                    RegistryEntry<BannerPattern> registryEntry = (RegistryEntry<BannerPattern>)list.get(r);
                    boolean bl = mouseX >= s && mouseY >= t && mouseX < s + 14 && mouseY < t + 14;
                    Identifier identifier2;
                    if (r == this.handler.getSelectedPattern()) {
                        identifier2 = PATTERN_SELECTED_TEXTURE;
                    } else if (bl) {
                        identifier2 = PATTERN_HIGHLIGHTED_TEXTURE;
                        DyeColor dyeColor2 = ((DyeItem)this.dye.getItem()).getColor();
                        context.drawTooltip(Text.translatable(((BannerPattern)registryEntry.value()).translationKey() + "." + dyeColor2.getId()), mouseX, mouseY);
                    } else {
                        identifier2 = PATTERN_TEXTURE;
                    }

                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, s, t, 14, 14);
                    Sprite sprite = context.getSprite(TexturedRenderLayers.getBannerPatternTextureId(registryEntry));
                    this.drawBanner(context, s, t, sprite);
                }
            }
        }

        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
    }

    private void drawBanner(DrawContext context, int x, int y, Sprite sprite) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x + 4, y + 2);
        float f = sprite.getMinU();
        float g = f + (sprite.getMaxU() - sprite.getMinU()) * 21.0F / 64.0F;
        float h = sprite.getMaxV() - sprite.getMinV();
        float i = sprite.getMinV() + h / 64.0F;
        float j = i + h * 40.0F / 64.0F;
        int k = 5;
        int l = 10;
        context.fill(0, 0, 5, 10, DyeColor.GRAY.getEntityColor());
        context.drawTexturedQuad(sprite.getAtlasId(), 0, 0, 5, 10, f, g, i, j);
        context.getMatrices().popMatrix();
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        this.scrollbarClicked = false;
        if (this.canApplyDyePattern) {
            int i = this.x + 60;
            int j = this.y + 13;

            for (int k = 0; k < 4; k++) {
                for (int l = 0; l < 4; l++) {
                    double d = click.x() - (i + l * 14);
                    double e = click.y() - (j + k * 14);
                    int m = k + this.visibleTopRow;
                    int n = m * 4 + l;
                    if (d >= 0.0 && e >= 0.0 && d < 14.0 && e < 14.0 && this.handler.onButtonClick(this.client.player, n)) {
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
//                        this.client.interactionManager.clickButton(this.handler.syncId, n);
                        return true;
                    }
                }
            }

            i = this.x + 119;
            j = this.y + 9;
            if (click.x() >= i && click.x() < i + 12 && click.y() >= j && click.y() < j + 56) {
                this.scrollbarClicked = true;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        int i = this.getRows() - 4;
        if (this.scrollbarClicked && this.canApplyDyePattern && i > 0) {
            int j = this.y + 13;
            int k = j + 56;
            this.scrollPosition = ((float)click.y() - j - 7.5F) / (k - j - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
            this.visibleTopRow = Math.max((int)(this.scrollPosition * i + 0.5), 0);
            return true;
        } else {
            return super.mouseDragged(click, offsetX, offsetY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        } else {
            int i = this.getRows() - 4;
            if (this.canApplyDyePattern && i > 0) {
                float f = (float)verticalAmount / i;
                this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0F, 1.0F);
                this.visibleTopRow = Math.max((int)(this.scrollPosition * i + 0.5F), 0);
            }

            return true;
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top) {
        return mouseX < left || mouseY < top || mouseX >= left + this.backgroundWidth || mouseY >= top + this.backgroundHeight;
    }

    private void onInventoryChanged() {
        ItemStack itemStack = this.handler.getOutputSlot().getStack();
        if (itemStack.isEmpty()) {
            this.bannerPatterns = null;
        } else {
            this.bannerPatterns = (BannerPatternsComponent)itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        }

        ItemStack itemStack2 = this.handler.getBannerSlot().getStack();
        ItemStack itemStack3 = this.handler.getDyeSlot().getStack();
        // if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof DyeItem dyeItem) {
        //     selectedDye = dyeItem.getColor();
        // }

        ItemStack itemStack4 = this.handler.getPatternSlot().getStack();
        BannerPatternsComponent bannerPatternsComponent = (BannerPatternsComponent)itemStack2.getOrDefault(
                DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT
        );
        this.hasTooManyPatterns = bannerPatternsComponent.layers().size() >= 6;
        if (this.hasTooManyPatterns) {
            this.bannerPatterns = null;
        }

        if (!ItemStack.areEqual(itemStack2, this.banner) || !ItemStack.areEqual(itemStack3, this.dye) || !ItemStack.areEqual(itemStack4, this.pattern)) {
            this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && !this.hasTooManyPatterns && !this.handler.getBannerPatterns().isEmpty();
        }

        if (this.visibleTopRow >= this.getRows()) {
            this.visibleTopRow = 0;
            this.scrollPosition = 0.0F;
        }

        this.banner = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }

    private int findDyeSlot(DyeColor dyeColor) {
        for (int i = 0; i < this.handler.slots.size(); i++) {
            Slot slot = this.handler.slots.get(i);

            // Only search player inventory slots
            if (slot.inventory == this.client.player.getInventory()) {
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof DyeItem dyeItem && dyeItem.getColor() == dyeColor) {
                    return slot.id;
                }
            }
        }
        return -1;
    }

    private boolean isHiddenSlot(Slot slot) {
        // Loom slots: banner = 0, dye = 1, pattern = 2
        return slot.id == 0 || slot.id == 1 || slot.id == 2;
    }

    private void swapSlots(int originSlot, int targetSlot) {
        this.client.interactionManager.clickSlot(
                this.handler.syncId,
                originSlot,
                0,
                SlotActionType.PICKUP,
                this.client.player
        );
        this.client.interactionManager.clickSlot(
                this.handler.syncId,
                targetSlot,
                0,
                SlotActionType.PICKUP,
                this.client.player
        );
        this.client.interactionManager.clickSlot(
                this.handler.syncId,
                originSlot,
                0,
                SlotActionType.PICKUP,
                this.client.player
        );
    }

}
