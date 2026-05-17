package myproject;

import java.util.Map;

/*
 * =========================================================
 * VARIABLES CLASS
 * =========================================================
 * Central location for reusable infrastructure variables.
 *
 * This class is intentionally designed using static constants
 * so the project can:
 *
 * - Run immediately after cloning
 * - Be reusable for assignments/interviews
 * - Be easy for teams to understand
 * - Avoid unnecessary Pulumi config complexity
 *
 * Future enhancement:
 * You can later migrate some values to Pulumi Config
 * when building enterprise multi-environment systems.
 * =========================================================
 */

public class Variables {

    // Logical environment name
    public static final String ENVIRONMENT_NAME = "dev";

    /*
     * =====================================================
     * NETWORK CONFIGURATION
     * =====================================================
     */

    // Main VPC CIDR block
    public static final String VPC_CIDR = "10.0.0.0/16";

    // Number of additional subnet bits
    // Example:
    // 10.0.0.0/16 + 8 bits = /24 subnets
    public static final Integer SUBNET_NEWBIT = 8;

    // Number of public/private subnets to create
    public static final Integer SUBNET_COUNT = 3;

    /*
     * =====================================================
     * PROJECT METADATA
     * =====================================================
     */

    // Generic owner name for reusable GitHub projects
    public static final String OWNER = "Team";

    // Project/business identifier
    public static final String PROJECT = "Networking";

    /*
     * =====================================================
     * COMMON TAGS
     * =====================================================
     * Tags automatically applied to all resources.
     */

    public static final Map<String, String> COMMON_TAGS = Map.of(
            "Environment", ENVIRONMENT_NAME,
            "Owner", OWNER,
            "Project", PROJECT,
            "ManagedBy", "Pulumi",
            "IaCTool", "Pulumi");
}
