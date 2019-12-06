package ai.iterative.elasticagent.models;

import com.amazonaws.services.ec2.model.Instance;

import java.util.Objects;

public class AgentStatusReport {

    private final JobIdentifier jobIdentifier;
    private final String elasticAgentId;
    private final Long createdAt;
    private Instance instance;

    public AgentStatusReport(JobIdentifier jobIdentifier, String elasticAgentId, Long createdAt, Instance instance) {
        this.jobIdentifier = jobIdentifier;
        this.elasticAgentId = elasticAgentId;
        this.createdAt = createdAt;
        this.instance = instance;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public String getElasticAgentId() {
        return elasticAgentId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Instance getInstance() { return instance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentStatusReport that = (AgentStatusReport) o;
        return Objects.equals(jobIdentifier, that.jobIdentifier) &&
                Objects.equals(elasticAgentId, that.elasticAgentId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobIdentifier, elasticAgentId, createdAt, instance);
    }
}
