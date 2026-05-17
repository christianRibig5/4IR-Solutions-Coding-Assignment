package myproject;

import com.pulumi.core.Output;
import com.pulumi.resources.StackReference;
import com.pulumi.resources.StackReferenceArgs;

import java.util.List;

public class VpcStackReference {

    /*
     * ============================================================
     * REMOTE STATE CLASS
     * ============================================================
     *
     * PURPOSE:
     * This class connects the EKS project to outputs exported
     * from the VPC project using Pulumi StackReference.
     *
     * BEFORE RUNNING THIS PROJECT:
     *
     * 1. Deploy the VPC stack first:
     * cd vpc
     * pulumi up
     *
     * 2. Get your VPC stack reference:
     * pulumi stack ls
     *
     * Example:
     * JohnDoe/vpc/dev
     *
     * 3. Update this value inside Variables.java:
     *
     * public static final String VPC_STACK_REFERENCE =
     * "JohnDow/vpc/dev";
     *
     * 4. Then deploy the EKS stack:
     * cd eks-cluster
     * pulumi up
     *
     * ============================================================
     */

    private final StackReference vpcStack;

    private final Output<String> vpcId;
    private final Output<List<String>> privateSubnetIds;
    private final Output<List<String>> publicSubnetIds;

    @SuppressWarnings("unchecked")
    public VpcStackReference() {

        this.vpcStack = new StackReference("vpc-reference", StackReferenceArgs.builder()
                .name(Variables.VPC_STACK_REFERENCE)
                .build());

        this.vpcId = this.vpcStack.requireOutput("vpc_id").applyValue(value -> (String) value);

        this.privateSubnetIds = this.vpcStack.requireOutput("private_subnet_ids")
                .applyValue(value -> (List<String>) value);

        this.publicSubnetIds = this.vpcStack.requireOutput("public_subnet_ids")
                .applyValue(value -> (List<String>) value);
    }

    public Output<String> getVpcId() {
        return this.vpcId;
    }

    public Output<List<String>> getPrivateSubnetIds() {
        return this.privateSubnetIds;
    }

    public Output<List<String>> getPublicSubnetIds() {
        return this.publicSubnetIds;
    }
}