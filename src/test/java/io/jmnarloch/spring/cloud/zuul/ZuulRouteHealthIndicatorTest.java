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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link ZuulRouteHealthIndicator}.
 *
 * @author Jakub Narloch
 */
@SpringApplicationConfiguration(classes = {ZuulRouteHealthIndicatorTest.Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ZuulRouteHealthIndicatorTest {

    @Autowired
    private ZuulRouteHealthIndicator zuulRouteHealthIndicator;

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Test
    public void shouldReportUpStateForNoRoutes() {

        // given
        when(discoveryClient.getInstances(Mockito.any(String.class))).thenReturn(new ArrayList<ServiceInstance>());

        // when
        final Health health = zuulRouteHealthIndicator.health();

        // then
        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    public void shouldReportDownState() {

        // given
        final ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute("/zuul", "proxied-service");
        zuulProperties.getRoutes().put(route.getId(), route);

        // when
        final Health health = zuulRouteHealthIndicator.health();

        // then
        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(((Collection) health.getDetails().get("available")).isEmpty());
        assertFalse(((Collection) health.getDetails().get("unavailable")).isEmpty());
    }

    @Test
    public void shouldReportUpState() {

        // given
        final ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute("/zuul", "proxied-service");
        zuulProperties.getRoutes().put(route.getId(), route);

        final List<ServiceInstance> services = new ArrayList<>();
        services.add(mock(ServiceInstance.class));
        when(discoveryClient.getInstances("proxied-service")).thenReturn(services);

        // when
        final Health health = zuulRouteHealthIndicator.health();

        // then
        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
        assertFalse(((Collection) health.getDetails().get("available")).isEmpty());
        assertTrue(((Collection) health.getDetails().get("unavailable")).isEmpty());
    }

    @EnableAutoConfiguration
    @Configuration
    public static class Application {

        @Bean
        public ZuulProperties zuulProperties() {
            return new ZuulProperties();
        }

        @Bean
        public DiscoveryClient discoveryClient() {
            return mock(DiscoveryClient.class);
        }
    }
}