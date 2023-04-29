/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EnolaGui {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "empty ;-)";
            try {
                // test data
                String query = t.getRequestURI().getQuery();
                var enolaListviewParameters = new EnolaListviewParameters(query);
                ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
                values.add(new ArrayList<String>(Arrays.asList("Zurich", "Meilen", "10", "Wikipedia#https://en.wikipedia.org/wiki/Meilen")));
                values.add(new ArrayList<String>(Arrays.asList("Zurich", "Adliswil", "20", "Wikipedia#https://en.wikipedia.org/wiki/Adliswil")));
                values.add(new ArrayList<String>(Arrays.asList("Zurich", "Bonstetten", "30", "Wikipedia#https://en.wikipedia.org/wiki/Bonstetten,_Switzerland")));
                values.add(new ArrayList<String>(Arrays.asList("Geneve", "Thonex", "10", "Wikipedia#https://en.wikipedia.org/wiki/Thonex")));
                values.add(new ArrayList<String>(Arrays.asList("Geneve", "Chene-Bourg", "30", "Wikipedia#https://en.wikipedia.org/wiki/Chene-Bourg")));
                values.add(new ArrayList<String>(Arrays.asList("Geneve", "Dardagny", "20", "Wikipedia#https://en.wikipedia.org/wiki/Dardagny")));
                values.add(new ArrayList<String>(Arrays.asList("Geneve", "Puplinge", "20", "Wikipedia#https://en.wikipedia.org/wiki/Puplinge")));
                values.add(new ArrayList<String>(Arrays.asList("Vaud", "La Sarraz", "20", "Wikipedia#https://en.wikipedia.org/wiki/La_Sarraz")));
                values.add(new ArrayList<String>(Arrays.asList("Vaud", "Lausanne", "30", "Wikipedia#https://en.wikipedia.org/wiki/Lausanne")));
                values.add(new ArrayList<String>(Arrays.asList("Vaud", "Nyon", "10", "Wikipedia#https://en.wikipedia.org/wiki/Nyon")));
                values.add(new ArrayList<String>(Arrays.asList("Vaud", "Montreux", "10", "Wikipedia#https://en.wikipedia.org/wiki/Montreux")));

                ArrayList<ArrayList<CellData>> data = new ArrayList<ArrayList<CellData>>();
                for (int i=0; i<values.size(); i++){
                    data.add(new ArrayList<CellData>());
                    for (int j=0; j<values.get(i).size();j++){
                        var value = values.get(i).get(j);
                        if (value.contains("#https://")) {
                            data.get(i).add(new CellData(value.split("#")[0], value.split("#")[1]));
                        } else {
                            data.get(i).add(new CellData(value));
                        }
                    }
                }

                ArrayList<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>();
                columnDescriptors.add(new ColumnDescriptor("Canton", AggregationMethod.COLLECT));
                columnDescriptors.add(new ColumnDescriptor("Commune", AggregationMethod.COLLECT));
                columnDescriptors.add(new ColumnDescriptor("Habitants", AggregationMethod.SUM));
                columnDescriptors.add(new ColumnDescriptor("Wikipedia", AggregationMethod.COLLECT));

                EnolaListview enolaListview = new EnolaListview(data, columnDescriptors, enolaListviewParameters);

                response = enolaListview.getHtml();
            } catch (Exception e) {
                e.printStackTrace();
                response = e.toString();
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }}


