/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 28.05.14.
 */
public class GuiSliderMultivalue extends GuiButton
{
    private float[] values;
    public int mousePressedInsideIndex = -1;
    private List<GuiControlListener<GuiSliderMultivalue>> listeners = new ArrayList<>();
    private float minValue = 0.0f;
    private float maxValue = 1.0f;

    public GuiSliderMultivalue(int id, int x, int y, int width, int height, int values, String displayString)
    {
        super(id, x, y, width, height, displayString);
        this.values = new float[values];
    }

    @Override
    public int getHoverState(boolean mouseHovering)
    {
        return 0;
    }

    @Override
    public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_)
    {
        if (this.visible)
        {
            if (this.mousePressedInsideIndex >= 0)
            {
                values[mousePressedInsideIndex] = (float) (x - (this.x + 4)) / (float) (this.width - 8);
                values[mousePressedInsideIndex] = (values[mousePressedInsideIndex]) * (maxValue - minValue) + minValue;

                if (values[mousePressedInsideIndex] < minValue)
                {
                    values[mousePressedInsideIndex] = minValue;
                }

                if (values[mousePressedInsideIndex] > maxValue)
                {
                    values[mousePressedInsideIndex] = maxValue;
                }

                notifyOfChanges();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            for (float value : values)
            {
                float drawVal = (value - this.minValue) / (this.maxValue - this.minValue);
                this.drawTexturedModalRect(this.x + (int) (drawVal * (float) (this.width - 8)), this.y, 0, 66, 4, height);
                this.drawTexturedModalRect(this.x + (int) (drawVal * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, height);
            }
        }
    }

    @Override
    protected void mouseDragged(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_)
    {
        float value = (float) (x - (this.x + 4)) / (float) (this.width - 8);
        value = value * (maxValue - minValue) + minValue;

        float nearestDist = -1;
        for (int i = 0; i < values.length; i++)
        {
            float dist = Math.abs(values[i] - value);

            if (dist < nearestDist || nearestDist < 0)
            {
                mousePressedInsideIndex = i;
                nearestDist = dist;
            }
        }

        values[mousePressedInsideIndex] = value;

        if (values[mousePressedInsideIndex] < minValue)
        {
            values[mousePressedInsideIndex] = minValue;
        }

        if (values[mousePressedInsideIndex] > maxValue)
        {
            values[mousePressedInsideIndex] = maxValue;
        }

        notifyOfChanges();
    }

    private void notifyOfChanges()
    {
        for (GuiControlListener<GuiSliderMultivalue> listener : listeners)
        {
            listener.valueChanged(this);
        }
    }

    public float getValue(int index)
    {
        return this.values[index];
    }

    public void setValue(int index, float value)
    {
        this.values[index] = value;
    }

    public float getMinValue()
    {
        return minValue;
    }

    public void setMinValue(float minValue)
    {
        this.minValue = minValue;
    }

    public float getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(float maxValue)
    {
        this.maxValue = maxValue;
    }

    public void addListener(GuiControlListener<GuiSliderMultivalue> listener)
    {
        listeners.add(listener);
    }

    public void removeListener(GuiControlListener<GuiSliderMultivalue> listener)
    {
        listeners.remove(listener);
    }

    public List<GuiControlListener<GuiSliderMultivalue>> listeners()
    {
        return Collections.unmodifiableList(listeners);
    }
}