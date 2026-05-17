import * as k8s from "@pulumi/kubernetes";

/*
    Create Namespace for Monitoring
*/
const monitoringNamespace = new k8s.core.v1.Namespace("monitoring", {
    metadata: {
        name: "monitoring",
    },
});

/*
    Install Prometheus using Helm Chart
*/
const prometheus = new k8s.helm.v3.Release("prometheus", {
    chart: "prometheus",
    version: "25.27.0",
    namespace: monitoringNamespace.metadata.name,
    repositoryOpts: {
        repo: "https://prometheus-community.github.io/helm-charts",
    },
    values: {
        alertmanager: {
            enabled: false,
        },
        prometheusPushgateway: {
            enabled: false,
        },
        server: {
            persistentVolume: {
                enabled: false,
            },
            resources: {
                requests: {
                    cpu: "100m",
                    memory: "256Mi",
                },
                limits: {
                    cpu: "300m",
                    memory: "512Mi",
                },
            },
        },
    },
}, {
    dependsOn: [monitoringNamespace],
    customTimeouts: {
        create: "10m",
        update: "10m",
        delete: "10m",
    },
});

/*
    Install Grafana using Helm Chart
*/
const grafana = new k8s.helm.v3.Release("grafana", {
    chart: "grafana",
    version: "8.5.2",
    namespace: monitoringNamespace.metadata.name,
    repositoryOpts: {
        repo: "https://grafana.github.io/helm-charts",
    },

    values: {
        service: {
            type: "LoadBalancer",
        },

        adminPassword: "Admin123!",
    },
});