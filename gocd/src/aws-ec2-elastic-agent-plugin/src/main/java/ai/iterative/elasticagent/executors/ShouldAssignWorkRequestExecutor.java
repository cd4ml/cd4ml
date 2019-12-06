/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.iterative.elasticagent.executors;

import ai.iterative.elasticagent.AwsEC2Instance;
import ai.iterative.elasticagent.requests.ShouldAssignWorkRequest;
import ai.iterative.elasticagent.AgentInstances;
import ai.iterative.elasticagent.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class ShouldAssignWorkRequestExecutor implements RequestExecutor {
    private final AgentInstances<AwsEC2Instance> agentInstances;
    private final ShouldAssignWorkRequest request;

    public ShouldAssignWorkRequestExecutor(ShouldAssignWorkRequest request, AgentInstances<AwsEC2Instance> agentInstances) {
        this.request = request;
        this.agentInstances = agentInstances;
    }

    @Override
    public GoPluginApiResponse execute() {
        AwsEC2Instance instance = agentInstances.find(request.agent().elasticAgentId());

        if (instance == null) {
            return DefaultGoPluginApiResponse.success("false");
        }

        if (instance.jobIdentifier().equals(request.jobIdentifier())) {
            return DefaultGoPluginApiResponse.success("true");
        }

        return DefaultGoPluginApiResponse.success("false");
    }
}
