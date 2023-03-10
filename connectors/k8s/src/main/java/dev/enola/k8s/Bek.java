package dev.enola.k8s;

import io.kubernetes.client.ProtoClient;
import io.kubernetes.client.ProtoClient.ObjectOrStatus;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.Config;

public class Bek {
    public static void main(String[] args) throws Exception {
        System.out.println("hello, world");

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        ProtoClient pc = new ProtoClient(client);
        ObjectOrStatus<V1.PodList> list =
                pc.list(V1.PodList.newBuilder(), "/api/v1/namespaces/default/pods");

        if (list.object.getItemsCount() > 0) {
            V1.Pod p = list.object.getItems(0);
            System.out.println(p);
        }
    }
}
