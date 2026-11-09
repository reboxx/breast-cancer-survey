package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SahelOption {

    @JsonProperty("Value")
    private String value;

    @JsonProperty("TextAr")
    private String textAr;

    @JsonProperty("TextEn")
    private String textEn;
}