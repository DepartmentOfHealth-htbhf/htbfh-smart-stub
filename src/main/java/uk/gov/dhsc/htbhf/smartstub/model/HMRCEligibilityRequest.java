package uk.gov.dhsc.htbhf.smartstub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class HMRCEligibilityRequest {

    @NotNull
    @Valid
    @JsonProperty("person")
    private PersonDTO person;

    @NotNull
    @JsonProperty("ctcAnnualIncomeThreshold")
    private final BigDecimal ctcAnnualIncomeThreshold;

    @NotNull
    @JsonProperty("eligibleStartDate")
    private final LocalDate eligibleStartDate;

    @NotNull
    @JsonProperty("eligibleEndDate")
    private final LocalDate eligibleEndDate;
}
