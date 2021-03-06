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
package org.graylog2.configuration;

import com.github.joschi.jadconfig.JadConfig;
import com.github.joschi.jadconfig.RepositoryException;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.repositories.InMemoryRepository;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ElasticsearchConfigurationTest {
    @Test
    public void testGetElasticSearchIndexPrefix() throws RepositoryException, ValidationException {
        ElasticsearchConfiguration configuration = new ElasticsearchConfiguration();
        new JadConfig(new InMemoryRepository(), configuration).process();

        assertEquals(configuration.getIndexPrefix(), "graylog2");
    }

    @Test
    public void testGetPathData() throws ValidationException, RepositoryException {
        final ElasticsearchConfiguration configuration = new ElasticsearchConfiguration();
        new JadConfig(new InMemoryRepository(), configuration).process();

        assertEquals(configuration.getPathData(), "data/elasticsearch");
    }

    @Test
    public void testIsClientNode() throws ValidationException, RepositoryException {
        final Map<String, String> props = new HashMap<>();
        final ElasticsearchConfiguration configuration1 = new ElasticsearchConfiguration();

        new JadConfig(new InMemoryRepository(), configuration1).process();

        assertTrue(configuration1.isClientNode());

        final ElasticsearchConfiguration configuration2 = new ElasticsearchConfiguration();
        props.put("elasticsearch_node_data", "false");
        new JadConfig(new InMemoryRepository(props), configuration2).process();

        assertTrue(configuration2.isClientNode());

        final ElasticsearchConfiguration configuration3 = new ElasticsearchConfiguration();
        props.put("elasticsearch_node_data", "true");
        new JadConfig(new InMemoryRepository(props), configuration3).process();

        assertFalse(configuration3.isClientNode());
    }
}
