/*
 * Copyright 2009-2012 Marcelo Morales
 *
 * Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License. under the License.
 */

package cryptoexplorer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import static com.google.common.collect.Sets.newTreeSet;
import static java.lang.String.format;
import static java.security.Provider.Service;
import static java.security.Security.getProviders;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/services")
@Produces(APPLICATION_JSON)
public class Services {

    @GET
    @Path("/list")
    public String servicesList() {

        ImmutableSet<String> hasDetail = ImmutableSet.of(SecureRandom.class.getSimpleName());

        TreeSet<Map<String, String>> elements = newMapOrderedBy("name");

        for (Provider provider : getProviders()) {
            for (Service service : provider.getServices()) {
                String type = service.getType();
                elements.add(ImmutableMap.of(
                        "name", type,
                        "description", type.replaceAll(format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"), " "),
                        "hasDetail", hasDetail.contains(type)?type:"false"));
            }
        }

        return new Gson().toJson(elements);
    }

    @GET
    @Path("/describe/{service}")
    public String describe(@PathParam("service") String service) {

        TreeSet<Map<String, String>> elements = newMapOrderedBy("provider");

        for (Provider provider : getProviders()) {
            for (Service providerService : provider.getServices()) {
                if (providerService.getType().equals(service)) {
                    elements.add(ImmutableMap.of("algorithm", providerService.getAlgorithm(), "provider",
                            provider.getName()));
                }
            }
        }

        return new Gson().toJson(elements);
    }

    private static TreeSet<Map<String, String>> newMapOrderedBy(final String sortKey) {
        return newTreeSet(new Comparator<Map<String, String>>() {

            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return o1.get(sortKey).compareTo(o2.get(sortKey));
            }
        });
    }
}