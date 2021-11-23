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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.bezier;


import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.math.IvVecMathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IvBezierPath3D
{
    private List<IvBezierPoint3D> bezierPoints;

    private List<Double> cachedDistances;
    private double cachedFullDistance;
    private List<Double> cachedProgresses;

    private boolean isDirty;

    public IvBezierPath3D()
    {
        cachedProgresses = new ArrayList<>();
        cachedDistances = new ArrayList<>();

        setBezierPoints(new ArrayList<IvBezierPoint3D>());
    }

    public IvBezierPath3D(List<IvBezierPoint3D> bezierPoints)
    {
        cachedProgresses = new ArrayList<>();
        cachedDistances = new ArrayList<>();

        setBezierPoints(bezierPoints);
    }

    public void buildDistances()
    {
        isDirty = false;

        cachedFullDistance = 0.0;
        cachedDistances.clear();
        cachedProgresses.clear();

        IvBezierPoint3D previousPoint = null;
        for (IvBezierPoint3D bezierPoint : bezierPoints)
        {
            if (previousPoint != null)
            {
                double distance = 0.0;

                int samples = 50;
                for (int i = 0; i < samples; i++)
                {
                    double[] bezierFrom = previousPoint.getBezierDirectionPointFrom();
                    double[] bezierTo = bezierPoint.getBezierDirectionPointTo();

                    double[] position = IvVecMathHelper.cubicMix(previousPoint.position, bezierFrom, bezierTo, bezierPoint.position, (double) i / (double) samples);
                    double[] position1 = IvVecMathHelper.cubicMix(previousPoint.position, bezierFrom, bezierTo, bezierPoint.position, (double) (i + 1) / (double) samples);

                    distance += IvVecMathHelper.distance(position, position1);
                }

                cachedFullDistance += distance;
                cachedDistances.add(distance);
            }

            previousPoint = bezierPoint;
        }

        cachedProgresses.addAll(cachedDistances.stream().map(d -> d / cachedFullDistance).collect(Collectors.toList()));
    }

    public double getPathLengthInRange(int startIndex, int endIndex)
    {
        double maxProgress = 0.0;

        int arraySize = cachedProgresses.size();
        for (int i = startIndex; i < endIndex; i++)
        {
            maxProgress += cachedProgresses.get(i % arraySize);
        }

        return maxProgress;
    }

    public double getPathLength()
    {
        return cachedFullDistance;
    }

    private IvBezierPoint3DCachedStep getCachedStep(int leftIndex, int rightIndex, double leftProgress, double rightProgress, double innerProgress)
    {
        return new IvBezierPoint3DCachedStep(bezierPoints.get(leftIndex), leftIndex, bezierPoints.get(rightIndex), rightIndex, leftProgress, rightProgress, innerProgress);
    }

    public IvBezierPoint3DCachedStep getCachedStep(int leftIndex, int rightIndex, double innerProgress)
    {
        double leftProgress = getPathLengthInRange(0, leftIndex);
        double rightProgress = getPathLengthInRange(0, rightIndex);
        return getCachedStep(leftIndex, rightIndex, leftProgress, rightProgress, innerProgress);
    }

    public IvBezierPoint3DCachedStep getCachedStep(double progress)
    {
        progress = ((progress % 1.0) + 1.0) % 1.0;
        double curProgress = 0.0;

        for (int i = 1; i < bezierPoints.size(); i++)
        {
            double distance = cachedProgresses.get(i - 1);

            if ((progress - distance) <= 0.0)
            {
                return getCachedStep(i - 1, i, curProgress, curProgress + distance, progress / distance);
            }

            progress -= distance;
            curProgress += distance;
        }

        int bezierPointsLength = bezierPoints.size();
        return getCachedStep(bezierPointsLength - 2, bezierPointsLength - 1, 1.0f);
    }

    public IvBezierPoint3DCachedStep getCachedStepAfterStep(IvBezierPoint3DCachedStep cachedStep, double stepSize)
    {
        return getCachedStep(cachedStep.getBezierPathProgress() + stepSize);
    }

    public double[] getMotion(IvBezierPoint3DCachedStep cachedStep, IvBezierPoint3DCachedStep cachedStep1)
    {
        return IvVecMathHelper.sub(cachedStep1.getPosition(), cachedStep.getPosition());
    }

    public double[] getPVector(IvBezierPoint3DCachedStep cachedStep, double stepSize)
    {
        double[] motion1 = getMotion(cachedStep, getCachedStepAfterStep(cachedStep, stepSize * 0.3));
        double[] motion2 = getMotion(cachedStep, getCachedStepAfterStep(cachedStep, stepSize * 0.6));

//        IvBezierPoint3DCachedStep nextStep = getCachedStep(nextPositionProgress);
//        double[] motionNext1 = getMotion(nextStep, getCachedStep(nextPositionProgress + stepSize * 0.3));
//        double[] motionNext2 = getMotion(nextStep, getCachedStep(nextPositionProgress + stepSize * 0.6));
//
//        double[] pVector = IvMathHelper.crossProduct(IvMathHelper.mix(motion1, motionNext1, cachedStep.progress), IvMathHelper.mix(motion2, motionNext2, cachedStep.progress));
        double[] pVector = IvVecMathHelper.crossProduct(motion1, motion2);

        return pVector;
    }

    public double[] getNaturalRotation(IvBezierPoint3DCachedStep cachedStep, double stepSize)
    {
        double[] motion1 = getMotion(cachedStep, getCachedStepAfterStep(cachedStep, stepSize * 0.3));
        double[] spherical = IvVecMathHelper.sphericalFromCartesian(motion1);

        return new double[]{-spherical[0] / Math.PI * 180.0, spherical[1] / Math.PI * 180.0 + 90.0};
    }

    public void markDirty()
    {
        isDirty = true;
    }

    public boolean isDirty()
    {
        return isDirty;
    }

    public void setBezierPoints(List<IvBezierPoint3D> points)
    {
        this.bezierPoints = new ArrayList<>();
        this.bezierPoints.addAll(points);

        markDirty();
    }

    public List<IvBezierPoint3D> getBezierPoints()
    {
        List<IvBezierPoint3D> l = new ArrayList<>(bezierPoints.size());
        l.addAll(bezierPoints);
        return l;
    }

    public void removeBezierPoint(IvBezierPoint3D point)
    {
        bezierPoints.remove(point);
        markDirty();
    }

    public void addBezierPoint(IvBezierPoint3D point)
    {
        bezierPoints.add(point);
        markDirty();
    }

    public void addBezierPoints(List<IvBezierPoint3D> points)
    {
        bezierPoints.addAll(points);
        markDirty();
    }
}
