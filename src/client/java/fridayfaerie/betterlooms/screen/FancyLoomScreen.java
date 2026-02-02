package fridayfaerie.betterlooms.screen;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import fridayfaerie.betterlooms.BetterLooms;
import fridayfaerie.betterlooms.BetterLoomsClient;
import fridayfaerie.betterlooms.config.BetterLoomsConfig;
import fridayfaerie.betterlooms.mixin.client.SlotAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.KeyBinding;
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
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
    private static final int PATTERN_LIST_ROWS = 7;
    private static final int PATTERN_LIST_COLUMNS = 4;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int PATTERN_ENTRY_SIZE = 14;
    private static final int SCROLLBAR_AREA_HEIGHT = 98;
    private static final int COLOR_LIST_OFFSET_X = 8;
    private static final int COLOR_LIST_OFFSET_Y = 16;
    private static final int PATTERN_LIST_OFFSET_X = 41;
    private static final int PATTERN_LIST_OFFSET_Y = 19;
    private static final float field_59943 = 64.0F;
    private static final float field_59944 = 21.0F;
    private static final float field_59945 = 40.0F;
    private BannerFlagBlockModel bannerField;
    @Nullable
    private BannerPatternsComponent bannerPatterns;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int visibleTopRow;
    private DyeColor selectedDye = DyeColor.WHITE;
    private int selectedPattern = 0;
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

        this.backgroundWidth = 176;
        this.backgroundHeight = 217;
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
//        this.titleX = this.titleX;
        this.titleY = 6;
//        this.playerInventoryTitleX = this.playerInventoryTitleX;
        this.playerInventoryTitleY = this.backgroundHeight - 92;

        for (Slot slot : handler.slots) {
            SlotAccessor acc = (SlotAccessor) slot;

            switch (slot.id) {
                case 0 -> {
                    acc.setX(154);
                    acc.setY(7);
                }
                case 1 -> {
                    acc.setX(118);
                    acc.setY(7);
                }
                case 2 -> {
                    acc.setX(136);
                    acc.setY(7);
                }
                case 3 -> {
                    acc.setX(180);
                    acc.setY(0);
                }
                default -> {
//                    acc.setX(acc.getX());
                    acc.setY(acc.getY()+51);
                }
            }
        }


        ModelPart modelPart = this.client.getLoadedEntityModels().getModelPart(EntityModelLayers.STANDING_BANNER_FLAG);
        this.bannerField = new BannerFlagBlockModel(modelPart);


        int buttonX = this.x + COLOR_LIST_OFFSET_X;
        int buttonY = this.y + COLOR_LIST_OFFSET_Y;

        int index = 0;
        for (DyeColor dyeColor : DyeColor.values()) {
            final int x = buttonX + (index % 2) * 13;
            final int y = buttonY + (index / 2) * 13;
            index++;
            DyeButtonWidget dyeButton = new DyeButtonWidget(
                    x, y, dyeColor, () -> selectedDye == dyeColor,
                    button -> {
                        Slot slot2 = this.handler.getDyeSlot();
                        if (slot2.hasStack() && ((DyeItem)slot2.getStack().getItem()).getColor() == dyeColor) {
                            return;
                        };
                        selectDye(dyeColor);

                    }
            );
            this.addDrawableChild(dyeButton);
        }


    }
    private void selectDye(DyeColor dyeColor){
        selectedDye = dyeColor;
        int dyeSlot = findDyeSlot(dyeColor);
        if (dyeSlot != -1) {
            swapSlots(dyeSlot, 1);
        } else {
            this.client.interactionManager.clickSlot(this.handler.syncId,
                    1,0,
                    SlotActionType.QUICK_MOVE,this.client.player
            );
        }
    }
    private boolean selectPatternByIndex(int n) {
        RegistryEntry<BannerPattern> targetPattern = this.allPatterns.get(n);
        boolean inAvailable = this.handler.getBannerPatterns().contains(targetPattern);
        if (!inAvailable){
            Slot patternSlot = this.handler.getPatternSlot();
            if (!targetPattern.isIn(BannerPatternTags.NO_ITEM_REQUIRED)) {
                if (targetPattern.streamTags().toList().isEmpty()){
                    int itemSlot = findBannerSlot(selectedDye);
                    if (itemSlot!=-1) {
                        swapSlots(itemSlot, this.handler.getBannerSlot().id);
                        return true;
                    }
                } else {
                    int itemSlot = findPatternSlot(targetPattern);
                    if (itemSlot != -1) {
                        swapSlots(itemSlot, patternSlot.id);
                    } else {
                        return true;
                    }
                }
            } else {
                if (patternSlot.hasStack()){
                    this.client.interactionManager.clickSlot(this.handler.syncId,
                            patternSlot.id,0,
                            SlotActionType.QUICK_MOVE,this.client.player
                    );
                }
            }
        }
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
        this.selectedPattern = n;
        List<RegistryEntry<BannerPattern>> availablePatterns = this.handler.getBannerPatterns();
        int handlerIndex = IntStream.range(0, availablePatterns.size())
                .filter(variable -> availablePatterns.get(variable).equals(targetPattern))
                .findFirst().orElse(-1);
        this.client.interactionManager.clickButton(this.handler.syncId, handlerIndex);

        if (BetterLoomsClient.CONFIG.enableInstantcomplete) {
            this.client.interactionManager.clickSlot(
                    this.handler.syncId, 3, 0,
                    SlotActionType.PICKUP, this.client.player
            );
            this.client.interactionManager.clickSlot(
                    this.handler.syncId, 0, 0,
                    SlotActionType.QUICK_MOVE, this.client.player
            );
            this.client.interactionManager.clickSlot(
                    this.handler.syncId, 0, 0,
                    SlotActionType.PICKUP, this.client.player
            );
        }
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private int getRows() {
        return MathHelper.ceilDiv(this.allPatterns.size(), PATTERN_LIST_COLUMNS);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);

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

        int k = (int)(83.0F * this.scrollPosition);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, i + 100, j + 19 + k, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
        if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
            DyeColor dyeColor = ((BannerItem)slot.getStack().getItem()).getColor();
            int l = i + 118;
            int m = j + 28;
            context.addBannerResult(this.bannerField, dyeColor, this.bannerPatterns, l, m, l + 50, m + 100);
            if (slot.hasStack()) {
                this.bannerPatterns = (BannerPatternsComponent) slot.getStack().getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
            } else {
                this.bannerPatterns = null;
            }

        } else if (this.hasTooManyPatterns) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + slot4.x - 5, j + slot4.y - 5, 26, 26);
        }

        int n = i + PATTERN_LIST_OFFSET_X;
        int l = j + PATTERN_LIST_OFFSET_Y;
        List<RegistryEntry.Reference<BannerPattern>> list = this.allPatterns;


        label64:
        for (int o = 0; o < PATTERN_LIST_ROWS; o++) {
            for (int p = 0; p < PATTERN_LIST_COLUMNS; p++) {
                int q = o + this.visibleTopRow;
                int r = q * PATTERN_LIST_COLUMNS + p;
                if (r >= list.size()) {
                    break label64;
                }

                int s = n + p * PATTERN_ENTRY_SIZE;
                int t = l + o * PATTERN_ENTRY_SIZE;
                RegistryEntry<BannerPattern> registryEntry = (RegistryEntry<BannerPattern>)list.get(r);
                boolean bl = mouseX >= s && mouseY >= t && mouseX < s + PATTERN_ENTRY_SIZE && mouseY < t + PATTERN_ENTRY_SIZE;
                Identifier identifier2;
                if (r == this.selectedPattern) {
                    identifier2 = PATTERN_SELECTED_TEXTURE;
                } else if (bl) {
                    identifier2 = PATTERN_HIGHLIGHTED_TEXTURE;
                    DyeColor dyeColor2 = selectedDye;
                    context.drawTooltip(Text.translatable(((BannerPattern)registryEntry.value()).translationKey() + "." + dyeColor2.getId()), mouseX, mouseY);

                    if (slot.getStack().getItem() instanceof BannerItem) {
                        ItemStack preview = slot.getStack().copyWithCount(1);
                        preview.apply(
                                DataComponentTypes.BANNER_PATTERNS,
                                BannerPatternsComponent.DEFAULT,
                                component -> new BannerPatternsComponent.Builder().addAll(component).add(registryEntry, selectedDye).build()
                        );
                        this.bannerPatterns = (BannerPatternsComponent)preview.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
                    }
                } else {
                    identifier2 = PATTERN_TEXTURE;
                }

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, s, t, PATTERN_ENTRY_SIZE, PATTERN_ENTRY_SIZE);
                Sprite sprite = context.getSprite(TexturedRenderLayers.getBannerPatternTextureId(registryEntry));
                this.drawBanner(context, s, t, sprite);
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
        int i = this.x + PATTERN_LIST_OFFSET_X;
        int j = this.y + PATTERN_LIST_OFFSET_Y;

        for (int k = 0; k < PATTERN_LIST_ROWS; k++) {
            for (int l = 0; l < PATTERN_LIST_COLUMNS; l++) {
                double d = click.x() - (i + l * PATTERN_ENTRY_SIZE);
                double e = click.y() - (j + k * PATTERN_ENTRY_SIZE);
                int m = k + this.visibleTopRow;
                int n = m * PATTERN_LIST_COLUMNS + l;

                if (d >= 0.0 && e >= 0.0 && d < PATTERN_ENTRY_SIZE && e < PATTERN_ENTRY_SIZE && n < this.allPatterns.size()) {
                    return selectPatternByIndex(n);
                }
            }
        }

        i = this.x + 100;
        j = this.y + 19;
        if (click.x() >= i && click.x() < i + 12 && click.y() >= j && click.y() < j + SCROLLBAR_AREA_HEIGHT) {
          this.scrollbarClicked = true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        int i = this.getRows() - PATTERN_LIST_ROWS; // Number of rows not shown
        if (this.scrollbarClicked && i > 0) {
            int j = this.y + 18;
            int k = j + SCROLLBAR_AREA_HEIGHT;
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
            int i = this.getRows() - PATTERN_LIST_ROWS;
            if (i > 0) {
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

    @Override
    public boolean keyPressed(KeyInput input) {

        for (Map.Entry<ColorOrPattern, KeyBinding> entry : BetterLoomsClient.KEY_BINDINGS.entrySet()){
            KeyBinding keyBinding = entry.getValue();
            if (keyBinding != null && keyBinding.matchesKey(input)){
                ColorOrPattern action = entry.getKey();
                int modifierBinding = 0;
                if (!BetterLoomsClient.SHIFT_BINDINGS.get(action).getBoundKeyTranslationKey().equals("key.keyboard.unknown")) {
                    modifierBinding += 1;
                }
                if (!BetterLoomsClient.CTRL_BINDINGS.get(action).getBoundKeyTranslationKey().equals("key.keyboard.unknown")) {
                    modifierBinding += 2;
                }
                if (!BetterLoomsClient.ALT_BINDINGS.get(action).getBoundKeyTranslationKey().equals("key.keyboard.unknown")) {
                    modifierBinding += 4;
                }
                if (input.modifiers() == modifierBinding){
                    performAction(entry.getKey());
                }
            }
        }
        return super.keyPressed(input);
    }


    private void performAction(ColorOrPattern action) {
        if (ACTION_TO_COLOR.containsKey(action)) {
            selectDye(ACTION_TO_COLOR.get(action));
        }
        else if (ACTION_TO_PATTERN_INDEX.containsKey(action)) {
            BetterLooms.LOGGER.info("selected pattern: " + ACTION_TO_COLOR.get(action));
            selectPatternByIndex(ACTION_TO_PATTERN_INDEX.get(action));
        }
    }
    private static final Map<ColorOrPattern, DyeColor> ACTION_TO_COLOR = Map.ofEntries(
            Map.entry(ColorOrPattern.COLOR_1, DyeColor.WHITE),
            Map.entry(ColorOrPattern.COLOR_2, DyeColor.ORANGE),
            Map.entry(ColorOrPattern.COLOR_3, DyeColor.MAGENTA),
            Map.entry(ColorOrPattern.COLOR_4, DyeColor.LIGHT_BLUE),
            Map.entry(ColorOrPattern.COLOR_5, DyeColor.YELLOW),
            Map.entry(ColorOrPattern.COLOR_6, DyeColor.LIME),
            Map.entry(ColorOrPattern.COLOR_7, DyeColor.PINK),
            Map.entry(ColorOrPattern.COLOR_8, DyeColor.GRAY),
            Map.entry(ColorOrPattern.COLOR_9, DyeColor.LIGHT_GRAY),
            Map.entry(ColorOrPattern.COLOR_10, DyeColor.CYAN),
            Map.entry(ColorOrPattern.COLOR_11, DyeColor.PURPLE),
            Map.entry(ColorOrPattern.COLOR_12, DyeColor.BLUE),
            Map.entry(ColorOrPattern.COLOR_13, DyeColor.BROWN),
            Map.entry(ColorOrPattern.COLOR_14, DyeColor.GREEN),
            Map.entry(ColorOrPattern.COLOR_15, DyeColor.RED),
            Map.entry(ColorOrPattern.COLOR_16, DyeColor.BLACK)
    );

    private static final Map<ColorOrPattern, Integer> ACTION_TO_PATTERN_INDEX = Map.ofEntries(
            Map.entry(ColorOrPattern.PATTERN_1,  0),
            Map.entry(ColorOrPattern.PATTERN_2,  1),
            Map.entry(ColorOrPattern.PATTERN_3,  2),
            Map.entry(ColorOrPattern.PATTERN_4,  3),
            Map.entry(ColorOrPattern.PATTERN_5,  4),
            Map.entry(ColorOrPattern.PATTERN_6,  5),
            Map.entry(ColorOrPattern.PATTERN_7,  6),
            Map.entry(ColorOrPattern.PATTERN_8,  7),
            Map.entry(ColorOrPattern.PATTERN_9,  8),
            Map.entry(ColorOrPattern.PATTERN_10, 9),
            Map.entry(ColorOrPattern.PATTERN_11, 10),
            Map.entry(ColorOrPattern.PATTERN_12, 11),
            Map.entry(ColorOrPattern.PATTERN_13, 12),
            Map.entry(ColorOrPattern.PATTERN_14, 13),
            Map.entry(ColorOrPattern.PATTERN_15, 14),
            Map.entry(ColorOrPattern.PATTERN_16, 15),
            Map.entry(ColorOrPattern.PATTERN_17, 16),
            Map.entry(ColorOrPattern.PATTERN_18, 17),
            Map.entry(ColorOrPattern.PATTERN_19, 18),
            Map.entry(ColorOrPattern.PATTERN_20, 19),
            Map.entry(ColorOrPattern.PATTERN_21, 20),
            Map.entry(ColorOrPattern.PATTERN_22, 21),
            Map.entry(ColorOrPattern.PATTERN_23, 22),
            Map.entry(ColorOrPattern.PATTERN_24, 23),
            Map.entry(ColorOrPattern.PATTERN_25, 24),
            Map.entry(ColorOrPattern.PATTERN_26, 25),
            Map.entry(ColorOrPattern.PATTERN_27, 26),
            Map.entry(ColorOrPattern.PATTERN_28, 27),
            Map.entry(ColorOrPattern.PATTERN_29, 28),
            Map.entry(ColorOrPattern.PATTERN_30, 29),
            Map.entry(ColorOrPattern.PATTERN_31, 30),
            Map.entry(ColorOrPattern.PATTERN_32, 31),
            Map.entry(ColorOrPattern.PATTERN_33, 32),
            Map.entry(ColorOrPattern.PATTERN_34, 33),
            Map.entry(ColorOrPattern.PATTERN_35, 34),
            Map.entry(ColorOrPattern.PATTERN_36, 35),
            Map.entry(ColorOrPattern.PATTERN_37, 36),
            Map.entry(ColorOrPattern.PATTERN_38, 37),
            Map.entry(ColorOrPattern.PATTERN_39, 38),
            Map.entry(ColorOrPattern.PATTERN_40, 39),
            Map.entry(ColorOrPattern.PATTERN_41, 40),
            Map.entry(ColorOrPattern.PATTERN_42, 41),
            Map.entry(ColorOrPattern.PATTERN_43, 42)
    );


    private void onInventoryChanged() {
        ItemStack itemStack = this.handler.getBannerSlot().getStack();
        if (itemStack.isEmpty()) {
            this.bannerPatterns = null;
        } else {
            this.bannerPatterns = (BannerPatternsComponent)itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        }

        ItemStack itemStack2 = this.handler.getBannerSlot().getStack();
        ItemStack itemStack3 = this.handler.getDyeSlot().getStack();
         if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof DyeItem dyeItem) {
             selectedDye = dyeItem.getColor();
         }

        ItemStack itemStack4 = this.handler.getPatternSlot().getStack();
        BannerPatternsComponent bannerPatternsComponent = (BannerPatternsComponent)itemStack2.getOrDefault(
                DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT
        );
        this.hasTooManyPatterns = bannerPatternsComponent.layers().size() >= 6;
        if (this.hasTooManyPatterns) {
            this.bannerPatterns = null;
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
            if (slot.inventory == this.client.player.getInventory()) {
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof DyeItem dyeItem && dyeItem.getColor() == dyeColor) {
                    return slot.id;
                }
            }
        }
        return -1;
    }

    private void swapSlots(int originSlot, int targetSlot) {
        this.client.interactionManager.clickSlot(this.handler.syncId,
                originSlot,0,
                SlotActionType.PICKUP,this.client.player
        );
        this.client.interactionManager.clickSlot(this.handler.syncId,
                targetSlot,0,
                SlotActionType.PICKUP,this.client.player
        );
        this.client.interactionManager.clickSlot(this.handler.syncId,
                originSlot,0,
                SlotActionType.PICKUP,this.client.player
        );
    }

    private int findPatternSlot(RegistryEntry<BannerPattern> pattern) {
        for (int i = 0; i < this.handler.slots.size(); i++) {
            Slot slot = this.handler.slots.get(i);
            if (slot.inventory == this.client.player.getInventory()) {
                TagKey<BannerPattern> provides =
                        slot.getStack().get(DataComponentTypes.PROVIDES_BANNER_PATTERNS);
                if (provides !=null && pattern.isIn(provides)) {
                    return slot.id;
                }
            }
        }
        return -1;
    }
    private int findBannerSlot(DyeColor selectedDye) {
        for (int i = 0; i < this.handler.slots.size(); i++) {
            Slot slot = this.handler.slots.get(i);
            if (slot.inventory == this.client.player.getInventory()) {
                if (slot.getStack().getItem() instanceof BannerItem bannerItem
                        && bannerItem.getColor()==selectedDye
                        && slot.getStack().get(DataComponentTypes.BANNER_PATTERNS).layers().isEmpty()){
                    return slot.id;
                }
            }
        }
        return -1;
    }

    public enum ColorOrPattern {
        COLOR_1,
        COLOR_2,
        COLOR_3,
        COLOR_4,
        COLOR_5,
        COLOR_6,
        COLOR_7,
        COLOR_8,
        COLOR_9,
        COLOR_10,
        COLOR_11,
        COLOR_12,
        COLOR_13,
        COLOR_14,
        COLOR_15,
        COLOR_16,
        PATTERN_1,
        PATTERN_2,
        PATTERN_3,
        PATTERN_4,
        PATTERN_5,
        PATTERN_6,
        PATTERN_7,
        PATTERN_8,
        PATTERN_9,
        PATTERN_10,
        PATTERN_11,
        PATTERN_12,
        PATTERN_13,
        PATTERN_14,
        PATTERN_15,
        PATTERN_16,
        PATTERN_17,
        PATTERN_18,
        PATTERN_19,
        PATTERN_20,
        PATTERN_21,
        PATTERN_22,
        PATTERN_23,
        PATTERN_24,
        PATTERN_25,
        PATTERN_26,
        PATTERN_27,
        PATTERN_28,
        PATTERN_29,
        PATTERN_30,
        PATTERN_31,
        PATTERN_32,
        PATTERN_33,
        PATTERN_34,
        PATTERN_35,
        PATTERN_36,
        PATTERN_37,
        PATTERN_38,
        PATTERN_39,
        PATTERN_40,
        PATTERN_41,
        PATTERN_42,
        PATTERN_43,
    }

}
