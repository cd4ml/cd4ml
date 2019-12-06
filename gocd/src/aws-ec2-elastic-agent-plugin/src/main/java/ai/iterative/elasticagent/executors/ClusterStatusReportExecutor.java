package ai.iterative.elasticagent.executors;

import ai.iterative.elasticagent.models.StatusReport;
import ai.iterative.elasticagent.requests.ClusterStatusReportRequest;
import ai.iterative.elasticagent.views.ViewBuilder;
import ai.iterative.elasticagent.AgentInstances;
import ai.iterative.elasticagent.RequestExecutor;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static ai.iterative.elasticagent.AwsEC2Plugin.LOG;

public class ClusterStatusReportExecutor implements RequestExecutor {

    private final ClusterStatusReportRequest clusterStatusReportRequest;
    private final AgentInstances agentInstances;
    private final ViewBuilder viewBuilder;

    public ClusterStatusReportExecutor(ClusterStatusReportRequest clusterStatusReportRequest, AgentInstances agentInstances, ViewBuilder viewBuilder) {
        this.clusterStatusReportRequest = clusterStatusReportRequest;
        this.agentInstances = agentInstances;
        this.viewBuilder = viewBuilder;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        LOG.info("[status-report] Generating status report");

        StatusReport statusReport = agentInstances.getStatusReport(clusterStatusReportRequest.getClusterProfile());

        final String statusReportView = viewBuilder.build("status-report.template", statusReport);

        JsonObject responseJSON = new JsonObject();
        responseJSON.addProperty("view", statusReportView);

        return DefaultGoPluginApiResponse.success(responseJSON.toString());
    }
}