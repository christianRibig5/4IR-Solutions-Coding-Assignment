package myproject;

import com.pulumi.aws.AwsFunctions;
import com.pulumi.aws.inputs.GetAvailabilityZonesArgs;
import com.pulumi.core.Output;

import java.util.List;
import java.util.stream.IntStream;

public class DataSourcesAndLocals {
    public static Output<List<String>> getAvailabilityZones() {

        return AwsFunctions.getAvailabilityZones(
                GetAvailabilityZonesArgs.builder()
                        .state("available")
                        .build())
                .applyValue(result -> result.names()
                        .stream()
                        .limit(Variables.SUBNET_COUNT)
                        .toList());
    }

    public static List<String> getPublicCidrs() {
        return IntStream.range(0, Variables.SUBNET_COUNT)
                .mapToObj(i -> cidrSubnet(Variables.VPC_CIDR, i))
                .toList();
    }

    public static List<String> getPrivateCidrs() {
        return IntStream.range(0, Variables.SUBNET_COUNT)
                .mapToObj(i -> cidrSubnet(Variables.VPC_CIDR, i + 10))
                .toList();
    }

    private static String cidrSubnet(String vpcCidr, int subnetNumber) {
        // Works for VPC like 10.0.0.0/16 and subnet /24
        String base = vpcCidr.split("/")[0]; // 10.0.0.0
        String[] parts = base.split("\\."); // [10, 0, 0, 0]

        return parts[0] + "." + parts[1] + "." + subnetNumber + ".0/24";
    }

}
