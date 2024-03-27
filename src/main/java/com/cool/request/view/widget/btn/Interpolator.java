/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Interpolator.java is part of Cool Request
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
 * Interface that defines the single {@link #interpolate(float)} method.
 * This interface is implemented by built-in interpolators.
 * Applications may choose to implement
 * their own Interpolator to get custom interpolation behavior.
 *
 * @author Chet
 */
public interface Interpolator {

    /**
     * This function takes an input value between 0 and 1 and returns
     * another value, also between 0 and 1. The purpose of the function
     * is to define how time (represented as a (0-1) fraction of the
     * duration of an animation) is altered to derive different value
     * calculations during an animation.
     * @param fraction a value between 0 and 1, representing the elapsed
     * fraction of a time interval (either an entire animation cycle or an
     * interval between two KeyTimes, depending on where this Interpolator has
     * been set)
     * @return a value between 0 and 1.  Values outside of this boundary may
     * be clamped to the interval [0,1] and cause undefined results.
     */
    public float interpolate(float fraction);

}