package uk.gov.dhsc.htbhf.smartstub.converter.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.TestConstants.HOMER_DATE_OF_BIRTH;
import static uk.gov.dhsc.htbhf.TestConstants.HOMER_DATE_OF_BIRTH_STRING;
import static uk.gov.dhsc.htbhf.TestConstants.UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE;

@ExtendWith(MockitoExtension.class)
class ConverterUtilsTest {

    @Mock
    private NativeWebRequest nativeWebRequest;

    @Test
    void shouldGetDate() {
        //Given
        given(nativeWebRequest.getHeader(any())).willReturn(HOMER_DATE_OF_BIRTH_STRING);
        //When
        LocalDate dateOfBirth = ConverterUtils.nullSafeGetDate(nativeWebRequest, "dateOfBirth");
        //Then
        assertThat(dateOfBirth).isEqualTo(HOMER_DATE_OF_BIRTH);
        verify(nativeWebRequest).getHeader("dateOfBirth");
    }

    @Test
    void shouldReturnNullWhenDateHeaderNotPresent() {
        //Given
        given(nativeWebRequest.getHeader(any())).willReturn(null);
        //When
        LocalDate dateOfBirth = ConverterUtils.nullSafeGetDate(nativeWebRequest, "dateOfBirth");
        //Then
        assertThat(dateOfBirth).isNull();
        verify(nativeWebRequest).getHeader("dateOfBirth");
    }

    @Test
    void shouldGetInteger() {
        //Given
        given(nativeWebRequest.getHeader(any())).willReturn("40800");
        //When
        Integer threshold = ConverterUtils.nullSafeGetInteger(nativeWebRequest, "ucMonthlyIncomeThreshold");
        //Then
        assertThat(threshold).isEqualTo(UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE);
        verify(nativeWebRequest).getHeader("ucMonthlyIncomeThreshold");
    }

    @Test
    void shouldReturnNullWhenIntegerHeaderNotPresent() {
        //Given
        given(nativeWebRequest.getHeader(any())).willReturn(null);
        //When
        Integer threshold = ConverterUtils.nullSafeGetInteger(nativeWebRequest, "ucMonthlyIncomeThreshold");
        //Then
        assertThat(threshold).isNull();
        verify(nativeWebRequest).getHeader("ucMonthlyIncomeThreshold");
    }
}
