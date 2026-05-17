#!/bin/bash
set -euo pipefail

echo "===================================="
echo "STEP 1: Destroy EKS Cluster"
echo "===================================="

cd eks-cluster
pulumi destroy --yes

echo "===================================="
echo "STEP 2: Destroy VPC"
echo "===================================="

cd ../vpc
pulumi destroy --yes

echo "✅ EKS cluster and VPC destroyed successfully!"