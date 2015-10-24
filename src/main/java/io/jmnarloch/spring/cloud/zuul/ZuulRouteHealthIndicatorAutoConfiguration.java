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

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the {@link ZuulRouteHealthIndicator}.
 *
 * @author Jakub Narloch
 * @see ZuulRouteHealthIndicator
 */
@Configuration
@ConditionalOnClass({DiscoveryClient.class, ZuulProperties.class})
@ConditionalOnProperty(value = "zuul.health.enabled", matchIfMissing = true)
public class ZuulRouteHealthIndicatorAutoConfiguration {

    @Bean
    @ConditionalOnBean({DiscoveryClient.class, ZuulProperties.class})
    public ZuulRouteHealthIndicator zuulRoutesHealthIndicator(DiscoveryClient discoveryClient, ZuulProperties zuulProperties) {
        return new ZuulRouteHealthIndicator(discoveryClient, zuulProperties);
    }
}
