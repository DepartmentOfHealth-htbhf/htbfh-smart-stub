package uk.gov.dhsc.htbhf.smartstub.helper;

import uk.gov.dhsc.htbhf.smartstub.model.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.smartstub.model.PersonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static uk.gov.dhsc.htbhf.smartstub.helper.PersonTestFactory.aPersonWhoIsHMRCEligible;

public class HMRCEligibilityRequestTestDataFactory {

    private static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    private static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    private static final BigDecimal CTC_MONTHLY_INCOME_THRESHOLD = BigDecimal.valueOf(11000);

    public static HMRCEligibilityRequest anHMRCEligibilityRequest() {
        return HMRCEligibilityRequest.builder()
                .person(aPersonWhoIsHMRCEligible())
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ctcAnnualIncomeThreshold(CTC_MONTHLY_INCOME_THRESHOLD)
                .build();
    }

    public static HMRCEligibilityRequest anHMRCEligibilityRequestWithPerson(PersonDTO person) {
        return HMRCEligibilityRequest.builder()
                .person(person)
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ctcAnnualIncomeThreshold(CTC_MONTHLY_INCOME_THRESHOLD)
                .build();
    }
}
