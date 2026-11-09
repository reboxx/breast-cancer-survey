package kw.gov.moh.breastcancer;

import kw.gov.moh.breastcancer.controller.SahelSurveyController;
import kw.gov.moh.breastcancer.config.SecurityConfig;
import kw.gov.moh.breastcancer.dto.sahel.SahelServiceResponse;
import kw.gov.moh.breastcancer.service.SahelSurveyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SahelSurveyController.class)
@Import(SecurityConfig.class)
class SahelSurveySecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SahelSurveyService service;

    @Test
    void postEndpointDoesNotRequireBasicAuthenticationOrCsrfToken() throws Exception {
        when(service.submitStep(eq("292041501116"), eq(1), eq("ar"), anyList()))
                .thenReturn(SahelServiceResponse.fail("test", "test"));

        mockMvc.perform(post("/api/v1/sahel")
                        .queryParam("civilId", "292041501116")
                        .queryParam("stepNo", "1")
                        .queryParam("lang", "ar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void getEndpointDoesNotRequireBasicAuthentication() throws Exception {
        when(service.start(eq("292041501116"), eq("en")))
                .thenReturn(SahelServiceResponse.fail("test", "test"));

        mockMvc.perform(get("/api/v1/sahel")
                        .queryParam("civilId", "292041501116")
                        .queryParam("lang", "en"))
                .andExpect(status().isOk());
    }
}
