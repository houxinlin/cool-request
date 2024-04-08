/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RMIFactory.java is part of Cool Request
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

package com.cool.request.rmi;

import com.cool.request.rmi.starter.ICoolRequestStarterRMI;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class RMIFactory {
    public static ICoolRequestStarterRMI getStarterRMI(int port) {
        return (ICoolRequestStarterRMI) getRMIInstance(port, ICoolRequestStarterRMI.class.getName());
    }

    public static Remote getRMIInstance(int port, String name) {
        try {
            return Naming.lookup("rmi://localhost:" + port + "/" + name);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
