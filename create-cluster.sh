#!/bin/bash
set -euo pipefail

echo "===================================="
echo "STEP 1: Create VPC using Pulumi"
echo "===================================="

cd vpc
pulumi up --yes

ORG=$(pulumi whoami)
PROJECT=$(grep '^name:' Pulumi.yaml | awk '{print $2}')
STACK=$(pulumi stack --show-name)

VPC_STACK_REFERENCE="${ORG}/${PROJECT}/${STACK}"

echo "VPC Stack Reference: ${VPC_STACK_REFERENCE}"

cd ../eks-cluster

echo "===================================="
echo "STEP 2: Inject VPC Stack Reference"
echo "===================================="

sed -i.bak "s|REPLACE_WITH_VPC_STACK_REFERENCE|${VPC_STACK_REFERENCE}|g" \
  src/main/java/myproject/Variables.java

rm -f src/main/java/myproject/Variables.java.bak

echo "===================================="
echo "STEP 3: Create EKS Cluster using Pulumi"
echo "===================================="

pulumi up --yes

echo "✅ VPC and EKS cluster created successfully!"