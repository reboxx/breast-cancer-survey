package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SahelServiceResponse {

    @JsonProperty("IsSuccess")
    private boolean success;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("MessageEn")
    private String messageEn;

    /** success | error | warning | info – optional. */
    @JsonProperty("AlertType")
    private String alertType;

    /** Next form; null when failed or finished. */
    @JsonProperty("Result")
    private SahelFormResult result;

    @JsonProperty("Actions")
    private List<SahelAction> actions;

    @JsonProperty("DataTableList")
    private List<SahelDataTableItem> dataTableList;

    // ---------- factories ----------

    /** Show next step. */
    public static SahelServiceResponse step(SahelFormResult form) {
        return SahelServiceResponse.builder()
                .success(true).message("").messageEn("")
                .result(form)
                .build();
    }

    /** Stop with message (ineligible, closed campaign, validation error...). */
    public static SahelServiceResponse fail(String messageAr, String messageEn) {
        return SahelServiceResponse.builder()
                .success(false).message(messageAr).messageEn(messageEn)
                .alertType("error")
                .build();
    }

    /** Final step – Result null ends the flow. */
    public static SahelServiceResponse finish(String messageAr, String messageEn, List<SahelAction> actions) {
        return SahelServiceResponse.builder()
                .success(true).message(messageAr).messageEn(messageEn)
                .alertType("success")
                .actions(actions)
                .build();
    }
}