/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TimingEventListener.java is part of Cool Request
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
 * This interface is implemented by any object wishing to receive events from a
 * {@link TimingSource} object. The TimingEventListener would be added as a
 * listener to the TimingSource object via the {@link
 * TimingSource#addEventListener(TimingEventListener)} method.
 * <p>
 * This functionality is handled automatically inside of {@link Animator}. To
 * use a non-default TimingSource object for Animator, simply call
 * {@link Animator#setTimer(TimingSource)} and the appropriate listeners
 * will be set up internally.
 *
 * @author Chet
 */
public interface TimingEventListener {

    /**
     * This method is called by the {@link TimingSource} object while the
     * timer is running.
     *
     * @param timingSource the object that generates the timing events.
     */
    public void timingSourceEvent(TimingSource timingSource);

}