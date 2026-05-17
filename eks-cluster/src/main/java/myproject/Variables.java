package myproject;

import java.util.List;
import java.util.Map;

/*
 * =========================================================
 * EKS VARIABLES CLASS
 * =========================================================
 * Centralized configuration class for reusable EKS
 * infrastructure deployment using Pulumi Java.
 *
 * Design Goals:
 * - Reusable for assignments/interviews
 * - Easy for recruiters/teams to run
 * - GitHub-friendly
 * - Simple static configuration
 * - Easy future migration to Pulumi Config
 *
 * NOTE:
 * AWS region and AWS profile are intentionally NOT
 * hardcoded here.
 *
 * Pulumi automatically uses:
 * - currently authenticated AWS credentials
 * - selected AWS profile
 * - configured AWS region
 *
 * This makes the project more portable and reusable.
 * =========================================================
 */

public class Variables {

    /*
     * =========================================================
     * ENVIRONMENT CONFIGURATION
     * =========================================================
     */

    // Pulumi VPC stack reference
    // Format:
    // organization/project/stack
    //
    // Example:
    // christianRibig5/vpc/dev
    //
    // IMPORTANT:
    // Update this value to match YOUR deployed VPC stack.
    //
    // To check available stacks:
    // pulumi stack ls
    //
    public static final String VPC_STACK_REFERENCE = "christianRibig5/vpc/dev";// "christianRibig5/vpc/dev";

    // Logical environment name
    public static final String ENVIRONMENT_NAME = "dev";

    // Business division or department
    public static final String BUSINESS_DIVISION = "retail";

    /*
     * =========================================================
     * EKS CLUSTER CONFIGURATION
     * =========================================================
     */

    // EKS cluster name
    public static final String CLUSTER_NAME = "eksdemo";

    // Leave null for AWS default supported version
    public static final String CLUSTER_VERSION = "1.34";

    // Leave null to use AWS default Kubernetes service CIDR
    public static final String CLUSTER_SERVICE_IPV4_CIDR = null;

    /*
     * =========================================================
     * CLUSTER ENDPOINT ACCESS
     * =========================================================
     */

    // Allow private endpoint access
    public static final boolean CLUSTER_ENDPOINT_PRIVATE_ACCESS = false;

    // Allow public endpoint access
    public static final boolean CLUSTER_ENDPOINT_PUBLIC_ACCESS = true;

    // WARNING:
    // 0.0.0.0/0 allows access from anywhere.
    // Restrict in production environments.
    public static final List<String> CLUSTER_ENDPOINT_PUBLIC_ACCESS_CIDRS = List.of("0.0.0.0/0");

    /*
     * =========================================================
     * NODE GROUP CONFIGURATION
     * =========================================================
     */

    // EC2 instance types for worker nodes
    public static final List<String> NODE_INSTANCE_TYPES = List.of("t3.medium");

    // ON_DEMAND or SPOT
    public static final String NODE_CAPACITY_TYPE = "ON_DEMAND";

    // Node root volume size in GB
    public static final int NODE_DISK_SIZE = 20;

    // For dynamic subnet count
    public static final int SUBNET_COUNT = 3;

    // Node desired size
    public static final int NODE_DESIRED_SIZE = 3;

    // Node minimum size
    public static final int NODE_MIN_SIZE = 1;

    // Node maximum size
    public static final int NODE_MAX_SIZE = 6;

    // Node maxUnavailablePercentage size
    public static final int NODE_MAX_UNAVAILABLE_PERCENTAGE = 33;

    /*
     * =========================================================
     * COMMON RESOURCE TAGS
     * =========================================================
     */

    public static final Map<String, String> COMMON_TAGS = Map.of(
            "ManagedBy", "Pulumi",
            "Environment", ENVIRONMENT_NAME,
            "BusinessDivision", BUSINESS_DIVISION,
            "Project", "EKS");
}