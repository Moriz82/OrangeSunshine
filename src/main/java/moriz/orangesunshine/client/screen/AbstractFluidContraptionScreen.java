package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.client.render.FluidBoxRenderer;
import moriz.orangesunshine.client.render.RenderUtil;
import moriz.orangesunshine.fluid.*;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 13.11.14.
 * Updated by Sollace on 4 Jan 2023
 */
public abstract class AbstractFluidContraptionScreen<T extends FluidContraptionScreenHandler<?>> extends AbstractContainerScreen<T> {
    protected final T handler;

    protected AbstractFluidContraptionScreen(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.handler = handler;
        titleLabelY = 10;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        titleLabelX = 83 - font.width(title) / 2;
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    protected Resovoir getTank() {
        return handler.getTank();
    }

    public void drawTank(GuiGraphics context, Resovoir tank, int x, int y, int width, int height) {
        if (tank.isEmpty()) {
            return;
        }

        SimpleFluid fluid = tank.getFluidType();
        int level = tank.getLevel();

        float fluidHeight = Mth.clamp((float) level / (float) tank.getCapacity(), 0, 1);
        int fluidHeightPixels = Mth.ceil(fluidHeight * height);

        FluidBoxRenderer.FluidAppearance appearance = FluidBoxRenderer.FluidAppearance.of(fluid, tank.getStack());

        float[] color = appearance.rgba();

        RenderUtil.drawRepeatingSprite(context, appearance.sprite(), x, y - fluidHeightPixels, width, fluidHeightPixels, color[0], color[1], color[2], 1);
    }

    public void drawTankTooltip(GuiGraphics context, Resovoir tank, int x, int y, int width, int height, int mouseX, int mouseY, List<Component> details) {
        if (rectContains(mouseX, mouseY, x, y, width, height)) {
            SimpleFluid fluid = tank.getFluidType();
            int level = tank.getLevel();

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(fluid.getName(tank.getStack()));
            if (!fluid.isEmpty()) {
                tooltip.add(Component.literal("Amount: " + level).withStyle(ChatFormatting.GRAY));
            }
            fluid.appendTooltip(tank.getStack(), null, tooltip, Item.TooltipContext.EMPTY);
            tooltip.addAll(details);
            context.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
        }
    }

    public static boolean rectContains(int x, int y, int rectX, int rectY, int width, int height) {
        return x >= rectX
            && y >= rectY
            && x < rectX + width
            && y < rectY + height;
    }
}
