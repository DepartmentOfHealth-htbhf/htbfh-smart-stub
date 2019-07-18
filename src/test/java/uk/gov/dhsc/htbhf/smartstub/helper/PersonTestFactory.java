package uk.gov.dhsc.htbhf.smartstub.helper;

import uk.gov.dhsc.htbhf.smartstub.model.PersonDTO;
import uk.gov.dhsc.htbhf.smartstub.service.BenefitsService;

import static uk.gov.dhsc.htbhf.smartstub.helper.AddressDTOTestDataFactory.aValidAddress;
import static uk.gov.dhsc.htbhf.smartstub.helper.TestConstants.HOMER_DATE_OF_BIRTH;
import static uk.gov.dhsc.htbhf.smartstub.helper.TestConstants.HOMER_FIRST_NAME;
import static uk.gov.dhsc.htbhf.smartstub.helper.TestConstants.SIMPSON_LAST_NAME;

/**
 * Test data factory for {@link PersonDTO} objects.
 */
public class PersonTestFactory {

    private static final String NINO = "EB123456C";

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is ineligible according to DWP (First character is 'I').
     */
    public static PersonDTO aPersonWhoIsDWPIneligible() {
        final String nino = "IA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is ineligible according to DWP (Second character is 'I').
     */
    public static PersonDTO anAlternatePersonWhoIsDWPIneligible() {
        final String nino = "AI000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is ineligible according to HMRC.
     */
    public static PersonDTO aPersonWhoIsHMRCIneligible() {
        final String nino = "AI000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is eligible according to DWP (First character is 'E').
     */
    public static PersonDTO aPersonWhoIsDWPEligible() {
        final String nino = "EA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is eligible according to DWP (Second character is 'E').
     */
    public static PersonDTO anAlternatePersonWhoIsDWPEligible() {
        final String nino = "AE000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is eligible according to HMRC.
     */
    public static PersonDTO aPersonWhoIsHMRCEligible() {
        final String nino = "AE000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is pending according to DWP (First character is 'P').
     */
    public static PersonDTO aPersonWhoIsDWPPending() {
        final String nino = "PA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is pending according to DWP (Second character is 'P').
     */
    public static PersonDTO anAlternatePersonWhoIsDWPPending() {
        final String nino = "AP000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is pending according to HMRC.
     */
    public static PersonDTO aPersonWhoIsHMRCPending() {
        final String nino = "AP000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that will trigger an Exception from the service
     */
    public static PersonDTO aPersonWhoWillTriggerAnError() {
        return buildDefaultPerson().nino(BenefitsService.EXCEPTIONAL_NINO).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person with children under one.
     * Note, the same value is used to set the number of children under four as a child under one is also under four.
     */
    public static PersonDTO aPersonWithChildrenUnderOne(Integer numberOfChildren) {
        final String nino = String.format("EE%d%d0000C", numberOfChildren, numberOfChildren);
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person with children under four.
     */
    public static PersonDTO aPersonWithChildrenUnderFour(Integer numberOfChildren) {
        final String nino = String.format("EE0%d0000C", numberOfChildren);
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person with children under one and four.
     */
    public static PersonDTO aPersonWithChildren(Integer childrenUnderOne, Integer childrenUnderFour) {
        final String nino = String.format("EE%d%d0000C", childrenUnderOne, childrenUnderFour);
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person not found.
     */
    public static PersonDTO aPersonNotFound() {
        final String nino = "DD000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with no nino.
     */
    public static PersonDTO aPersonWithNoNino() {
        return buildDefaultPerson().nino(null).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with no date of birth.
     */
    public static PersonDTO aPersonWithNoDateOfBirth() {
        return buildDefaultPerson().dateOfBirth(null).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with no address.
     */
    public static PersonDTO aPersonWithNoAddress() {
        return buildDefaultPerson().address(null).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with an invalid nino.
     */
    public static PersonDTO aPersonWithAnInvalidNino() {
        return buildDefaultPerson().nino("ab123").build();
    }

    private static PersonDTO.PersonDTOBuilder buildDefaultPerson() {
        return PersonDTO.builder()
                .dateOfBirth(HOMER_DATE_OF_BIRTH)
                .nino(NINO)
                .address(aValidAddress())
                .forename(HOMER_FIRST_NAME)
                .surname(SIMPSON_LAST_NAME);
    }

}
