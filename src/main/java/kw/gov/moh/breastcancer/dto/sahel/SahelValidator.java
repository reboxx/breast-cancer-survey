package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SahelValidator {

    @JsonProperty("Regex")
    private String regex;

    @JsonProperty("MessageAr")
    private String messageAr;

    @JsonProperty("MessageEn")
    private String messageEn;
}