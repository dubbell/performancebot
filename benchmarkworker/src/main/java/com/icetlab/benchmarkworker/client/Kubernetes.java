package com.icetlab.benchmarkworker.client;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

/**
 * Uses kubernetes to find the server ip of performancebot
 */
public class Kubernetes implements PerformanceBotClient {
  private KubernetesClient kubernetesClient;

  public Kubernetes() {
    this.kubernetesClient = new KubernetesClientBuilder().build();
  }

  @Override
  public String getServerIpWithPort() {
    Service service = kubernetesClient.services().withName("perfbot-svc").get();
    String ip = service.getSpec().getClusterIP();
    int port = service.getSpec().getPorts().get(0).getPort();
    return ip + ":" + port;
  }
}
