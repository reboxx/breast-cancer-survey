package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SahelDataTableItem {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("LabelAr")
    private String labelAr;

    @JsonProperty("LabelEn")
    private String labelEn;

    @JsonProperty("Value")
    private String value;
}