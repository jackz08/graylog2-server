/**
 * This file is part of Graylog2.
 *
 * Graylog2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog2.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.shared.system.activities;

/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class Activity {

    String message;
    Class<?> caller;

    public Activity(Class<?> caller) {
        this.caller = caller;
    }

    public Activity(String content, Class<?> caller) {
        this.message = content;
        this.caller = caller;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Class<?> getCaller() {
        return caller;
    }

}
