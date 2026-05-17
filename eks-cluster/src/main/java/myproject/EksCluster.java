package myproject;

import com.pulumi.aws.eks.Cluster;
import com.pulumi.aws.eks.ClusterArgs;
import com.pulumi.aws.eks.inputs.ClusterAccessConfigArgs;
import com.pulumi.aws.eks.inputs.ClusterKubernetesNetworkConfigArgs;
import com.pulumi.aws.eks.inputs.ClusterVpcConfigArgs;

import java.util.List;

public class EksCluster {

    private final Cluster cluster;

    public EksCluster(
            VpcStackReference vpcStackReference,
            EksClusterIamRole eksClusterIamRole) {

        var clusterArgs = ClusterArgs.builder()
                .name(DataSourcesAndLocals.EKS_CLUSTER_NAME)
                .roleArn(eksClusterIamRole.getRole().arn())
                .version(Variables.CLUSTER_VERSION)
                .vpcConfig(ClusterVpcConfigArgs.builder()
                        .subnetIds(vpcStackReference.getPrivateSubnetIds())
                        .endpointPrivateAccess(Variables.CLUSTER_ENDPOINT_PRIVATE_ACCESS)
                        .endpointPublicAccess(Variables.CLUSTER_ENDPOINT_PUBLIC_ACCESS)
                        .publicAccessCidrs(Variables.CLUSTER_ENDPOINT_PUBLIC_ACCESS_CIDRS)
                        .build())
                .enabledClusterLogTypes(List.of(
                        "api",
                        "audit",
                        "authenticator",
                        "controllerManager",
                        "scheduler"))
                .accessConfig(ClusterAccessConfigArgs.builder()
                        .authenticationMode("API_AND_CONFIG_MAP")
                        .bootstrapClusterCreatorAdminPermissions(true)
                        .build())
                .tags(Variables.COMMON_TAGS);

        // Only configure custom Kubernetes service CIDR when provided.
        // This prevents Pulumi Java from failing when the value is null.
        if (Variables.CLUSTER_SERVICE_IPV4_CIDR != null) {
            clusterArgs.kubernetesNetworkConfig(
                    ClusterKubernetesNetworkConfigArgs.builder()
                            .serviceIpv4Cidr(Variables.CLUSTER_SERVICE_IPV4_CIDR)
                            .build());
        }

        this.cluster = new Cluster(
                "eks-cluster",
                clusterArgs.build());
    }

    public Cluster getCluster() {
        return this.cluster;
    }
}
