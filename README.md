# Spring Cloud Netflix Zuul route health

> A Spring Cloud Zuul health indicator

[![Build Status](https://travis-ci.org/jmnarloch/zuul-route-health-spring-cloud-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/zuul-route-health-spring-cloud-starter)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/zuul-route-health-spring-cloud-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/zuul-route-health-spring-cloud-starter?branch=master)

## Features

Configures Spring Boot's Actuator health indicator that indicates if for every *explicitly* configured route 
a backing service exists in the discovery service.

## Setup

Add the Spring Cloud starter to your project:

## Properties

Property `zuul.health.enabled` let's you enable this extension.

```
zuul.health.enabled=true # true by default
```

## License

Apache 2.0