package uk.gov.dhsc.htbhf.smartstub.service;

import org.junit.jupiter.api.Test;
import uk.gov.dhsc.htbhf.smartstub.model.BenefitDTO;
import uk.gov.dhsc.htbhf.smartstub.model.PersonDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.INELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.PENDING;
import static uk.gov.dhsc.htbhf.smartstub.controller.IntegrationTestAssertions.assertCorrectNumberOfChildren;
import static uk.gov.dhsc.htbhf.smartstub.helper.PersonTestFactory.*;

class BenefitsServiceHMRCTest {

    private BenefitsService benefitsService = new BenefitsService(new IdentifierService());

    @Test
    void shouldReturnIneligibleForMatchingNino() {
        PersonDTO person = aPersonWhoIsHMRCIneligible();

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getEligibilityStatus()).isEqualTo(INELIGIBLE);
    }

    @Test
    void shouldReturnEligibleForMatchingNino() {
        PersonDTO person = aPersonWhoIsHMRCEligible();

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getEligibilityStatus()).isEqualTo(ELIGIBLE);
    }

    @Test
    void shouldReturnPendingForMatchingNino() {
        PersonDTO person = aPersonWhoIsHMRCPending();

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getEligibilityStatus()).isEqualTo(PENDING);
    }

    @Test
    void shouldReturnNoMatchNino() {
        PersonDTO person = aPersonNotFound();

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getEligibilityStatus()).isEqualTo(NO_MATCH);
        assertThat(benefit.getNumberOfChildrenUnderOne()).isNull();
        assertThat(benefit.getNumberOfChildrenUnderFour()).isNull();
    }

    @Test
    void shouldReturnTwoChildrenUnderFourForMatchingNino() {
        PersonDTO person = aPersonWithChildrenUnderFour(2);

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getNumberOfChildrenUnderOne()).isEqualTo(0);
        assertThat(benefit.getNumberOfChildrenUnderFour()).isEqualTo(2);
        assertCorrectNumberOfChildren(benefit, 0, 2);
    }

    @Test
    void shouldReturnThreeChildrenUnderOneForMatchingNino() {
        PersonDTO person = aPersonWithChildrenUnderOne(3);

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getNumberOfChildrenUnderOne()).isEqualTo(3);
        assertThat(benefit.getNumberOfChildrenUnderFour()).isEqualTo(3);
        assertCorrectNumberOfChildren(benefit, 3, 3);
    }

    @Test
    void shouldReturnSameNumberOfChildrenUnder1AndUnder4WhenRequestHasUnder1LargerThanUnder4() {
        PersonDTO person = aPersonWithChildren(4, 1);

        BenefitDTO benefit = benefitsService.getHMRCBenefits(person.getNino());

        assertThat(benefit.getNumberOfChildrenUnderOne()).isEqualTo(1);
        assertThat(benefit.getNumberOfChildrenUnderFour()).isEqualTo(1);
        assertCorrectNumberOfChildren(benefit, 1, 1);
    }

    @Test
    void shouldThrowExceptionWhenErrorNinoSupplied() {
        PersonDTO person = aPersonWhoWillTriggerAnError();

        assertThrows(IllegalArgumentException.class, () -> benefitsService.getHMRCBenefits(person.getNino()));
    }
}
