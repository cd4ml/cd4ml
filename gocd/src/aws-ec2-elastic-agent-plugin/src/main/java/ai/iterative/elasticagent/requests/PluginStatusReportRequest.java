package ai.iterative.elasticagent.requests;

import ai.iterative.elasticagent.views.ViewBuilder;
import ai.iterative.elasticagent.AgentInstances;
import ai.iterative.elasticagent.RequestExecutor;
import ai.iterative.elasticagent.executors.PluginStatusReportExecutor;

import java.util.Map;

public class PluginStatusReportRequest extends ServerPingRequest {
    public static PluginStatusReportRequest fromJSON(String json) {
        return (PluginStatusReportRequest) ServerPingRequest.fromJSON(json);
    }

    public RequestExecutor executor(Map<String, AgentInstances> clusterSpecificAgentInstances, ViewBuilder instance) {
        return new PluginStatusReportExecutor(this, clusterSpecificAgentInstances, instance);
    }
}
