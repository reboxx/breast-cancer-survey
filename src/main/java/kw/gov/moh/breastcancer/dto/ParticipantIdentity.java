package kw.gov.moh.breastcancer.dto;

import java.time.LocalDate;

/**
 * Trusted identity attributes used for eligibility (FR-001/002).
 * Source still to be confirmed – Sahel only sends civilId.
 */
public record ParticipantIdentity(
        String civilId,
        LocalDate dateOfBirth,
        String gender,          // F / M
        String nationality,     // KWT ...
        String governorate,     // may be null
        String maritalStatus,   // "available on Sahel" – may be null
        Boolean hasChildren     // "available on Sahel" – may be null
) {
    public boolean isComplete() {
        return civilId != null && dateOfBirth != null && gender != null && nationality != null;
    }
}