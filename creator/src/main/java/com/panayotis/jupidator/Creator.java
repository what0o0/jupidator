/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.parsables.ParseFolder;
import java.io.File;

/**
 *
 * @author teras
 */
public class Creator {

    /**
     * @param args the command line arguments
     */
    public static void main(String... args) {
        ParseFolder result = new ParseFolder(new File("/Applications/CrossMobile.app"));
        ParseFolder prod = new ParseFolder(result.toJSON());
        System.out.println(result.equals(prod));
    }
}