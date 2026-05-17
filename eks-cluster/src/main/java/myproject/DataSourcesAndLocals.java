package myproject;

public class DataSourcesAndLocals {

    private DataSourcesAndLocals() {
    }

    // Business division or team name
    public static final String OWNERS = Variables.BUSINESS_DIVISION;

    // Environment name: dev, prod, staging
    public static final String ENVIRONMENT = Variables.ENVIRONMENT_NAME;

    // Standard naming prefix
    // Example: retail-dev
    public static final String NAME = OWNERS + "-" + ENVIRONMENT;

    // Full EKS cluster name
    // Example: retail-dev-eksdemo
    public static final String EKS_CLUSTER_NAME = NAME + "-" + Variables.CLUSTER_NAME;
}
