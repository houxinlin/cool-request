/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TimingTargetAdapter.java is part of Cool Request
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
 * Implements the {@link TimingTarget} interface, providing stubs for all
 * TimingTarget methods.  Subclasses may extend this adapter rather than
 * implementing the TimingTarget interface if they only care about a
 * subset of the events that TimingTarget provides.  For example,
 * sequencing animations may only require monitoring the
 * {@link TimingTarget#end} method, so subclasses of this adapter
 * may ignore the other methods such as timingEvent.
 *
 * @author Chet
 */
public class TimingTargetAdapter implements TimingTarget {

    /**
     * This method will receive all of the timing events from an Animator
     * during an animation.  The fraction is the percent elapsed (0 to 1)
     * of the current animation cycle.
     * @param fraction the fraction of completion between the start and
     * end of the current cycle.  Note that on reversing cycles
     * ({@link Animator.Direction#BACKWARD}) the fraction decreases
     * from 1.0 to 0 on backwards-running cycles.  Note also that animations
     * with a duration of {@link Animator#INFINITE INFINITE} will call
     * timingEvent with an undefined value for fraction, since there is
     * no fraction that makes sense if the animation has no defined length.
     * @see Animator.Direction
     */
    public void timingEvent(float fraction) {}

    /**
     * Called when the Animator's animation begins.  This provides a chance
     * for targets to perform any setup required at animation start time.
     */
    public void begin() {}

    /**
     * Called when the Animator's animation ends
     */
    public void end() {}

    /**
     * Called when the Animator repeats the animation cycle
     */
    public void repeat() {}

}