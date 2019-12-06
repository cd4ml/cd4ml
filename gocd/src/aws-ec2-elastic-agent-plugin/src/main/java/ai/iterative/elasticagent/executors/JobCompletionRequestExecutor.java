package ai.iterative.elasticagent.executors;

import ai.iterative.elasticagent.AwsEC2Instance;
import ai.iterative.elasticagent.requests.JobCompletionRequest;
import ai.iterative.elasticagent.AgentInstances;
import ai.iterative.elasticagent.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class JobCompletionRequestExecutor implements RequestExecutor {
    private final JobCompletionRequest jobCompletionRequest;
    private final AgentInstances<AwsEC2Instance> agentInstances;

    public JobCompletionRequestExecutor(JobCompletionRequest jobCompletionRequest, AgentInstances<AwsEC2Instance> agentInstances) {
        this.jobCompletionRequest = jobCompletionRequest;
        this.agentInstances = agentInstances;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        agentInstances.terminate(jobCompletionRequest.getElasticAgentId(), jobCompletionRequest.clusterProperties());
        return new DefaultGoPluginApiResponse(200);
    }
}
