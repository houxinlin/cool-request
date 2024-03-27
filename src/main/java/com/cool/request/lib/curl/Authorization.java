/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Authorization.java is part of Cool Request
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

import java.io.Serializable;


/**
 * This class is an Authorization encapsulator.
 *
 */
public class Authorization  implements Serializable {

    private static final long serialVersionUID = 241L;

    private static final String URL = "Authorization.url"; // $NON-NLS-1$

    private static final String USERNAME = "Authorization.username"; // $NON-NLS-1$

    private static final String PASSWORD = "Authorization.password"; // $NON-NLS-1$ NOSONAR no hard coded password

    private static final String DOMAIN = "Authorization.domain"; // $NON-NLS-1$

    private static final String REALM = "Authorization.realm"; // $NON-NLS-1$

    private static final String MECHANISM = "Authorization.mechanism"; // $NON-NLS-1$

    private static final String TAB = "\t"; // $NON-NLS-1$

    /**
     * create the authorization
     * @param url url for which the authorization should be considered
     * @param user name of the user
     * @param pass password for the user
     * @param domain authorization domain (used for NTML-authentication)
     * @param realm authorization realm
     * @param mechanism authorization mechanism, that should be used
     */
    Authorization(String url, String user, String pass, String domain, String realm, AuthManager.Mechanism mechanism) {
        setURL(url);

    }

    public boolean expectsModification() {
        return false;
    }

    public Authorization() {
        this("","","","","", AuthManager.Mechanism.BASIC);
    }




    public void setURL(String url) {
    }


    public void setUser(String trim) {

    }

    public void setPass(String trim) {

    }

    public void setMechanism(AuthManager.Mechanism mechanism) {


    }
}