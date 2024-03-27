/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * LinearInterpolator.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.widget.btn;

/**
 * This class implements the Interpolator interface by providing a
 * simple interpolate function that simply returns the value that
 * it was given. The net effect is that callers will end up calculating
 * values linearly during intervals.
 * <p>
 * Because there is no variation to this class, it is a singleton and
 * is referenced by using the {@link #getInstance} static method.
 *
 * @author Chet
 */
public final class LinearInterpolator implements Interpolator {

    private static LinearInterpolator instance = null;

    private LinearInterpolator() {}

    /**
     * Returns the single DiscreteInterpolator object
     */
    public static LinearInterpolator getInstance() {
        if (instance == null) {
            instance = new LinearInterpolator();
        }
        return instance;
    }

    /**
     * This method always returns the value it was given, which will cause
     * callers to calculate a linear interpolation between boundary values.
     * @param fraction a value between 0 and 1, representing the elapsed
     * fraction of a time interval (either an entire animation cycle or an
     * interval between two KeyTimes, depending on where this Interpolator has
     * been set)
     * @return the same value passed in as <code>fraction</code>
     */
    public float interpolate(float fraction) {
        return fraction;
    }

}