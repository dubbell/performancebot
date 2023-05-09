package com.icetlab.performancebot.client;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

/**
 * Holds the Kubernetes Client and provides its IP through getServerIp
 */
public class Kubernetes implements BenchmarkWorkerClient {

  private final KubernetesClient kubernetesClient;

  public Kubernetes() {
    this.kubernetesClient = new KubernetesClientBuilder().build();
  }

  @Override
  public String getServerIp() {
    Service service = kubernetesClient.services().withName("benchmark-worker").get();
    String ip = service.getSpec().getClusterIP();
    int port = service.getSpec().getPorts().get(0).getPort();
    return String.format("%s:%d", ip, port);
  }
}
