package kw.gov.moh.breastcancer.identity;

import kw.gov.moh.breastcancer.dto.ParticipantIdentity;

import java.util.Optional;

/**
 * Source of trusted identity attributes (FR-002).
 * Sahel only sends civilId – the real implementation is PENDING
 * (Sahel-provided data / PACI / MOH identity service – to be confirmed).
 *
 * Contract: governorate / maritalStatus values are returned as the
 * question option VALUE codes used in the questionnaire seed data.
 */
public interface IdentityProvider {
    Optional<ParticipantIdentity> findByCivilId(String civilId);
}