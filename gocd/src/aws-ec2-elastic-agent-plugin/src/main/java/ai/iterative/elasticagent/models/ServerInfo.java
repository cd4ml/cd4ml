package ai.iterative.elasticagent.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerInfo {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Expose
    @SerializedName("server_id")
    private String serverId;

    @Expose
    @SerializedName("site_url")
    private String siteUrl;

    @Expose
    @SerializedName("secure_site_url")
    private String secureSiteUrl;

    public String getServerId() {
        return serverId;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getSecureSiteUrl() {
        return secureSiteUrl;
    }

    public void setSecureSiteUrl(String secureSiteUrl) {
        this.secureSiteUrl = secureSiteUrl;
    }

    public static ServerInfo fromJSON(String json) {
        return GSON.fromJson(json, ServerInfo.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

}
