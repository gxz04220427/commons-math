/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.geometry.euclidean.twod;

import java.util.List;

import org.apache.commons.math.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math.geometry.partitioning.RegionFactory;
import org.junit.Assert;
import org.junit.Test;

public class SubLineTest {

    @Test
    public void testEndPoints() {
        SubLine sub = new SubLine(new Vector2D(-1, -7), new Vector2D(7, -1));
        List<Vector2D[]> segments = sub.getSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals(-1, segments.get(0)[0].getX(), 1.0e-10);
        Assert.assertEquals(-7, segments.get(0)[0].getY(), 1.0e-10);
        Assert.assertEquals( 7, segments.get(0)[1].getX(), 1.0e-10);
        Assert.assertEquals(-1, segments.get(0)[1].getY(), 1.0e-10);
    }

    @Test
    public void testNoEndPoints() {
        SubLine wholeLine = new Line(new Vector2D(-1, 7), new Vector2D(7, 1)).wholeHyperplane();
        List<Vector2D[]> segments = wholeLine.getSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertTrue(Double.isInfinite(segments.get(0)[0].getX()) &&
                          segments.get(0)[0].getX() < 0);
        Assert.assertTrue(Double.isInfinite(segments.get(0)[0].getY()) &&
                          segments.get(0)[0].getY() > 0);
        Assert.assertTrue(Double.isInfinite(segments.get(0)[1].getX()) &&
                          segments.get(0)[1].getX() > 0);
        Assert.assertTrue(Double.isInfinite(segments.get(0)[1].getY()) &&
                          segments.get(0)[1].getY() < 0);
    }

    @Test
    public void testNoSegments() {
        SubLine empty = new SubLine(new Line(new Vector2D(-1, -7), new Vector2D(7, -1)),
                                    new RegionFactory<Euclidean1D>().getComplement(new IntervalsSet()));
        List<Vector2D[]> segments = empty.getSegments();
        Assert.assertEquals(0, segments.size());
    }

    @Test
    public void testSeveralSegments() {
        SubLine twoSubs = new SubLine(new Line(new Vector2D(-1, -7), new Vector2D(7, -1)),
                                    new RegionFactory<Euclidean1D>().union(new IntervalsSet(1, 2),
                                                                           new IntervalsSet(3, 4)));
        List<Vector2D[]> segments = twoSubs.getSegments();
        Assert.assertEquals(2, segments.size());
    }

    @Test
    public void testHalfInfiniteNeg() {
        SubLine empty = new SubLine(new Line(new Vector2D(-1, -7), new Vector2D(7, -1)),
                                    new IntervalsSet(Double.NEGATIVE_INFINITY, 0.0));
        List<Vector2D[]> segments = empty.getSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertTrue(Double.isInfinite(segments.get(0)[0].getX()) &&
                          segments.get(0)[0].getX() < 0);
        Assert.assertTrue(Double.isInfinite(segments.get(0)[0].getY()) &&
                          segments.get(0)[0].getY() < 0);
        Assert.assertEquals( 3, segments.get(0)[1].getX(), 1.0e-10);
        Assert.assertEquals(-4, segments.get(0)[1].getY(), 1.0e-10);
    }

    @Test
    public void testHalfInfinitePos() {
        SubLine empty = new SubLine(new Line(new Vector2D(-1, -7), new Vector2D(7, -1)),
                                    new IntervalsSet(0.0, Double.POSITIVE_INFINITY));
        List<Vector2D[]> segments = empty.getSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals( 3, segments.get(0)[0].getX(), 1.0e-10);
        Assert.assertEquals(-4, segments.get(0)[0].getY(), 1.0e-10);
        Assert.assertTrue(Double.isInfinite(segments.get(0)[1].getX()) &&
                          segments.get(0)[1].getX() > 0);
        Assert.assertTrue(Double.isInfinite(segments.get(0)[1].getY()) &&
                          segments.get(0)[1].getY() > 0);
    }

}