/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TimingSource.java is part of Cool Request
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

import java.util.ArrayList;

/**
 * This class provides a generic wrapper for arbitrary
 * Timers that may be used with the Timing Framework.
 * Animator creates its own internal TimingSource by default,
 * but an Animator can be directed to use a different
 * TimingSource by calling {@link Animator#setTimer(TimingSource)}.
 *
 * The implementation details of any specific timer may
 * vary widely, but any timer should be able to expose
 * the basic capabilities used in this interface. Animator
 * depends on these capabilities for starting, stopping,
 * and running any TimingSource.
 *
 * The usage of an external TimingSource object for sending in timing
 * events to an Animator is to implement this interface appropriately,
 * pass in that object to {@link Animator#setTimer(TimingSource)},
 * which adds the Animator as a listener to the TimingSource object,
 * and then send in any later timing events from the object to the
 * protected method {@link #timingEvent()}, which will send these timing
 * events to all listeners.
 *
 * @author Chet
 */
public abstract class TimingSource {

    // listeners that will receive timing events
    private ArrayList<TimingEventListener> listeners =
            new ArrayList<TimingEventListener>();

    /**
     * Starts the TimingSource
     */
    public abstract void start();

    /**
     * Stops the TimingSource
     */
    public abstract void stop();

    /**
     * Sets the delay between callback events. This
     * will be called by Animator if its
     * {@link Animator#setResolution(int) setResolution(int)}
     * method is called. Note that the actual resolution may vary,
     * according to the resolution of the timer used by the framework as well
     * as system load and configuration; this value should be seen more as a
     * minimum resolution than a guaranteed resolution.
     * @param resolution delay, in milliseconds, between
     * each timing event callback.
     * @throws IllegalArgumentException resolution must be >= 0
     * @see Animator#setResolution(int)
     */
    public abstract void setResolution(int resolution);

    /**
     * Sets delay which should be observed by the
     * TimingSource after a call to {@link #start()}. Some timers may not be
     * able to adhere to specific resolution requests
     * @param delay delay, in milliseconds, to pause before
     * starting timing events.
     * @throws IllegalArgumentException resolution must be >= 0
     * @see Animator#setStartDelay(int)
     */
    public abstract void setStartDelay(int delay);

    /**
     * Adds a TimingEventListener to the set of listeners that
     * receive timing events from this TimingSource.
     * @param listener the listener to be added.
     */
    public final void addEventListener(TimingEventListener listener) {
        synchronized(listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    /**
     * Removes a TimingEventListener from the set of listeners that
     * receive timing events from this TimingSource.
     * @param listener the listener to be removed.
     */
    public final void removeEventListener(TimingEventListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }


    /**
     * Subclasses call this method to post timing events to this
     * object's {@link TimingEventListener} objects.
     */
    protected final void timingEvent() {
        synchronized(listeners) {
            for (TimingEventListener listener : listeners) {
                listener.timingSourceEvent(this);
            }
        }
    }
}