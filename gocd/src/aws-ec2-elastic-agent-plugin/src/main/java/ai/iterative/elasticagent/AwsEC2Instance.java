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
import ai.iterative.elasticagent.requests.CreateAgentRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class AwsEC2Instance {
    private final DateTime createdAt;
    private final Map<String, String> properties;
    private final String environment;
    private final JobIdentifier jobIdentifier;
    private String name;

    private String instanceId;
    private AmazonEC2 ec2Client;

    public AwsEC2Instance(String name, Date createdAt, Map<String, String> properties, String environment, JobIdentifier jobIdentifier) {
        this.name = name;
        this.createdAt = new DateTime(createdAt);
        this.properties = properties;
        this.environment = environment;
        this.jobIdentifier = jobIdentifier;


    }

    public void launch() {
        awsEC2Up();
    }

    public void terminate() {
        awsEC2Down();
    }

    public AgentStatusReport status() {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
        describeInstancesRequest.withInstanceIds(instanceId);
        DescribeInstancesResult response = ec2Client.describeInstances(describeInstancesRequest);
        Instance instance = response.getReservations().get(0).getInstances().get(0);

        return new AgentStatusReport(jobIdentifier, name, this.createdAt.getMillis(), instance);
    }

    public String name() {
        return name;
    }

    public DateTime createdAt() {
        return createdAt;
    }

    public String environment() {
        return environment;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public JobIdentifier jobIdentifier() {
        return jobIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AwsEC2Instance that = (AwsEC2Instance) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


    public static AwsEC2Instance create(CreateAgentRequest request) {
        return new AwsEC2Instance("agent_" + UUID.randomUUID().toString(),
                new Date(), request.profileProperties(), request.environment(), request.jobIdentifier());
    }

    private void awsEC2Up() {
        // TODO: set this by plugin settinngs
        int gocdPort = 80;
        String gocdHost = "https://";

        String accesskey = "";
        String secretkey = "";
        Regions region = Regions.US_EAST_1;
        String securityGroupName = "";
        String keyParName = "";
        String ami = "ami-067ded195acfff78d";
        String instanceType = "t2.micro"; //g3s.xlarge
        String tag = "gocd";

        AWSCredentials credentials = new BasicAWSCredentials(accesskey, secretkey);

        ec2Client = AmazonEC2ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        CreateSecurityGroupRequest createSecurityGroupRequest =
            new CreateSecurityGroupRequest()
                    .withGroupName(securityGroupName)
                    .withDescription("");
        CreateSecurityGroupResult createSecurityGroupResult = ec2Client.createSecurityGroup(createSecurityGroupRequest);

        IpRange ipRange = new IpRange().withCidrIp("0.0.0.0/0");
        IpPermission ipPermission = new IpPermission()
                .withIpv4Ranges(Arrays.asList(new IpRange[] { ipRange }))
                .withIpProtocol("tcp")
                .withFromPort(gocdPort)
                .withToPort(gocdPort);

        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
            new AuthorizeSecurityGroupIngressRequest()
                .withGroupName(securityGroupName)
                .withIpPermissions(ipPermission);

        ec2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

        CreateKeyPairRequest createKeyPairRequest =
            new CreateKeyPairRequest()
                    .withKeyName(keyParName);
        CreateKeyPairResult createKeyPairResult = ec2Client.createKeyPair(createKeyPairRequest);
        createKeyPairResult.getKeyPair().getKeyMaterial(); // SAVE THIS with hash with previous parameters
        TagSpecification tags = new TagSpecification().withTags(new Tag(tag));

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withImageId(ami)
                .withInstanceType(instanceType)
                .withKeyName(keyParName)
                .withSecurityGroups(securityGroupName)
                .withTagSpecifications(tags)
                .withMinCount(1)
                .withMaxCount(1);

        this.instanceId = ec2Client.runInstances(runInstancesRequest)
            .getReservation().getInstances().get(0).getInstanceId();

        StartInstancesRequest startInstancesRequest = new StartInstancesRequest()
                .withInstanceIds(this.instanceId);
    }

    private void awsEC2Down() {
        TerminateInstancesRequest terminateInstanceRequest = new TerminateInstancesRequest()
                .withInstanceIds(instanceId);
        ec2Client.terminateInstances(terminateInstanceRequest);
    }

    private void awsEC2Describe() {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
        DescribeInstancesResult response = ec2Client.describeInstances(describeInstancesRequest);
    }
}
