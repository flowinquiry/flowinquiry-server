package io.flowinquiry.config.hibernatecache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flowinquiry.hibernate.second-level-cache.redis")
public record RedisHibernateProps(
        Integer idleConnectionTimeout,
        Integer connectTimeout,
        Integer timeout,
        Integer retryAttempts,
        Integer retryInterval,
        String password,
        Integer subscriptionsPerConnection,
        String clientName,
        String address,
        Integer subscriptionConnectionMinimumIdleSize,
        Integer subscriptionConnectionPoolSize,
        Integer connectionMinimumIdleSize,
        Integer connectionPoolSize,
        Integer database,
        Integer dnsMonitoringInterval) {}
