package uk.gov.dhsc.htbhf.smartstub.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;
import uk.gov.dhsc.htbhf.smartstub.model.BenefitDTO;
import uk.gov.dhsc.htbhf.smartstub.model.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.smartstub.model.PersonDTO;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertInternalServerErrorResponse;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.INELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.PENDING;
import static uk.gov.dhsc.htbhf.smartstub.controller.IntegrationTestAssertions.assertSuccessfulResponse;
import static uk.gov.dhsc.htbhf.smartstub.controller.IntegrationTestAssertions.assertSuccessfulResponseWithCorrectNumberOfChildren;
import static uk.gov.dhsc.htbhf.smartstub.helper.HMRCEligibilityRequestTestDataFactory.anHMRCEligibilityRequest;
import static uk.gov.dhsc.htbhf.smartstub.helper.HMRCEligibilityRequestTestDataFactory.anHMRCEligibilityRequestWithPerson;
import static uk.gov.dhsc.htbhf.smartstub.helper.PersonTestFactory.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HMRCBenefitControllerIntegrationTest {

    private static final URI ENDPOINT = URI.create("/v1/hmrc/benefits");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnNoChildrenForMatchingNino() {
        HMRCEligibilityRequest anEligibilityRequest = anHMRCEligibilityRequest();

        ResponseEntity<BenefitDTO> benefit = restTemplate.postForEntity(ENDPOINT, anEligibilityRequest, BenefitDTO.class);

        assertSuccessfulResponseWithCorrectNumberOfChildren(benefit, ELIGIBLE, 0, 0);
    }

    @Test
    void shouldReturnTwoChildrenUnderOneForMatchingNino() {
        PersonDTO person = aPersonWithChildrenUnderOne(2);
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<BenefitDTO> benefit = restTemplate.postForEntity(ENDPOINT, request, BenefitDTO.class);

        assertSuccessfulResponseWithCorrectNumberOfChildren(benefit, ELIGIBLE, 2, 2);
    }

    @Test
    void shouldReturnTwoChildrenUnderFourForMatchingNino() {
        PersonDTO person = aPersonWithChildrenUnderFour(2);
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<BenefitDTO> benefit = restTemplate.postForEntity(ENDPOINT, request, BenefitDTO.class);

        assertSuccessfulResponseWithCorrectNumberOfChildren(benefit, ELIGIBLE, 0, 2);
    }

    @Test
    void shouldReturnNoMatchForUmatchedNino() {
        PersonDTO person = aPersonNotFound();
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<BenefitDTO> benefit = restTemplate.postForEntity(ENDPOINT, request, BenefitDTO.class);

        assertSuccessfulResponse(benefit, NO_MATCH);
        assertThat(benefit.getBody().getNumberOfChildrenUnderOne()).isNull();
        assertThat(benefit.getBody().getNumberOfChildrenUnderFour()).isNull();
    }

    @Test
    void shouldReturnSameChildrenUnder1AndChildrenUnder4WhenChildrenUnder1TooHigh() {
        PersonDTO person = aPersonWithChildren(3, 1);
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<BenefitDTO> benefit = restTemplate.postForEntity(ENDPOINT, request, BenefitDTO.class);

        assertSuccessfulResponseWithCorrectNumberOfChildren(benefit, ELIGIBLE, 1, 1);
    }

    @Test
    void shouldReturnIneligibleForMatchingNino() {
        shouldReturnExpectedStatusForMatchingNino(aPersonWhoIsHMRCIneligible(), INELIGIBLE);
    }

    @Test
    void shouldReturnEligibleForMatchingNino() {
        shouldReturnExpectedStatusForMatchingNino(aPersonWhoIsHMRCEligible(), ELIGIBLE);
    }

    @Test
    void shouldReturnPendingForMatchingNino() {
        shouldReturnExpectedStatusForMatchingNino(aPersonWhoIsHMRCPending(), PENDING);
    }

    private void shouldReturnExpectedStatusForMatchingNino(PersonDTO person, EligibilityStatus expectedStatus) {
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<BenefitDTO> benefit = restTemplate.postForEntity(ENDPOINT, request, BenefitDTO.class);

        assertSuccessfulResponseWithCorrectNumberOfChildren(benefit, expectedStatus, 0, 0);
    }

    @Test
    void shouldReturnBadRequestForMissingNino() {
        PersonDTO person = aPersonWithNoNino();
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<ErrorResponse> errorResponse = restTemplate.postForEntity(ENDPOINT, request, ErrorResponse.class);

        assertValidationErrorInResponse(errorResponse, "person.nino", "must not be null");
    }

    @Test
    void shouldReturnBadRequestForInvalidNino() {
        PersonDTO person = aPersonWithAnInvalidNino();
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<ErrorResponse> errorResponse = restTemplate.postForEntity(ENDPOINT, request, ErrorResponse.class);

        assertValidationErrorInResponse(errorResponse, "person.nino", "must match \"[a-zA-Z]{2}\\d{6}[a-dA-D]\"");
    }

    @Test
    void shouldReturnBadRequestForMissingDateOfBirth() {
        PersonDTO person = aPersonWithNoDateOfBirth();
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<ErrorResponse> errorResponse = restTemplate.postForEntity(ENDPOINT, request, ErrorResponse.class);

        assertValidationErrorInResponse(errorResponse, "person.dateOfBirth", "must not be null");
    }

    @Test
    void shouldReturnBadRequestForMissingAddress() {
        PersonDTO person = aPersonWithNoAddress();
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<ErrorResponse> errorResponse = restTemplate.postForEntity(ENDPOINT, request, ErrorResponse.class);

        assertValidationErrorInResponse(errorResponse, "person.address", "must not be null");
    }

    @Test
    void shouldReturnServiceUnavailableForExceptionalNino() {
        PersonDTO person = aPersonWhoWillTriggerAnError();
        HMRCEligibilityRequest request = anHMRCEligibilityRequestWithPerson(person);

        ResponseEntity<ErrorResponse> error = restTemplate.postForEntity(ENDPOINT, request, ErrorResponse.class);

        assertInternalServerErrorResponse(error);
    }

}
