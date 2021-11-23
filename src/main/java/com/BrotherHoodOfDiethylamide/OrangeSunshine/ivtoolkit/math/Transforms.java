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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.math;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.util.IvStreams;

import javax.annotation.Nonnull;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lukas on 15.09.16.
 */
public class Transforms
{
    public static Stream<AxisAlignedTransform2D> transformStream(IntPredicate rotate, IntPredicate mirror)
    {
        return IvStreams.flatMapToObj(rotationStream(rotate), r -> mirrorStream(mirror).mapToObj(m -> AxisAlignedTransform2D.from(r, m != 0)));
    }

    public static Stream<AxisAlignedTransform2D> transformStream(boolean rotate, boolean mirror)
    {
        return IvStreams.flatMapToObj(rotationStream(rotate), r -> mirrorStream(mirror).mapToObj(m -> AxisAlignedTransform2D.from(r, m != 0)));
    }

    @Nonnull
    protected static IntStream mirrorStream(boolean mirror)
    {
        return IntStream.of(mirror ? new int[]{0, 1} : new int[1]);
    }

    @Nonnull
    protected static IntStream rotationStream(boolean rotate)
    {
        return IntStream.of(rotations(rotate));
    }

    @Nonnull
    protected static IntStream mirrorStream(IntPredicate predicate)
    {
        return IntStream.of(new int[]{0, 1}).filter(predicate);
    }

    @Nonnull
    protected static IntStream rotationStream(IntPredicate predicate)
    {
        return IntStream.of(rotations()).filter(predicate);
    }

    protected static int[] rotations(boolean rotate)
    {
        return rotate ? rotations() : new int[1];
    }

    public static int[] rotations()
    {
        return new int[]{0, 1, 2, 3};
    }

    public static boolean[] mirrors(boolean mirror)
    {
        return mirror ? mirrors() : new boolean[1];
    }

    public static boolean[] mirrors()
    {
        return new boolean[]{true, false};
    }

    public static AxisAlignedTransform2D apply(AxisAlignedTransform2D first, AxisAlignedTransform2D second)
    {
        // If the first mirrored, we have to invert its rotations' direction
        return AxisAlignedTransform2D.from(first.getRotation() * (second.isMirrorX() ? -1 : 1) + second.getRotation()
                , first.isMirrorX() != second.isMirrorX());
    }
}
