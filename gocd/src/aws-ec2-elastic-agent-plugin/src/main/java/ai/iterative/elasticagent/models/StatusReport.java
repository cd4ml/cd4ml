package ai.iterative.elasticagent.models;

public class StatusReport {

    private final String version;
    //Add fields as needed

    public StatusReport(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
