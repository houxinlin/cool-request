package com.cool.request.lib.curl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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