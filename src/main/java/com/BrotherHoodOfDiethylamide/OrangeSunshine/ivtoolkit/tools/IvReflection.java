/*
 * Copyright 2019 Lukas Tenbrink
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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.tools;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class IvReflection
{
    public static Field findField(Class<?> clazz, String srg)
    {
        try {
            /*String remapped = ObfuscationReflectionHelper.fie(srg);
*/
            Field f = clazz.getDeclaredField(srg);
            f.setAccessible(true);
            return f;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
