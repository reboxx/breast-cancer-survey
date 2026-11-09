package kw.gov.moh.breastcancer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@EnableConfigurationProperties(SurveyProperties.class)
public class SurveyConfig {

    public static final ZoneId KUWAIT = ZoneId.of("Asia/Kuwait");

    @Bean
    public Clock clock() {
        return Clock.system(KUWAIT);
    }
}