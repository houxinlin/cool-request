/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CLOptionDescriptor.java is part of Cool Request
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

package com.cool.request.lib.curl;

/**
 * Basic class describing an type of option. Typically, one creates a static
 * array of <code>CLOptionDescriptor</code>s, and passes it to
 * {@link CLArgsParser#CLArgsParser(String[], CLOptionDescriptor[])}.
 *
 * @see CLArgsParser
 * @see CLUtil
 */
public final class CLOptionDescriptor {
    /** Flag to say that one argument is required */
    public static final int ARGUMENT_REQUIRED = 1 << 1;

    /** Flag to say that the argument is optional */
    public static final int ARGUMENT_OPTIONAL = 1 << 2;

    /** Flag to say this option does not take arguments */
    public static final int ARGUMENT_DISALLOWED = 1 << 3;

    /** Flag to say this option requires 2 arguments */
    public static final int ARGUMENTS_REQUIRED_2 = 1 << 4;

    /** Flag to say this option may be repeated on the command line */
    public static final int DUPLICATES_ALLOWED = 1 << 5;

    private final int id;

    private final int flags;

    private final String name;

    private final String description;

    private final int[] incompatible;

    /**
     * Constructor.
     *
     * @param name        the name/long option
     * @param flags       the flags
     * @param id          the id/character option
     * @param description description of option usage
     */
    public CLOptionDescriptor(final String name, final int flags, final int id, final String description) {

        checkFlags(flags);

        this.id = id;
        this.name = name;
        this.flags = flags;
        this.description = description;
        this.incompatible = ((flags & DUPLICATES_ALLOWED) != 0) ? new int[0] : new int[]{id};
    }


    /**
     * Constructor.
     *
     * @param name         the name/long option
     * @param flags        the flags
     * @param id           the id/character option
     * @param description  description of option usage
     * @param incompatible descriptors for incompatible options
     */
    public CLOptionDescriptor(final String name, final int flags, final int id, final String description,
                              final CLOptionDescriptor[] incompatible) {

        checkFlags(flags);

        this.id = id;
        this.name = name;
        this.flags = flags;
        this.description = description;

        this.incompatible = new int[incompatible.length];
        for (int i = 0; i < incompatible.length; i++) {
            this.incompatible[i] = incompatible[i].getId();
        }
    }

    private void checkFlags(final int flags) {
        int modeCount = 0;
        if ((ARGUMENT_REQUIRED & flags) == ARGUMENT_REQUIRED) {
            modeCount++;
        }
        if ((ARGUMENT_OPTIONAL & flags) == ARGUMENT_OPTIONAL) {
            modeCount++;
        }
        if ((ARGUMENT_DISALLOWED & flags) == ARGUMENT_DISALLOWED) {
            modeCount++;
        }
        if ((ARGUMENTS_REQUIRED_2 & flags) == ARGUMENTS_REQUIRED_2) {
            modeCount++;
        }

        if (0 == modeCount) {
            final String message = "No mode specified for option " + this;
            throw new IllegalStateException(message);
        } else if (1 != modeCount) {
            final String message = "Multiple modes specified for option " + this;
            throw new IllegalStateException(message);
        }
    }

    /**
     * Get the array of incompatible option ids.
     *
     * @return the array of incompatible option ids
     */
    final int[] getIncompatible() {
        return this.incompatible;
    }

    /**
     * Retrieve textual description.
     *
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Retrieve flags about option. Flags include details such as whether it
     * allows parameters etc.
     *
     * @return the flags
     */
    public final int getFlags() {
        return this.flags;
    }

    /**
     * Retrieve the id for option. The id is also the character if using single
     * character options.
     *
     * @return the id
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Retrieve name of option which is also text for long option.
     *
     * @return name/long option
     */
    public final String getName() {
        return this.name;
    }

    @Override
    public final String toString() {
        return "[OptionDescriptor " + name + ", " + id + ", " + flags + ", " + description + "]";
    }
}