package uk.gov.dhsc.htbhf.smartstub.helper;

import uk.gov.dhsc.htbhf.smartstub.model.AddressDTO;
import uk.gov.dhsc.htbhf.smartstub.model.PersonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Test data factory for {@link PersonDTO} objects.
 */
public class PersonTestFactory {

    private static final LocalDate DOB = LocalDate.parse("1985-12-31");
    private static final String ADDRESS_LINE_1 = "Flat b";
    private static final String ADDRESS_LINE_2 = "123 Fake street";
    private static final String TOWN_OR_CITY = "Springfield";
    private static final String POSTCODE = "AA1 1AA";
    private static final String NINO = "EB123456C";
    private static final String FORENAME = "Lisa";
    private static final String SURNAME = "Simpson";
    private static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    private static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    private static final BigDecimal UC_MONTHLY_INCOME_THRESHOLD = BigDecimal.valueOf(408);

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is ineligible.
     */
    public static PersonDTO aPersonWhoIsIneligible() {
        final String nino = "IA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is eligible.
     */
    public static PersonDTO aPersonWhoIsEligible() {
        final String nino = "EA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person who is pending.
     */
    public static PersonDTO aPersonWhoIsPending() {
        final String nino = "PA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person with children under one.
     * Note, the same value is used to set the number of children under four as a child under one is also under four.
     */
    public static PersonDTO aPersonWithChildrenUnderOne(Integer numberOfChildren) {
        final String nino = String.format("EA%d%d0000C", numberOfChildren, numberOfChildren);
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person with children under four.
     */
    public static PersonDTO aPersonWithChildrenUnderFour(Integer numberOfChildren) {
        final String nino = String.format("EA0%d0000C", numberOfChildren);
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person with children under one and four.
     */
    public static PersonDTO aPersonWithChildren(Integer childrenUnderOne, Integer childrenUnderFour) {
        final String nino = String.format("EA%d%d0000C", childrenUnderOne, childrenUnderFour);
        return buildDefaultPerson().nino(nino).build();
    }

    /**
     * Creates a {@link PersonDTO} request object with a nino that encodes to a person not found.
     */
    public static PersonDTO aPersonNotFound() {
        final String nino = "DA000000C";
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
                .dateOfBirth(DOB)
                .nino(NINO)
                .address(aValidAddress())
                .forename(FORENAME)
                .surname(SURNAME)
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ucMonthlyIncomeThreshold(UC_MONTHLY_INCOME_THRESHOLD);
    }

    private static AddressDTO aValidAddress() {
        return AddressDTO.builder()
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .townOrCity(TOWN_OR_CITY)
                .postcode(POSTCODE)
                .build();
    }
}