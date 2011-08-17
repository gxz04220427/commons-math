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
package org.apache.commons.math.geometry.euclidean.threed;

import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.geometry.Vector;
import org.apache.commons.math.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math.geometry.partitioning.Embedding;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/** The class represent lines in a three dimensional space.

 * <p>Each oriented line is intrinsically associated with an abscissa
 * wich is a coordinate on the line. The point at abscissa 0 is the
 * orthogonal projection of the origin on the line, another equivalent
 * way to express this is to say that it is the point of the line
 * which is closest to the origin. Abscissa increases in the line
 * direction.</p>

 * @version $Id$
 * @since 3.0
 */
public class Line implements Embedding<Euclidean3D, Euclidean1D> {

    /** Line direction. */
    private Vector3D direction;

    /** Line point closest to the origin. */
    private Vector3D zero;

    /** Build a line from a point and a direction.
     * @param p point belonging to the line (this can be any point)
     * @param direction direction of the line
     * @exception MathArithmeticException if the direction norm is too small
     */
    public Line(final Vector3D p, final Vector3D direction) {
        reset(p, direction);
    }

    /** Copy constructor.
     * <p>The created instance is completely independent from the
     * original instance, it is a deep copy.</p>
     * @param line line to copy
     */
    public Line(final Line line) {
        this.direction = line.direction;
        this.zero      = line.zero;
    }

    /** Reset the instance as if built from a point and a normal.
     * @param p point belonging to the line (this can be any point)
     * @param dir direction of the line
     * @exception MathArithmeticException if the direction norm is too small
     */
    public void reset(final Vector3D p, final Vector3D dir) {
        final double norm = dir.getNorm();
        if (norm == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }
        this.direction = new Vector3D(1.0 / norm, dir);
        zero = new Vector3D(1.0, p, -p.dotProduct(this.direction), this.direction);
    }

    /** Get a line with reversed direction.
     * @return a new instance, with reversed direction
     */
    public Line revert() {
        return new Line(zero, direction.negate());
    }

    /** Get the normalized direction vector.
     * @return normalized direction vector
     */
    public Vector3D getDirection() {
        return direction;
    }

    /** Get the line point closest to the origin.
     * @return line point closest to the origin
     */
    public Vector3D getOrigin() {
        return zero;
    }

    /** Get the abscissa of a point with respect to the line.
     * <p>The abscissa is 0 if the projection of the point and the
     * projection of the frame origin on the line are the same
     * point.</p>
     * @param point point to check
     * @return abscissa of the point
     */
    public double getAbscissa(final Vector3D point) {
        return point.subtract(zero).dotProduct(direction);
    }

    /** Get one point from the line.
     * @param point desired abscissa for the point
     * @return one point belonging to the line, at specified abscissa
     */
    public Vector3D pointAt(final double abscissa) {
        return new Vector3D(1.0, zero, abscissa, direction);
    }

    /** {@inheritDoc}
     * @see #getAbscissa(Vector3D)
     */
    public Vector1D toSubSpace(final Vector<Euclidean3D> point) {
        return new Vector1D(getAbscissa((Vector3D) point));
    }

    /** {@inheritDoc}
     * @see #pointAt(double)
     */
    public Vector3D toSpace(final Vector<Euclidean1D> point) {
        return pointAt(((Vector1D) point).getX());
    }

    /** Check if the instance is similar to another line.
     * <p>Lines are considered similar if they contain the same
     * points. This does not mean they are equal since they can have
     * opposite directions.</p>
     * @param line line to which instance should be compared
     * @return true if the lines are similar
     */
    public boolean isSimilarTo(final Line line) {
        final double angle = Vector3D.angle(direction, line.direction);
        return ((angle < 1.0e-10) || (angle > (FastMath.PI - 1.0e-10))) && contains(line.zero);
    }

    /** Check if the instance contains a point.
     * @param p point to check
     * @return true if p belongs to the line
     */
    public boolean contains(final Vector3D p) {
        return distance(p) < 1.0e-10;
    }

    /** Compute the distance between the instance and a point.
     * @param p to check
     * @return distance between the instance and the point
     */
    public double distance(final Vector3D p) {
        final Vector3D d = p.subtract(zero);
        final Vector3D n = new Vector3D(1.0, d, -d.dotProduct(direction), direction);
        return n.getNorm();
    }

    /** Compute the shortest distance between the instance and another line.
     * @param line line to check against the instance
     * @return shortest distance between the instance and the line
     */
    public double distance(final Line line) {

        final Vector3D normal = Vector3D.crossProduct(direction, line.direction);
        final double n = normal.getNorm();
        if (n < MathUtils.SAFE_MIN) {
            // lines are parallel
            return distance(line.zero);
        }

        // signed separation of the two parallel planes that contains the lines
        final double offset = line.zero.subtract(zero).dotProduct(normal) / n;

        return FastMath.abs(offset);

    }

    /** Compute the point of the instance closest to another line.
     * @param line line to check against the instance
     * @return point of the instance closest to another line
     */
    public Vector3D closestPoint(final Line line) {

        final double cos = direction.dotProduct(line.direction);
        final double n = 1 - cos * cos;
        if (n < MathUtils.EPSILON) {
            // the lines are parallel
            return zero;
        }

        final Vector3D delta0 = line.zero.subtract(zero);
        final double a        = delta0.dotProduct(direction);
        final double b        = delta0.dotProduct(line.direction);

        return new Vector3D(1, zero, (a - b * cos) / n, direction);

    }

    /** Get the intersection point of the instance and another line.
     * @param line other line
     * @return intersection point of the instance and the other line
     * or null if there are no intersection points
     */
    public Vector3D intersection(final Line line) {
        final Vector3D closest = closestPoint(line);
        return line.contains(closest) ? closest : null;
    }

}