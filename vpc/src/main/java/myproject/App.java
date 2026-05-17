package myproject;

import com.pulumi.Pulumi;
import com.pulumi.aws.ec2.*;
import com.pulumi.aws.ec2.inputs.RouteTableRouteArgs;
import com.pulumi.core.Output;
import com.pulumi.resources.CustomResourceOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class App {
        public static void main(String[] args) {
                Pulumi.run(ctx -> {

                        var vpc = new Vpc("main-vpc", VpcArgs.builder()
                                        .cidrBlock(Variables.VPC_CIDR)
                                        .enableDnsHostnames(true)
                                        .enableDnsSupport(true)
                                        .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                        "Name", Variables.ENVIRONMENT_NAME + "-vpc")))
                                        .build(), CustomResourceOptions.builder().protect(false).build());

                        var igw = new InternetGateway("main-igw", InternetGatewayArgs.builder()
                                        .vpcId(vpc.id())
                                        .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                        "Name", Variables.ENVIRONMENT_NAME + "-igw")))
                                        .build());

                        Output<List<String>> azs = DataSourcesAndLocals.getAvailabilityZones();

                        List<String> publicCidrs = DataSourcesAndLocals.getPublicCidrs();
                        List<String> privateCidrs = DataSourcesAndLocals.getPrivateCidrs();

                        Subnet[] publicSubnets = new Subnet[Variables.SUBNET_COUNT];
                        Subnet[] privateSubnets = new Subnet[Variables.SUBNET_COUNT];

                        for (int i = 0; i < Variables.SUBNET_COUNT; i++) {
                                final int index = i;

                                publicSubnets[index] = new Subnet("public-subnet-" + (index + 1),
                                                SubnetArgs.builder()
                                                                .vpcId(vpc.id())
                                                                .cidrBlock(publicCidrs.get(index))
                                                                .availabilityZone(
                                                                                azs.applyValue(list -> list.get(index)))
                                                                .mapPublicIpOnLaunch(true)
                                                                .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                                                "Name",
                                                                                Variables.ENVIRONMENT_NAME
                                                                                                + "-public-subnet-"
                                                                                                + (index + 1),
                                                                                "Tier", "Public")))
                                                                .build());
                        }

                        for (int i = 0; i < Variables.SUBNET_COUNT; i++) {
                                final int index = i;

                                privateSubnets[index] = new Subnet("private-subnet-" + (index + 1),
                                                SubnetArgs.builder()
                                                                .vpcId(vpc.id())
                                                                .cidrBlock(privateCidrs.get(index))
                                                                .availabilityZone(
                                                                                azs.applyValue(list -> list.get(index)))
                                                                .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                                                "Name",
                                                                                Variables.ENVIRONMENT_NAME
                                                                                                + "-private-subnet-"
                                                                                                + (index + 1),
                                                                                "Tier", "Private")))
                                                                .build());
                        }
                        var natEip = new Eip("nat-eip", EipArgs.builder()
                                        .domain("vpc")
                                        .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                        "Name", Variables.ENVIRONMENT_NAME + "-nat-eip")))
                                        .build());

                        var natGateway = new NatGateway("nat-gateway", NatGatewayArgs.builder()
                                        .allocationId(natEip.id())
                                        .subnetId(publicSubnets[0].id())
                                        .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                        "Name", Variables.ENVIRONMENT_NAME + "-nat-gateway")))
                                        .build());

                        var publicRouteTable = new RouteTable("public-rt", RouteTableArgs.builder()
                                        .vpcId(vpc.id())
                                        .routes(RouteTableRouteArgs.builder()
                                                        .cidrBlock("0.0.0.0/0")
                                                        .gatewayId(igw.id())
                                                        .build())
                                        .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                        "Name", Variables.ENVIRONMENT_NAME + "-public-rt",
                                                        "Tier", "Public")))
                                        .build());

                        for (int i = 0; i < 3; i++) {
                                new RouteTableAssociation("public-rt-assoc-" + (i + 1),
                                                RouteTableAssociationArgs.builder()
                                                                .subnetId(publicSubnets[i].id())
                                                                .routeTableId(publicRouteTable.id())
                                                                .build());
                        }

                        var privateRouteTable = new RouteTable("private-rt", RouteTableArgs.builder()
                                        .vpcId(vpc.id())
                                        .routes(RouteTableRouteArgs.builder()
                                                        .cidrBlock("0.0.0.0/0")
                                                        .natGatewayId(natGateway.id())
                                                        .build())
                                        .tags(mergeTags(Variables.COMMON_TAGS, Map.of(
                                                        "Name", Variables.ENVIRONMENT_NAME + "-private-rt",
                                                        "Tier", "Private")))
                                        .build());

                        for (int i = 0; i < 3; i++) {
                                new RouteTableAssociation("private-rt-assoc-" + (i + 1),
                                                RouteTableAssociationArgs.builder()
                                                                .subnetId(privateSubnets[i].id())
                                                                .routeTableId(privateRouteTable.id())
                                                                .build());
                        }

                        ctx.export("vpc_id", vpc.id());

                        // Public subnet IDs
                        Output<List<String>> publicSubnetIds = Output.all(
                                        Arrays.stream(publicSubnets)
                                                        .map(subnet -> subnet.id())
                                                        .toList())
                                        .applyValue(values -> values.stream()
                                                        .map(value -> (String) value)
                                                        .toList());

                        ctx.export("public_subnet_ids", publicSubnetIds);

                        // Private subnet IDs
                        Output<List<String>> privateSubnetIds = Output.all(
                                        Arrays.stream(privateSubnets)
                                                        .map(subnet -> subnet.id())
                                                        .toList())
                                        .applyValue(values -> values.stream()
                                                        .map(value -> (String) value)
                                                        .toList());

                        ctx.export("private_subnet_ids", privateSubnetIds);

                        // Public subnet map
                        Output<Map<String, String>> publicSubnetMap = Output.tuple(azs, publicSubnetIds)
                                        .applyValue(tuple -> {

                                                List<String> azList = tuple.t1;
                                                List<String> subnetIds = tuple.t2;

                                                Map<String, String> result = new HashMap<>();

                                                for (int i = 0; i < azList.size(); i++) {
                                                        result.put(azList.get(i), subnetIds.get(i));
                                                }

                                                return result;
                                        });

                        ctx.export("public_subnet_map", publicSubnetMap);
                });
        }

        private static Map<String, String> mergeTags(
                        Map<String, String> commonTags,
                        Map<String, String> extraTags) {
                Map<String, String> merged = new HashMap<>();
                merged.putAll(commonTags);
                merged.putAll(extraTags);
                return merged;
        }
}
