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

package ai.iterative.elasticagent;

import ai.iterative.elasticagent.models.AgentStatusReport;
import ai.iterative.elasticagent.models.JobIdentifier;
import ai.iterative.elasticagent.models.StatusReport;
import ai.iterative.elasticagent.requests.CreateAgentRequest;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static ai.iterative.elasticagent.AwsEC2Plugin.LOG;

public class AwsEC2AgentInstances implements AgentInstances<AwsEC2Instance> {

    private final ConcurrentHashMap<String, AwsEC2Instance> instances = new ConcurrentHashMap<>();

    private boolean refreshed;
    public Clock clock = Clock.DEFAULT;

    @Override
    public AwsEC2Instance create(CreateAgentRequest request) throws Exception {
        LOG.info("Creating agent");

        AwsEC2Instance instance = AwsEC2Instance.create(request);
        instance.launch();
        register(instance);

        LOG.info("Registered agent");

        return instance;
    }

    @Override
    public void terminate(String agentId, ClusterProfileProperties clusterProfile) throws Exception {
        LOG.info("Terminating agent");

        AwsEC2Instance instance = instances.get(agentId);
        if (instance != null) {
            instance.terminate();
        } else {
            LOG.error("Requested to terminate an instance that does not exist " + agentId);
        }

        instances.remove(agentId);

        LOG.info("removed agent");
    }

    @Override
    public void terminateUnregisteredInstances(ClusterProfileProperties clusterProfile, Agents agents) throws Exception {
        AwsEC2AgentInstances toTerminate = unregisteredAfterTimeout(clusterProfile, agents);
        if (toTerminate.instances.isEmpty()) return;

        LOG.warn("Terminating instances that did not register " + toTerminate.instances.keySet());
        for (AwsEC2Instance instance : toTerminate.instances.values()) {
            terminate(instance.name(), clusterProfile);
        }
    }

    @Override
    public Agents instancesCreatedAfterTimeout(ClusterProfileProperties clusterProfile, Agents agents) {
        ArrayList<Agent> oldAgents = new ArrayList<>();

        for (Agent agent : agents.agents()) {
            AwsEC2Instance instance = instances.get(agent.elasticAgentId());

            if (instance == null) continue;

            if (clock.now().isAfter(instance.createdAt().plus(clusterProfile.getAutoRegisterPeriod()))) {
                oldAgents.add(agent);
            }
        }

        return new Agents(oldAgents);
    }

    @Override
    public void refreshAll(ClusterProfileProperties clusterProfileProperties) throws Exception {
        // TODO: Implement me!

        //throw new UnsupportedOperationException();

//        if (!refreshed) {
//            TODO: List all instances from the cloud provider and select the ones that are created by this plugin
//            TODO: Most cloud providers allow applying some sort of labels or tags to instances that you may find of use
//            List<InstanceInfo> instanceInfos = cloud.listInstances().filter(...)
//            for (Instance instanceInfo: instanceInfos) {
//                  register(ExampleInstance.fromInstanceInfo(instanceInfo))
//            }
//            refreshed = true;
//        }
    }

    @Override
    public AwsEC2Instance find(String agentId) {
        return instances.get(agentId);
    }

    @Override
    public AwsEC2Instance find(JobIdentifier jobIdentifier) {
        return instances.values()
            .stream()
            .filter(x -> x.jobIdentifier().equals(jobIdentifier))
            .findFirst()
            .orElse(null);
    }

    @Override
    public StatusReport getStatusReport(ClusterProfileProperties clusterProfileProperties) throws Exception {
        StatusReport report = new StatusReport("sds");
        return report;
    }

    @Override
    public AgentStatusReport getAgentStatusReport(ClusterProfileProperties pluginSettings, AwsEC2Instance agentInstance) {
        return agentInstance.status();
    }

    // used by tests
    public boolean hasInstance(String agentId) {
        return instances.containsKey(agentId);
    }

    private void register(AwsEC2Instance instance) {
        instances.put(instance.name(), instance);
    }

    private AwsEC2AgentInstances unregisteredAfterTimeout(ClusterProfileProperties settings, Agents knownAgents) throws Exception {
        Period period = settings.getAutoRegisterPeriod();
        AwsEC2AgentInstances unregisteredContainers = new AwsEC2AgentInstances();

        for (String instanceName : instances.keySet()) {
            if (knownAgents.containsAgentWithId(instanceName)) continue;

            // TODO: Connect to the cloud provider to fetch information about this instance

            // InstanceInfo instanceInfo = connection.inspectInstance(instanceName);
            DateTime dateTimeCreated = new DateTime();

            if (clock.now().isAfter(dateTimeCreated.plus(period))) {
                unregisteredContainers.register(new AwsEC2Instance(instanceName, dateTimeCreated.toDate(), null, null, null));
            }
        }

        return unregisteredContainers;
    }

}
