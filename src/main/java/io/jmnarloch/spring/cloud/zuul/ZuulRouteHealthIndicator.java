/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.zuul;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Zuul routes health indicator. Indicates whether for each configured Zuul routes the discovery service contains the
 * entry for specific service. Aggregates the state of all routes.
 *
 * @author Jakub Narloch
 */
public class ZuulRouteHealthIndicator implements HealthIndicator {

    /**
     * The available routes.
     */
    private static final String AVAILABLE = "available";

    /**
     * The unavailable routes.
     */
    private static final String UNAVAILABLE = "unavailable";

    /**
     * The local discovery client.
     */
    private final DiscoveryClient discoveryClient;

    /**
     * The Zuul routing configuration.
     */
    private final ZuulProperties zuulProperties;

    /**
     * Creates new instance of {@link ZuulRouteHealthIndicator} instance with the discovery client and Zuul properties.
     * @param discoveryClient the discover client
     * @param zuulProperties the zuul properties
     */
    public ZuulRouteHealthIndicator(DiscoveryClient discoveryClient, ZuulProperties zuulProperties) {
        Assert.notNull(discoveryClient, "Parameter 'discoveryClient' can not be null");
        Assert.notNull(zuulProperties, "Parameter 'zuulPropertes' can not be null");
        this.discoveryClient = discoveryClient;
        this.zuulProperties = zuulProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Health health() {

        final Health.Builder builder = Health.unknown();
        builder.status(getRouteStatus());
        return withAdditionalDetails(builder).build();
    }

    /**
     * Retrieves the Zuul routes health status.
     * @return the routes status
     */
    private Status getRouteStatus() {
        for (ZuulProperties.ZuulRoute route : zuulProperties.getRoutes().values()) {
            if (route.getServiceId() != null && discoveryClient.getInstances(route.getServiceId()).isEmpty()) {
                return Status.DOWN;
            }
        }
        return Status.UP;
    }

    /**
     * Populates the health information with the list of available routes.
     *
     * @param builder the health builder
     * @return the health builder
     */
    private Health.Builder withAdditionalDetails(Health.Builder builder) {

        final Set<String> available = new HashSet<>();
        final Set<String> unavailable = new HashSet<>();

        for (ZuulProperties.ZuulRoute route : zuulProperties.getRoutes().values()) {
            if(route.getServiceId() != null) {
                if(!discoveryClient.getInstances(route.getServiceId()).isEmpty()) {
                    available.add(route.getServiceId());
                } else {
                    unavailable.add(route.getServiceId());
                }
            }
        }

        if(!available.isEmpty()) {
            builder.withDetail(AVAILABLE, available);
        }
        if(!unavailable.isEmpty()) {
            builder.withDetail(UNAVAILABLE, unavailable);
        }
        return builder;
    }
}
