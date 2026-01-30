package fridayfaerie.betterlooms.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.function.BooleanSupplier;

public class DyeButtonWidget extends ButtonWidget {
    private final DyeColor dyeColor;
    private final BooleanSupplier isSelected;

    public DyeButtonWidget(
            int x, int y,
            DyeColor dyeColor,
            BooleanSupplier isSelected,
            PressAction onPress
    ) {
        super(
                x, y, 13, 13,
                Text.empty(),
                onPress,
                builder -> Text.empty());
        this.dyeColor = dyeColor;
        this.isSelected = isSelected;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);


        float brightness = this.isHovered() ? 1.15f : 1.0f;
        int rgb = dyeColor.getEntityColor();

        int r = Math.min(255, (int)(((rgb >> 16) & 0xFF) * brightness));
        int g = Math.min(255, (int)(((rgb >> 8) & 0xFF) * brightness));
        int b = Math.min(255, (int)((rgb & 0xFF) * brightness));

        int argb = 0xFF000000 | (r << 16) | (g << 8) | b;


        context.fill(
                this.getX() ,
                this.getY() ,
                this.getX() + this.getWidth(),
                this.getY() + this.getHeight(),
                argb
        );
        drawOutline(context,0xFF000000);
        if (isSelected.getAsBoolean()) {
            drawOutline(context, 0xFFFFFFFF);
        }

    }

    private void drawOutline(DrawContext context, int color) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        context.fill(x, y, x + w, y + 1, color);
        context.fill(x, y + h - 1, x + w, y + h, color);
        context.fill(x, y, x + 1, y + h, color);
        context.fill(x + w - 1, y, x + w, y + h, color);
    }

}
