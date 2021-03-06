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

package org.apache.commons.math4.random;

import org.apache.commons.rng.RandomSource;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.NormalDistribution;
import org.apache.commons.math4.util.FastMath;


/**
 * Generate random vectors isotropically located on the surface of a sphere.
 *
 * @since 2.1
 */
public class UnitSphereRandomVectorGenerator
    implements RandomVectorGenerator {
    /**
     * Sampler used for generating the individual components of the vectors.
     */
    private final RealDistribution.Sampler rand;
    /**
     * Space dimension.
     */
    private final int dimension;

    /**
     * @param dimension Space dimension.
     * @param rng RNG for the individual components of the vectors.
     */
    public UnitSphereRandomVectorGenerator(final int dimension,
                                           final UniformRandomProvider rng) {
        this.dimension = dimension;
        this.rand = new NormalDistribution().createSampler(rng);
    }
    /**
     * Create an object that will use a {@link RandomSource#MT_64 default RNG},
     * in order to generate the individual components.
     *
     * @param dimension Space dimension.
     */
    public UnitSphereRandomVectorGenerator(final int dimension) {
        this(dimension, RandomSource.create(RandomSource.MT_64));
    }

    /** {@inheritDoc} */
    @Override
    public double[] nextVector() {
        final double[] v = new double[dimension];

        // See http://mathworld.wolfram.com/SpherePointPicking.html for example.
        // Pick a point by choosing a standard Gaussian for each element, and then
        // normalizing to unit length.
        double normSq = 0;
        for (int i = 0; i < dimension; i++) {
            final double comp = rand.sample();
            v[i] = comp;
            normSq += comp * comp;
        }

        final double f = 1 / FastMath.sqrt(normSq);
        for (int i = 0; i < dimension; i++) {
            v[i] *= f;
        }

        return v;
    }
}
