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
package org.graylog2.rest.resources.tools;

import com.codahale.metrics.annotation.Timed;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.graylog2.inputs.extractors.SplitAndIndexExtractor;
import org.graylog2.shared.rest.resources.RestResource;
import org.graylog2.rest.resources.tools.responses.SplitAndIndexTesterResponse;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Path("/tools/split_and_index_tester")
public class SplitAndIndexTesterResource extends RestResource {
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public SplitAndIndexTesterResponse splitAndIndexTester(@QueryParam("split_by") @NotNull String splitBy,
                                                           @QueryParam("index") @Min(0) int index,
                                                           @QueryParam("string") @NotNull String string) {
        final String cut = SplitAndIndexExtractor.cut(string, splitBy, index - 1);
        int[] positions = SplitAndIndexExtractor.getCutIndices(string, splitBy, index - 1);

        return SplitAndIndexTesterResponse.create((cut != null), cut, positions[0], positions[1]);
    }

}
