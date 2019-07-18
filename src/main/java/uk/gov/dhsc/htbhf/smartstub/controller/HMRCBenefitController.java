package uk.gov.dhsc.htbhf.smartstub.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dhsc.htbhf.smartstub.model.BenefitDTO;
import uk.gov.dhsc.htbhf.smartstub.model.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.smartstub.service.BenefitsService;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/hmrc/benefits")
@Slf4j
@AllArgsConstructor
public class HMRCBenefitController {

    private BenefitsService benefitsService;

    @PostMapping
    public BenefitDTO getBenefits(@RequestBody @Valid HMRCEligibilityRequest eligibilityRequest) {
        log.debug("Received HMRC eligibility request {}", eligibilityRequest);
        BenefitDTO benefits = benefitsService.getHMRCBenefits(eligibilityRequest.getPerson().getNino());
        log.debug("Sending HRMC response {}", benefits);
        return benefits;
    }
}
