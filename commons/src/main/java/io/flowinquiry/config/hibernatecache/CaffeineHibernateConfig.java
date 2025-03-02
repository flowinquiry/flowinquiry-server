package io.flowinquiry.config.hibernatecache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnProperty(
        prefix = "flowinquiry.hibernate.second_level_cache",
        name = "provider",
        havingValue = "caffeine",
        matchIfMissing = true)
@PropertySource("classpath:hibernatecache/hibernate-caffeine.properties")
public class CaffeineHibernateConfig {}
