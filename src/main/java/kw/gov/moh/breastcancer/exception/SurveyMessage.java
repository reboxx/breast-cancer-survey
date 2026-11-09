package kw.gov.moh.breastcancer.exception;

/** User-facing messages. Arabic/English wording to be approved by Public Health. */
public enum SurveyMessage {

    INVALID_CIVIL_ID(
            "الرقم المدني غير صحيح",
            "Invalid Civil ID."),
    IDENTITY_UNAVAILABLE(
            "تعذر التحقق من بياناتك حالياً، يرجى المحاولة لاحقاً",
            "We could not verify your details at the moment. Please try again later."),
    NOT_ELIGIBLE(
            "عذراً، هذا الاستبيان مخصص للمواطنات الكويتيات من عمر 35 إلى 69 سنة",
            "Sorry, this survey is for Kuwaiti women aged 35 to 69."),
    NO_ACTIVE_CAMPAIGN(
            "الاستبيان غير متاح حالياً",
            "The survey is not available at the moment."),
    ALREADY_SUBMITTED(
            "لقد قمتِ بإرسال هذا الاستبيان مسبقاً، شكراً لمشاركتك",
            "You have already submitted this survey. Thank you for your participation."),
    NO_DRAFT(
            "يرجى بدء الاستبيان من جديد",
            "Please start the survey again."),
    CONFIRM_REQUIRED(
            "يرجى تأكيد الإجابات قبل الإرسال",
            "Please confirm your answers before submitting."),
    GENERAL_ERROR(
            "حدث خطأ، يرجى المحاولة لاحقاً",
            "Something went wrong. Please try again later.");

    private final String ar;
    private final String en;

    SurveyMessage(String ar, String en) { this.ar = ar; this.en = en; }

    public String ar() { return ar; }
    public String en() { return en; }

    public SurveyException ex() { return new SurveyException(ar, en); }
}