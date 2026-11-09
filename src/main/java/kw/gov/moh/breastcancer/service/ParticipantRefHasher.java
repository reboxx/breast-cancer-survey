package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.config.SurveyProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

/** Indirect participant reference: HMAC-SHA256(civilId) – raw civil ID is never stored (Jira 8). */
// @Component
public class ParticipantRefHasher {

    private static final String ALG = "HmacSHA256";
    private final SecretKeySpec key;

    public ParticipantRefHasher(SurveyProperties props) {
        if (props.participantRefSecret() == null || props.participantRefSecret().length() < 32) {
            throw new IllegalStateException("survey.participant-ref-secret must be at least 32 chars");
        }
        this.key = new SecretKeySpec(props.participantRefSecret().getBytes(StandardCharsets.UTF_8), ALG);
    }

    public String hash(String civilId) {
        try {
            Mac mac = Mac.getInstance(ALG);
            mac.init(key);
            return HexFormat.of().formatHex(mac.doFinal(civilId.trim().getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot hash participant reference", e);
        }
    }
}