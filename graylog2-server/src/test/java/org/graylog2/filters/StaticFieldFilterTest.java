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
package org.graylog2.filters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.graylog2.inputs.Input;
import org.graylog2.inputs.InputService;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class StaticFieldFilterTest {

    @Test
    public void testFilter() throws Exception {
        Message msg = new Message("hello", "junit", Tools.iso8601());
        msg.setSourceInputId("someid");

        final InputService inputService = mock(InputService.class);
        final Input input = mock(Input.class);
        when(inputService.find(eq("someid"))).thenReturn(input);
        when(inputService.getStaticFields(eq(input)))
                     .thenReturn(Lists.newArrayList(Maps.immutableEntry("foo", "bar")));
        
        final StaticFieldFilter filter = new StaticFieldFilter(inputService);
        filter.filter(msg);

        assertEquals("hello", msg.getMessage());
        assertEquals("junit", msg.getSource());
        assertEquals("bar", msg.getField("foo"));
    }

    @Test
    public void testFilterIsNotOverwritingExistingKeys() throws Exception {
        Message msg = new Message("hello", "junit", Tools.iso8601());
        msg.addField("foo", "IWILLSURVIVE");
        
        final InputService inputService = mock(InputService.class);
        final Input input = mock(Input.class);
        when(inputService.find(eq("someid"))).thenReturn(input);
        when(inputService.getStaticFields(eq(input)))
                .thenReturn(Lists.newArrayList(Maps.immutableEntry("foo", "bar")));

        final StaticFieldFilter filter = new StaticFieldFilter(inputService);
        filter.filter(msg);

        assertEquals("hello", msg.getMessage());
        assertEquals("junit", msg.getSource());
        assertEquals("IWILLSURVIVE", msg.getField("foo"));
    }

}
