package myproject;

import com.pulumi.aws.iam.Role;
import com.pulumi.aws.iam.RoleArgs;
import com.pulumi.aws.iam.RolePolicyAttachment;
import com.pulumi.aws.iam.RolePolicyAttachmentArgs;

/*
 * ============================================================
 * EKS CLUSTER IAM ROLE
 * ============================================================
 *
 * This IAM role is assumed by the EKS service to manage
 * the Kubernetes control plane.
 * ============================================================
 */

public class EksClusterIamRole {

    private final Role role;

    public EksClusterIamRole() {

        /*
         * IAM role for EKS control plane
         */
        this.role = new Role("eks-cluster-role", RoleArgs.builder()
                .name(DataSourcesAndLocals.NAME + "-eks-cluster-role")
                .assumeRolePolicy("""
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Effect": "Allow",
                              "Principal": {
                                "Service": "eks.amazonaws.com"
                              },
                              "Action": "sts:AssumeRole"
                            }
                          ]
                        }
                        """)
                .tags(Variables.COMMON_TAGS)
                .build());

        /*
         * Required policy for EKS control plane
         */
        attachPolicy(
                "eks-cluster-policy",
                "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy");

        /*
         * VPC Resource Controller policy
         * Recommended for production-grade EKS networking.
         */
        attachPolicy(
                "eks-vpc-resource-controller",
                "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController");
    }

    private void attachPolicy(String resourceName, String policyArn) {
        new RolePolicyAttachment(resourceName, RolePolicyAttachmentArgs.builder()
                .role(this.role.name())
                .policyArn(policyArn)
                .build());
    }

    public Role getRole() {
        return this.role;
    }
}