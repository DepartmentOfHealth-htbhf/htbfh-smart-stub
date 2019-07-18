package uk.gov.dhsc.htbhf.smartstub.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.smartstub.model.BenefitDTO;
import uk.gov.dhsc.htbhf.smartstub.model.ChildDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.nCopies;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.INELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.PENDING;

/**
 * Service for creating a {@link BenefitDTO} response based on a given national insurance number (nino).
 * See README.md for details on mappings.
 */
@Service
@AllArgsConstructor
@Slf4j
public class BenefitsService {

    public static final String EXCEPTIONAL_NINO = "ZZ999999D";
    private static final int DWP_ELIGIBILITY_STATUS_FIRST_POSITION = 0;
    private static final int DWP_ELIGIBILITY_STATUS_SECOND_POSITION = 1;
    private static final int HMRC_ELIGIBILITY_STATUS_POSITION = 1;
    private static final int CHILDREN_UNDER_ONE_POSITION = 2;
    private static final int CHILDREN_UNDER_FOUR_POSITION = 3;
    private static final Map<Character, EligibilityStatus> ELIGIBILITY_STATUS_MAP = Map.of('E', ELIGIBLE, 'I', INELIGIBLE, 'P', PENDING);

    private final IdentifierService identifierService;

    public BenefitDTO getDWPBenefits(String nino) {
        String householdIdentifier = identifierService.getDWPHouseholdIdentifier(nino);
        return getBenefits(nino, householdIdentifier);
    }

    private BenefitDTO getBenefits(String nino, String householdIdentifier) {
        if (EXCEPTIONAL_NINO.equals(nino)) {
            String message = "NINO provided (" + EXCEPTIONAL_NINO + ") has been configured to trigger an Exception";
            log.info(message);
            throw new IllegalArgumentException(message);
        }
        char[] ninoChars = nino.toCharArray();
        EligibilityStatus status = getEligibilityStatus(ninoChars);
        if (status == NO_MATCH) {
            return BenefitDTO.builder().eligibilityStatus(NO_MATCH).build();
        }

        Integer childrenUnderFour = getNumberOfChildrenUnderFour(ninoChars);
        Integer childrenUnderOne = getNumberOfChildrenUnderOne(childrenUnderFour, ninoChars);

        return BenefitDTO.builder()
                .eligibilityStatus(status)
                .numberOfChildrenUnderOne(childrenUnderOne)
                .numberOfChildrenUnderFour(childrenUnderFour)
                .householdIdentifier(householdIdentifier)
                .children(createChildren(childrenUnderOne, childrenUnderFour))
                .build();
    }

    private EligibilityStatus getEligibilityStatus(char[] ninoChars) {
        // Either of the first two characters can be used to determine eligibility. If the first character maps to NO_MATCH, then check the second character.
        EligibilityStatus status = ELIGIBILITY_STATUS_MAP.getOrDefault(ninoChars[DWP_ELIGIBILITY_STATUS_FIRST_POSITION], NO_MATCH);
        if (status == NO_MATCH) {
            return ELIGIBILITY_STATUS_MAP.getOrDefault(ninoChars[DWP_ELIGIBILITY_STATUS_SECOND_POSITION], NO_MATCH);
        }
        return status;
    }

    public BenefitDTO getHMRCBenefits(String nino) {
        String householdIdentifier = identifierService.getHMRCHouseholdIdentifier(nino);
        return getBenefits(nino, HMRC_ELIGIBILITY_STATUS_POSITION, householdIdentifier);
    }

    private BenefitDTO getBenefits(String nino, int eligibilityStatusPosition, String householdIdentifier) {
        if (EXCEPTIONAL_NINO.equals(nino)) {
            String message = "NINO provided (" + EXCEPTIONAL_NINO + ") has been configured to trigger an Exception";
            log.info(message);
            throw new IllegalArgumentException(message);
        }
        char[] ninoChars = nino.toCharArray();
        var status = ELIGIBILITY_STATUS_MAP.getOrDefault(ninoChars[eligibilityStatusPosition], NO_MATCH);
        if (status == NO_MATCH) {
            return BenefitDTO.builder().eligibilityStatus(NO_MATCH).build();
        }

        Integer childrenUnderFour = getNumberOfChildrenUnderFour(ninoChars);
        Integer childrenUnderOne = getNumberOfChildrenUnderOne(childrenUnderFour, ninoChars);

        return BenefitDTO.builder()
                .eligibilityStatus(status)
                .numberOfChildrenUnderOne(childrenUnderOne)
                .numberOfChildrenUnderFour(childrenUnderFour)
                .householdIdentifier(householdIdentifier)
                .children(createChildren(childrenUnderOne, childrenUnderFour))
                .build();
    }

    private List<ChildDTO> createChildren(Integer numberOfChildrenUnderOne, Integer numberOfChildrenUnderFour) {
        List<ChildDTO> childrenUnderOne = nCopies(numberOfChildrenUnderOne, new ChildDTO(LocalDate.now().minusMonths(6)));
        List<ChildDTO> childrenBetweenOneAndFour = nCopies(numberOfChildrenUnderFour - numberOfChildrenUnderOne, new ChildDTO(LocalDate.now().minusYears(3)));
        return Stream.concat(childrenUnderOne.stream(), childrenBetweenOneAndFour.stream()).collect(Collectors.toList());
    }

    private Integer getNumberOfChildrenUnderOne(Integer childrenUnderFour, char[] nino) {
        Integer childrenUnderOne = Character.getNumericValue(nino[CHILDREN_UNDER_ONE_POSITION]);
        return (childrenUnderOne > childrenUnderFour) ? childrenUnderFour : childrenUnderOne;
    }

    private Integer getNumberOfChildrenUnderFour(char[] nino) {
        return Character.getNumericValue(nino[CHILDREN_UNDER_FOUR_POSITION]);
    }
}
