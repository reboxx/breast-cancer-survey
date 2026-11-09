package kw.gov.moh.breastcancer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "survey")
public record SurveyProperties(
        String participantRefSecret,   // HMAC secret for civil ID hashing (from vault/env)
        String infoActionType,         // Sahel actionType for MOH info link (from Sahel portal)
        String multiValueSeparator,    // CheckBoxList value separator – confirm with Sahel
        int minAge,
        int maxAge
) {}