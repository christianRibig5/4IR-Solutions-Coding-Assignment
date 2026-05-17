package myproject;

import com.pulumi.aws.ec2.Tag;
import com.pulumi.aws.ec2.TagArgs;
import com.pulumi.core.Output;

/*
 * ============================================================
 * EKS TAGS CLASS
 * ============================================================
 *
 * aws_ec2_tag for public/private subnets.
 *
 * These tags allow Kubernetes/EKS to discover which subnets
 * should be used for:
 *
 * - public load balancers
 * - internal/private load balancers
 *
 * ============================================================
 */

public class EksTags {

    public EksTags(VpcStackReference vpcStackReference) {

        for (int i = 0; i < Variables.SUBNET_COUNT; i++) {

            int index = i;

            Output<String> publicSubnetId = vpcStackReference.getPublicSubnetIds()
                    .applyValue(ids -> ids.get(index));

            Output<String> privateSubnetId = vpcStackReference.getPrivateSubnetIds()
                    .applyValue(ids -> ids.get(index));

            // Public subnet tag for external load balancer
            createTag("public-elb-tag-" + (index + 1), publicSubnetId,
                    "kubernetes.io/role/elb",
                    "1");

            // Public subnet cluster ownership tag
            createTag(
                    "public-cluster-tag-" + (index + 1),
                    publicSubnetId,
                    "kubernetes.io/cluster/" + DataSourcesAndLocals.EKS_CLUSTER_NAME,
                    "shared");

            // Private subnet tag for internal load balancer
            createTag("private-internal-elb-tag-" + (index + 1), privateSubnetId,
                    "kubernetes.io/role/internal-elb",
                    "1");

            // Private subnet cluster ownership tag
            createTag("private-cluster-tag-" + (index + 1), privateSubnetId,
                    "kubernetes.io/cluster/" + DataSourcesAndLocals.EKS_CLUSTER_NAME, "shared");
        }
    }

    private void createTag(String resourceName, Output<String> resourceId, String key, String value) {
        new Tag(resourceName, TagArgs.builder().resourceId(resourceId).key(key)
                .value(value)
                .build());
    }
}
