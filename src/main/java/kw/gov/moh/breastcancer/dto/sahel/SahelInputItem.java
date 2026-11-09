package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * One entry of the POST body array: [{Key, Value, Type, Extension}].
 * Sahel resends ALL answers from all previous steps on every POST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SahelInputItem {

    @JsonProperty("Key")
    @JsonAlias("key")
    private String key;

    /** CheckBoxList format to confirm with Sahel (e.g. "1,3"). */
    @JsonProperty("Value")
    @JsonAlias("value")
    private String value;

    /** Doc says Int, examples send "Dropdown" – String accepts both. */
    @JsonProperty("Type")
    @JsonAlias("type")
    private String type;

    @JsonProperty("Extension")
    @JsonAlias("extension")
    private String extension;
}