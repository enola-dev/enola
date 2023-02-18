package dev.enola.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;

public class Bek {
    public static void main(String[] args) throws Exception {
        System.out.println("hello, world");

        ApiClient client = Config.defaultClient();
    }
}
