package uk.gov.dhsc.htbhf.smartstub.controller.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;
import uk.gov.dhsc.htbhf.smartstub.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.smartstub.model.v2.VerificationOutcome;
import uk.gov.dhsc.htbhf.smartstub.service.v2.IdentityAndEligibilityService;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertInternalServerErrorResponse;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.smartstub.helper.TestConstants.SIMPSON_LAST_NAME;
import static uk.gov.dhsc.htbhf.smartstub.helper.TestConstants.TWO_CHILDREN;
import static uk.gov.dhsc.htbhf.smartstub.helper.TestConstants.VALID_NINO_V2;
import static uk.gov.dhsc.htbhf.smartstub.helper.v2.HttpRequestTestDataFactory.aValidEligibilityHttpEntity;
import static uk.gov.dhsc.htbhf.smartstub.helper.v2.HttpRequestTestDataFactory.anHttpEntityWithNinoAndSurname;
import static uk.gov.dhsc.htbhf.smartstub.helper.v2.HttpRequestTestDataFactory.anInvalidEligibilityHttpEntity;
import static uk.gov.dhsc.htbhf.smartstub.helper.v2.IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchFailedResponse;
import static uk.gov.dhsc.htbhf.smartstub.helper.v2.IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches;
import static uk.gov.dhsc.htbhf.smartstub.helper.v2.IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithMatches;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DWPBenefitControllerV2Test {

    private static final URI ENDPOINT = URI.create("/v2/dwp/benefits");
    private static final String IDENTITY_NOT_MATCHED_NINO = "AB123456D";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnOkResponseWithAllMatchResponse() {
        //Given
        HttpEntity request = aValidEligibilityHttpEntity();

        //When
        ResponseEntity<IdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(ENDPOINT,
                HttpMethod.GET, request, IdentityAndEligibilityResponse.class);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches(TWO_CHILDREN));
    }

    @Test
    void shouldReturnOkResponseWithIdentityStatusNotMatchedResponse() {
        //Given - making sure we test that the NINO is used from the request
        HttpEntity request = anHttpEntityWithNinoAndSurname(IDENTITY_NOT_MATCHED_NINO, SIMPSON_LAST_NAME);

        //When
        ResponseEntity<IdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(ENDPOINT,
                HttpMethod.GET, request, IdentityAndEligibilityResponse.class);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(anIdentityMatchFailedResponse());
    }

    @Test
    void shouldReturnOkResponseWithMobileNotMatchedVerificationResponse() {
        //Given - making sure we test that the surname is used from the request
        HttpEntity request = anHttpEntityWithNinoAndSurname(VALID_NINO_V2, "MobileNotMatched");

        //When
        ResponseEntity<IdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(ENDPOINT,
                HttpMethod.GET, request, IdentityAndEligibilityResponse.class);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        IdentityAndEligibilityResponse response = anIdentityMatchedEligibilityConfirmedUCResponseWithMatches(
                VerificationOutcome.NOT_MATCHED,
                VerificationOutcome.MATCHED,
                TWO_CHILDREN
        );
        assertThat(responseEntity.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnBadRequestResponseWithInvalidRequest() {
        //Given
        HttpEntity request = anInvalidEligibilityHttpEntity();

        //When
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.exchange(ENDPOINT, HttpMethod.GET, request, ErrorResponse.class);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertValidationErrorInResponse(responseEntity, "person.nino",
                "must match \"^(?!BG|GB|NK|KN|TN|NT|ZZ)[A-CEGHJ-PR-TW-Z][A-CEGHJ-NPR-TW-Z](\\d{6})[A-D]$\"");
    }

    @Test
    void shouldReturnBadRequestResponseWithExceptionalNino() {
        //Given
        HttpEntity request = anHttpEntityWithNinoAndSurname(IdentityAndEligibilityService.EXCEPTION_NINO, SIMPSON_LAST_NAME);

        //When
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.exchange(ENDPOINT, HttpMethod.GET, request, ErrorResponse.class);

        //Then
        assertInternalServerErrorResponse(responseEntity);
    }

}