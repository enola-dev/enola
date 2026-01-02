/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
