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
public class IExp implements IValue
{
    public int min;
    public int max;

    public double exp;

    public IExp(int min, int max, double exp)
    {
        this.min = min;
        this.max = max;
        this.exp = exp;
    }

    @Override
    public Integer getValue(Random random)
    {
        return (int) Math.round(min + ((Math.pow(exp, random.nextDouble()) - 1.0) / (exp - 1.0)) * (max - min));
    }
}
