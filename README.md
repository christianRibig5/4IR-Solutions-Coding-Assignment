# Guestbook Monitoring with Pulumi, Kubernetes, Prometheus and Grafana

## Overview
This project deploys the Kubernetes Guestbook application using Pulumi TypeScript on Amazon EKS.

The solution integrates:
- Kubernetes
- Pulumi
- Amazon EKS
- Prometheus
- Grafana
- Helm Charts

The monitoring stack provides visibility into cluster and application performance.
---
## Architecture
Frontend Service
Redis Leader
Redis Replica
Prometheus Monitoring
Grafana Dashboard
---
## Prerequisites
- Node.js
- Pulumi CLI
- kubectl
- AWS CLI
- EKS Cluster
- Helm
---
## Project file structure :
4IR-GUESTBOOK-MONITORING/
│
├── eks-cluster/
├── examples/
├── vpc/
│
├── create-cluster.sh
├── destroy-cluster.sh
└── README.md

## Requirements for using the scripts :
- macOS/Linux OR
- Windows with Git Bash OR WSL

## First Step: EKS CLUSTER creation

Run the script on parent folder to create the cluster on aws

## scripts/
   ├── create-cluster.sh  
   ├── destroy-cluster.sh

- ./create-cluster.sh and ./destroy-cluster.sh after the testing to billing from Amazon provider. 

## If you want run this project without the script.

1. Deploy the VPC first:
   cd vpc
   pulumi up
2. Get the VPC stack reference:
   pulumi stack ls
3. Copy the stack reference from the URL/name.
   Format:
   organization/project/stack
   Example:
   christianRibig5/vpc/dev
4. Open:
   eks-cluster/src/main/java/myproject/Variables.java
5. Replace:
   REPLACE_WITH_VPC_STACK_REFERENCE
   with your real VPC stack reference.
6. Deploy EKS:
   cd ../eks-cluster
   pulumi up
   --
  # After testing 
  1. on the parent folder 
      cd eks-cluster
      pulumi destroy --yes
      wait until the infrastructures are deleted and the step 2
 2. cd ../vpc
    pulumi destroy --yes
