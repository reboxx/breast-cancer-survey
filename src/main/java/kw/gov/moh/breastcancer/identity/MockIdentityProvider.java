package kw.gov.moh.breastcancer.identity;

import kw.gov.moh.breastcancer.dto.ParticipantIdentity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/** DEV/TEST ONLY – remove once the real identity source is agreed. */
@Component
@Profile({"dev", "test"})
public class MockIdentityProvider implements IdentityProvider {

    @Override
    public Optional<ParticipantIdentity> findByCivilId(String civilId) {
        return Optional.of(new ParticipantIdentity(
                civilId, LocalDate.of(1980, 5, 10), "F", "KWT",
                "2",     // governorate option value
                "2",     // marital status option value
                true));
    }
}