package io.flexwork;

import io.flexwork.config.AsyncSyncConfiguration;
import io.flexwork.config.EmbeddedSQL;
import io.flexwork.config.FlexworkProperties;
import io.flexwork.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

/** Base composite annotation for integration tests. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
        classes = {FlexworkApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class})
@EnableConfigurationProperties({FlexworkProperties.class})
@EmbeddedSQL
public @interface IntegrationTest {}
