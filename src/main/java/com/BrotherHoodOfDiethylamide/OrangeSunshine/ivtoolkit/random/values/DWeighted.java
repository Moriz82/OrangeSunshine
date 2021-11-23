/*
 * Copyright 2016 Lukas Tenbrink
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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.random.values;

import java.util.Random;

/**
 * Created by lukas on 04.04.14.
 */
public class DWeighted implements DValue
{
    public double[] values;
    public int[] weights;

    public DWeighted(double[] values, int[] weights)
    {
        this.values = values;
        this.weights = weights;
    }

    public DWeighted(Number... valuesWithWeights)
    {
        this.values = new double[valuesWithWeights.length / 2];
        this.weights = new int[values.length];

        for (int i = 0; i < values.length; i++)
        {
            values[i] = (Double) valuesWithWeights[i * 2];
            weights[i] = (Integer) valuesWithWeights[i * 2 + 1];
        }
    }

    @Override
    public Double getValue(Random random)
    {
        int total = getTotalWeight(weights);
        int selected = random.nextInt(total);

        for (int i = 0; i < weights.length; i++)
        {
            selected -= weights[i];
            if (selected < 0)
            {
                return values[i];
            }
        }

        throw new RuntimeException("Weights have invalid values!");
    }

    public static int getTotalWeight(int[] weights)
    {
        int weight = 0;

        for (int i : weights)
        {
            weight += i;
        }

        return weight;
    }
}
