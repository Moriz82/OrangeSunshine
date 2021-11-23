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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.asm;

import java.util.Hashtable;

/**
 * Created by lukas on 25.02.14.
 */
public class IvDevRemapper
{
    public static Hashtable<String, String> fakeMappings = new Hashtable<String, String>();

    private static boolean isSetUp;

    // TODO Read actual DEV files
    public static void setUp()
    {
        isSetUp = true;
    }

    public boolean isSetUp()
    {
        return isSetUp;
    }

    // Hurr fake and gay
    public static String getSRGName(String name)
    {
        String mapping = fakeMappings.get(name);

        return mapping != null ? mapping : name;
    }
}
