package myproject;

import com.pulumi.Pulumi;

public class App {

    public static void main(String[] args) {

        Pulumi.run(ctx -> {

            var vpcStackReference = new VpcStackReference();
            new EksTags(vpcStackReference);
            var eksClusterIamRole = new EksClusterIamRole();
            var eksCluster = new EksCluster(vpcStackReference, eksClusterIamRole);
            var eksNodeGroupIamRole = new EksNodeGroupIamRole();
            var eksNodeGroupPrivate = new EksNodeGroupPrivate(eksCluster, vpcStackReference, eksNodeGroupIamRole);

            // =====================================================
            // EKS CLUSTER OUTPUTS
            // =====================================================

            ctx.export("eks_cluster_endpoint",
                    eksCluster.getCluster().endpoint());

            ctx.export("eks_cluster_id",
                    eksCluster.getCluster().id());

            ctx.export("eks_cluster_version",
                    eksCluster.getCluster().version());

            ctx.export("eks_cluster_name",
                    eksCluster.getCluster().name());

            ctx.export("private_node_group_name",
                    eksNodeGroupPrivate.getNodeGroup().nodeGroupName());

            ctx.export("eks_node_instance_role_arn",
                    eksNodeGroupIamRole.getRole().arn());

            ctx.export("to_configure_kubectl",
                    eksCluster.getCluster().name()
                            .applyValue(name -> "aws eks update-kubeconfig --name " + name));
        });
    }
}
