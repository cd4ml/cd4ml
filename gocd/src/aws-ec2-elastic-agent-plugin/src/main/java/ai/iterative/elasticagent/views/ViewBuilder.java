package ai.iterative.elasticagent.views;

//TODO You can modify and use this class to generate views
public class ViewBuilder {

    private static ViewBuilder builder;

    public static ViewBuilder instance() {
        if (builder == null) {
            builder = new ViewBuilder();
        }
        return builder;
    }

    public String build(String template) {
        return "";
    }

    public String build(String template, Object model) {
        return "";
    }
}
