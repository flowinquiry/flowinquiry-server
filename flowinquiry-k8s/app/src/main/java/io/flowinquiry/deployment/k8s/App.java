package io.flowinquiry.deployment.k8s;


import com.pulumi.Pulumi;
import com.pulumi.core.Output;
import com.pulumi.kubernetes.apps.v1.Deployment;
import com.pulumi.kubernetes.apps.v1.DeploymentArgs;
import com.pulumi.kubernetes.apps.v1.inputs.DeploymentSpecArgs;
import com.pulumi.kubernetes.core.v1.Namespace;
import com.pulumi.kubernetes.core.v1.NamespaceArgs;
import com.pulumi.kubernetes.core.v1.Service;
import com.pulumi.kubernetes.core.v1.ServiceArgs;
import com.pulumi.kubernetes.core.v1.inputs.*;
import com.pulumi.kubernetes.meta.v1.inputs.LabelSelectorArgs;
import com.pulumi.kubernetes.meta.v1.inputs.ObjectMetaArgs;
import com.pulumi.kubernetes.networking.v1.Ingress;
import com.pulumi.kubernetes.networking.v1.IngressArgs;
import com.pulumi.kubernetes.networking.v1.inputs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);


    private static final String APP_NAMESPACE = "flowinquiry";
    private static final String TRAEFIK = "traefik";


    public static void main(String[] args) {
        Pulumi.run(ctx -> {
            var labels = Map.of("app", TRAEFIK);

            var namespace = new Namespace(APP_NAMESPACE, NamespaceArgs.builder()
                    .metadata(ObjectMetaArgs.builder()
                            .name(APP_NAMESPACE)
                            .build())
                    .build());

            var deployment = new Deployment(TRAEFIK, DeploymentArgs.builder()
                    .metadata(ObjectMetaArgs.builder()
                            .namespace(namespace.metadata().applyValue(m->m.name().orElseThrow()))
                            .labels(labels)
                            .build())
                    .spec(DeploymentSpecArgs.builder()
                            .selector(LabelSelectorArgs.builder()
                                    .matchLabels(labels)
                                    .build())
                            .replicas(1)
                            .template(PodTemplateSpecArgs.builder()
                                    .metadata(ObjectMetaArgs.builder()
                                            .namespace(namespace.metadata().applyValue(m->m.name().orElseThrow()))
                                            .labels(labels)
                                            .build())
                                    .spec(PodSpecArgs.builder()
                                            .containers(ContainerArgs.builder()
                                                    .name(TRAEFIK)
                                                    .image(TRAEFIK)
                                                    .ports(ContainerPortArgs.builder()
                                                                    .name("http")
                                                                    .containerPort(80)
                                                                    .build(),
                                                            ContainerPortArgs.builder()
                                                                    .name("https")
                                                                    .containerPort(443)
                                                                    .build())
                                                    .build())
                                            .build())
                                    .build())

                            .build())
                    .build());

            var service = new Service(TRAEFIK, ServiceArgs.builder()
                    .metadata(ObjectMetaArgs.builder()
                            .namespace(namespace.metadata().applyValue(m->m.name().orElseThrow()))
                            .labels(labels)
                            .build())
                    .spec(ServiceSpecArgs.builder()
                            .selector(labels)
                            .ports(ServicePortArgs.builder()
                                            .name("http")
                                            .port(80)
                                            .targetPort("http")
                                            .build(),
                                    ServicePortArgs.builder()
                                            .name("https")
                                            .port(443)
                                            .targetPort("https")
                                            .build())
                            .build())
                    .build());

            var ingress = new Ingress("traefik-ingress", IngressArgs.builder()
                    .metadata(ObjectMetaArgs.builder()
                            .namespace(namespace.metadata().applyValue(m->m.name().orElseThrow()))
                            .annotations(Map.of("kubernetes.io/ingress.class", TRAEFIK))
                            .build())
                    .spec(IngressSpecArgs.builder()
                            .rules(IngressRuleArgs.builder()
                                    .host("localhost")
                                    .http(HTTPIngressRuleValueArgs.builder()
                                            .paths(HTTPIngressPathArgs.builder()
                                                    .path("/")
                                                    .pathType("Prefix")
                                                    .backend(IngressBackendArgs.builder()
                                                            .service(IngressServiceBackendArgs.builder()
                                                                    .name(service.metadata().applyValue(m->m.name().orElseThrow()))
                                                                    .port(ServiceBackendPortArgs.builder().number(80).build())
                                                                    .build())
                                                    .build())
                                            .build())
                                    .build())
                            .build())
                    .build())
                    .build());

            var name = deployment.metadata()
                    .applyValue(m -> m.name().orElse("")).asPlaintext();

            LOG.info("Deployment name: {}", name);
            ingress.status().applyValue(v -> v.orElseThrow().loadBalancer().orElseThrow().ingress().getFirst().ip());
            ctx.export("name", name);
        });
    }
}
