package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SahelFormResult {

    @JsonProperty("NameAr")
    private String nameAr;

    @JsonProperty("NameEn")
    private String nameEn;

    @JsonProperty("HintAr")
    private String hintAr;

    @JsonProperty("HintEn")
    private String hintEn;

    /** 1..n for multi-step; 0 = single step. */
    @JsonProperty("StepNo")
    private Integer stepNo;

    /** Terms URL – shows agreement checkbox; null if not used. */
    @JsonProperty("TOS")
    private String tos;

    @JsonProperty("ButtonLabelAr")
    private String buttonLabelAr;

    @JsonProperty("ButtonLabelEn")
    private String buttonLabelEn;

    @Builder.Default
    @JsonProperty("ControlsModels")
    private List<SahelControlModel> controlsModels = new ArrayList<>();
}