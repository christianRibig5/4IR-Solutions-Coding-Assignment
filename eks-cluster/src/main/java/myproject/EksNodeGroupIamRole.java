package myproject;

import com.pulumi.aws.iam.Role;
import com.pulumi.aws.iam.RoleArgs;
import com.pulumi.aws.iam.RolePolicyAttachment;
import com.pulumi.aws.iam.RolePolicyAttachmentArgs;

/*
 * ============================================================
 * EKS NODE GROUP IAM ROLE
 * ============================================================
 *
 * This role is assumed by EC2 worker nodes launched
 * inside the EKS managed node group.
 * ============================================================
 */

public class EksNodeGroupIamRole {

    private final Role role;

    public EksNodeGroupIamRole() {

        this.role = new Role("eks-nodegroup-role", RoleArgs.builder()
                .name(DataSourcesAndLocals.NAME + "-eks-nodegroup-role")
                .assumeRolePolicy("""
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Effect": "Allow",
                              "Principal": {
                                "Service": "ec2.amazonaws.com"
                              },
                              "Action": "sts:AssumeRole"
                            }
                          ]
                        }
                        """)
                .tags(Variables.COMMON_TAGS)
                .build());

        attachPolicy(
                "eks-worker-node-policy",
                "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy");

        attachPolicy(
                "eks-cni-policy",
                "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy");

        attachPolicy(
                "eks-ecr-readonly-policy",
                "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly");
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