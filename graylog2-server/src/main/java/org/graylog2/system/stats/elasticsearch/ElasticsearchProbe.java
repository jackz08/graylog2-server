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
package org.graylog2.system.stats.elasticsearch;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsNodes;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksRequest;
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.service.PendingClusterTask;
import org.elasticsearch.node.Node;
import org.graylog2.indexer.indices.Indices;

import javax.inject.Inject;
import java.util.List;

@Singleton
public class ElasticsearchProbe {
    private final Node node;
    private final Indices indices;

    @Inject
    public ElasticsearchProbe(Node node, Indices indices) {
        this.node = node;
        this.indices = indices;
    }

    public ElasticsearchStats elasticsearchStats() {
        final ClusterAdminClient adminClient = node.client().admin().cluster();

        final ClusterStatsResponse clusterStatsResponse = adminClient.clusterStats(new ClusterStatsRequest()).actionGet();
        final String clusterName = clusterStatsResponse.getClusterNameAsString();

        final ClusterStatsNodes clusterNodesStats = clusterStatsResponse.getNodesStats();
        final NodesStats nodesStats = NodesStats.create(
                clusterNodesStats.getCounts().getTotal(),
                clusterNodesStats.getCounts().getMasterOnly(),
                clusterNodesStats.getCounts().getDataOnly(),
                clusterNodesStats.getCounts().getMasterData(),
                clusterNodesStats.getCounts().getClient()
        );

        final IndicesStats indicesStats = IndicesStats.create(
                clusterStatsResponse.getIndicesStats().getIndexCount(),
                clusterStatsResponse.getIndicesStats().getStore().sizeInBytes(),
                clusterStatsResponse.getIndicesStats().getFieldData().getMemorySizeInBytes(),
                clusterStatsResponse.getIndicesStats().getIdCache().getMemorySizeInBytes()
        );

        final PendingClusterTasksResponse pendingClusterTasksResponse = adminClient.pendingClusterTasks(new PendingClusterTasksRequest()).actionGet();
        final int pendingTasksSize = pendingClusterTasksResponse.pendingTasks().size();
        final List<Long> pendingTasksTimeInQueue = Lists.newArrayListWithCapacity(pendingTasksSize);
        for (PendingClusterTask pendingClusterTask : pendingClusterTasksResponse) {
            pendingTasksTimeInQueue.add(pendingClusterTask.getTimeInQueueInMillis());
        }

        final ClusterHealthResponse clusterHealthResponse = adminClient.health(new ClusterHealthRequest(indices.allIndicesAlias())).actionGet();
        final ClusterHealth clusterHealth = ClusterHealth.create(
                clusterHealthResponse.getNumberOfNodes(),
                clusterHealthResponse.getNumberOfDataNodes(),
                clusterHealthResponse.getActiveShards(),
                clusterHealthResponse.getRelocatingShards(),
                clusterHealthResponse.getActivePrimaryShards(),
                clusterHealthResponse.getInitializingShards(),
                clusterHealthResponse.getUnassignedShards(),
                clusterHealthResponse.isTimedOut(),
                pendingTasksSize,
                pendingTasksTimeInQueue
        );

        return ElasticsearchStats.create(
                clusterName,
                clusterHealthResponse.getStatus(),
                clusterHealth,
                nodesStats,
                indicesStats);
    }
}
