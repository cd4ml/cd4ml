package ai.iterative.elasticagent.executors;

import ai.iterative.elasticagent.requests.ClusterProfileValidateRequest;
import ai.iterative.elasticagent.RequestExecutor;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.*;

public class ClusterProfileValidateRequestExecutor implements RequestExecutor {
    private final ClusterProfileValidateRequest request;
    private static final Gson GSON = new Gson();

    public ClusterProfileValidateRequestExecutor(ClusterProfileValidateRequest request) {
        this.request = request;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        ArrayList<Map<String, String>> result = new ArrayList<>();

        List<String> knownFields = new ArrayList<>();

        for (Metadata field : GetClusterProfileMetadataExecutor.CLUSTER_PROFILE_FIELDS) {
            knownFields.add(field.getKey());
            Map<String, String> validationError = field.validate(request.getProperties().get(field.getKey()));

            if (!validationError.isEmpty()) {
                result.add(validationError);
            }
        }

        Set<String> set = new HashSet<>(request.getProperties().keySet());
        set.removeAll(knownFields);

        if (!set.isEmpty()) {
            for (String key : set) {
                LinkedHashMap<String, String> validationError = new LinkedHashMap<>();
                validationError.put("key", key);
                validationError.put("message", "Is an unknown property");
                result.add(validationError);
            }
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(result));
    }
}
