package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/** Lower-camel names per Sahel doc section 4.1 "Actions". */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SahelAction {

    /** Value issued in Sahel admin portal (doc example: "download"). */
    @JsonProperty("actionType")
    private String actionType;

    /** Must be whitelisted by Sahel. */
    @JsonProperty("actionUrl")
    private String actionUrl;

    @JsonProperty("viewBag")
    private String viewBag;

    @JsonProperty("labelAr")
    private String labelAr;

    @JsonProperty("labelEn")
    private String labelEn;
}