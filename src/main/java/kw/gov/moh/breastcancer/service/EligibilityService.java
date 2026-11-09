package kw.gov.moh.breastcancer.service;

import kw.gov.moh.breastcancer.config.SurveyProperties;
import kw.gov.moh.breastcancer.dto.ParticipantIdentity;
import kw.gov.moh.breastcancer.exception.SurveyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

/** Jira 1 – FR-001..004. */
@Service
@RequiredArgsConstructor
public class EligibilityService {

    private static final Set<String> KUWAITI = Set.of("KWT", "KW", "KUWAITI", "KUWAIT");
    private static final Set<String> FEMALE = Set.of("F", "FEMALE");

    private final SurveyProperties props;
    private final Clock clock;

    /** FR-002/003: required attributes present, otherwise survey must not start. */
    public void requireVerifiedIdentity(ParticipantIdentity identity) {
        if (identity == null || !identity.isComplete()) {
            throw SurveyMessage.IDENTITY_UNAVAILABLE.ex();
        }
    }

    /** FR-001: checked only when a NEW participation starts. Returns age at start. */
    public int checkNewParticipant(ParticipantIdentity identity) {
        requireVerifiedIdentity(identity);
        int age = ageOf(identity.dateOfBirth());
        boolean eligible = FEMALE.contains(identity.gender().trim().toUpperCase())
                && KUWAITI.contains(identity.nationality().trim().toUpperCase())
                && age >= props.minAge() && age <= props.maxAge();
        if (!eligible) {
            throw SurveyMessage.NOT_ELIGIBLE.ex();
        }
        return age;
    }

    public int ageOf(LocalDate dob) {
        return Period.between(dob, LocalDate.now(clock)).getYears();
    }

    /** Five-year groups 35-39 ... 65-69 (FR-026). */
    public String ageGroup(int age) {
        int lower = props.minAge() + ((age - props.minAge()) / 5) * 5;
        return lower + "-" + (lower + 4);
    }
}