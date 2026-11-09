package kw.gov.moh.breastcancer.dto.sahel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SahelControlModel {

    /** Question code – comes back as InputItemModel.Key. */
    @JsonProperty("Id")
    private String id;

    /** Text, Checkbox, Dropdown, Radio, CheckBoxList, Hidden, Date, DataTable ... */
    @JsonProperty("Type")
    private String type;

    @JsonProperty("LabelAr")
    private String labelAr;

    @JsonProperty("LabelEn")
    private String labelEn;

    @JsonProperty("HintAr")
    private String hintAr;

    @JsonProperty("HintEn")
    private String hintEn;

    /** String per Sahel spec. */
    @JsonProperty("MaxLength")
    private String maxLength;

    @JsonProperty("IsRequired")
    private Boolean required;

    @JsonProperty("IsDisabled")
    private Boolean disabled;

    @JsonProperty("IsMultiline")
    private Boolean multiline;

    /** Prefill (resume draft / read-only Sahel data). */
    @JsonProperty("DefaultValue")
    private String defaultValue;

    @JsonProperty("AllowedExtensions")
    private String allowedExtensions;

    /** Date controls only, format yyyy-MM-dd. */
    @JsonProperty("FirstDate")
    private String firstDate;

    @JsonProperty("LastDate")
    private String lastDate;

    @JsonProperty("OptionControlModels")
    private List<SahelOption> optionControlModels;

    @JsonProperty("Validators")
    private List<SahelValidator> validators;

    /** Only when Type = DataTable. */
    @JsonProperty("DataTableList")
    private List<SahelDataTableItem> dataTableList;
}