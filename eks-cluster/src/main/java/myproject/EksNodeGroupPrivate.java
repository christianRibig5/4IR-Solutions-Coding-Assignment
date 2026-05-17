package myproject;

import com.pulumi.aws.eks.NodeGroup;
import com.pulumi.aws.eks.NodeGroupArgs;
import com.pulumi.aws.eks.inputs.NodeGroupScalingConfigArgs;
import com.pulumi.aws.eks.inputs.NodeGroupUpdateConfigArgs;

import java.util.Map;

public class EksNodeGroupPrivate {

    private final NodeGroup nodeGroup;

    public EksNodeGroupPrivate(
            EksCluster eksCluster,
            VpcStackReference vpcStackReference,
            EksNodeGroupIamRole nodeGroupIamRole) {

        this.nodeGroup = new NodeGroup("eks-private-nodegroup", NodeGroupArgs.builder()

                .clusterName(eksCluster.getCluster().name())

                .nodeGroupName(DataSourcesAndLocals.NAME + "-private-ng")

                .nodeRoleArn(nodeGroupIamRole.getRole().arn())

                .subnetIds(vpcStackReference.getPrivateSubnetIds())

                .instanceTypes(Variables.NODE_INSTANCE_TYPES)

                .capacityType(Variables.NODE_CAPACITY_TYPE)

                .amiType("AL2023_x86_64_STANDARD")

                .diskSize(Variables.NODE_DISK_SIZE)

                .scalingConfig(NodeGroupScalingConfigArgs.builder()
                        .desiredSize(Variables.NODE_DESIRED_SIZE)
                        .minSize(Variables.NODE_MIN_SIZE)
                        .maxSize(Variables.NODE_MAX_SIZE)
                        .build())

                .updateConfig(NodeGroupUpdateConfigArgs.builder()
                        .maxUnavailablePercentage(Variables.NODE_MAX_UNAVAILABLE_PERCENTAGE)
                        .build())

                .forceUpdateVersion(true)

                .labels(Map.of(
                        "env", Variables.ENVIRONMENT_NAME,
                        "team", Variables.BUSINESS_DIVISION))

                .tags(Map.of(
                        "Name", DataSourcesAndLocals.NAME + "-private-ng",
                        "Environment", Variables.ENVIRONMENT_NAME,
                        "ManagedBy", "Pulumi",
                        "Project", "EKS"))

                .build());
    }

    public NodeGroup getNodeGroup() {
        return this.nodeGroup;
    }
}
