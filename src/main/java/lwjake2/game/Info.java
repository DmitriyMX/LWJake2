/*
 * Copyright (C) 1997-2001 Id Software, Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package lwjake2.game;

import lombok.extern.slf4j.Slf4j;
import lwjake2.Defines;

import java.util.StringTokenizer;

@Slf4j
public class Info {

    /**
     * Returns a value for a key from an info string. 
     */
    public static String Info_ValueForKey(String s, String key) {

        StringTokenizer tk = new StringTokenizer(s, "\\");

        while (tk.hasMoreTokens()) {
            String key1 = tk.nextToken();

            if (!tk.hasMoreTokens()) {
                log.warn("MISSING VALUE");
                return s;
            }
            String value1 = tk.nextToken();

            if (key.equals(key1))
                return value1;
        }

        return "";
    }

    /**
     * Sets a value for a key in the user info string.
     */
    public static String Info_SetValueForKey(String s, String key, String value) {

        if (value == null || value.length() == 0)
            return s;

        if (key.contains("\\") || value.contains("\\")) {
            log.warn("Can't use keys or values with a \\");
            return s;
        }

        if (key.contains(";")) {
            log.warn("Can't use keys or values with a semicolon");
            return s;
        }

        if (key.contains("\"") || value.contains("\"")) {
            log.warn("Can't use keys or values with a \"");
            return s;
        }

        if (key.length() > Defines.MAX_INFO_KEY - 1
                || value.length() > Defines.MAX_INFO_KEY - 1) {
            log.warn("Keys and values must be < 64 characters.");
            return s;
        }

        StringBuffer sb = new StringBuffer(Info_RemoveKey(s, key));

        if (sb.length() + 2 + key.length() + value.length() > Defines.MAX_INFO_STRING) {

            log.warn("Info string length exceeded");
            return s;
        }

        sb.append('\\').append(key).append('\\').append(value);

        return sb.toString();
    }

    /** 
     * Removes a key and value from an info string. 
     */
    public static String Info_RemoveKey(String s, String key) {

        StringBuffer sb = new StringBuffer(512);

        if (key.contains("\\")) {
            log.warn("Can't use a key with a \\");
            return s;
        }

        StringTokenizer tk = new StringTokenizer(s, "\\");

        while (tk.hasMoreTokens()) {
            String key1 = tk.nextToken();

            if (!tk.hasMoreTokens()) {
                log.warn("MISSING VALUE");
                return s;
            }
            String value1 = tk.nextToken();

            if (!key.equals(key1))
                sb.append('\\').append(key1).append('\\').append(value1);
        }

        return sb.toString();

    }

    /**
     * Some characters are illegal in info strings because they can mess up the
     * server's parsing.
     */
    public static boolean Info_Validate(String s) {
        return !((s.indexOf('"') != -1) || (s.indexOf(';') != -1));
    }

    private static String fillspaces = "                     ";

    public static void Print(String s) {

        StringBuffer sb = new StringBuffer(512);
        StringTokenizer tk = new StringTokenizer(s, "\\");

        while (tk.hasMoreTokens()) {

            String key1 = tk.nextToken();

            if (!tk.hasMoreTokens()) {
                log.warn("MISSING VALUE");
                return;
            }

            String value1 = tk.nextToken();

            sb.append(key1);

            int len = key1.length();

            if (len < 20) {
                sb.append(fillspaces.substring(len));
            }
            sb.append('=').append(value1).append('\n');
        }
        log.warn(sb.toString());
    }
}