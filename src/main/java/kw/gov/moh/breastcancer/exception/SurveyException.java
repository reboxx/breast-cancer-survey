package kw.gov.moh.breastcancer.exception;

import lombok.Getter;

/** Business rejection returned to Sahel as IsSuccess=false. */
@Getter
public class SurveyException extends RuntimeException {

    private final String messageAr;
    private final String messageEn;

    public SurveyException(String messageAr, String messageEn) {
        super(messageEn);
        this.messageAr = messageAr;
        this.messageEn = messageEn;
    }
}