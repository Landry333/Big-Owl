package com.example.bigowlapp.utils;

import com.example.bigowlapp.repository.exception.EmptyFieldException;
import com.google.i18n.phonenumbers.NumberParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhoneNumberFormatterTest {

    @Mock
    private PhoneNumberFormatter.CountryCodeGetter mockCountryCodeGetter;

    PhoneNumberFormatter phoneNumberFormatter;

    @Before
    public void setUp() {
        when(mockCountryCodeGetter.getCountryCode()).thenReturn("CA");
        phoneNumberFormatter = new PhoneNumberFormatter(null);
        phoneNumberFormatter.setCountryCodeGetter(mockCountryCodeGetter);
    }

    @Test(expected = EmptyFieldException.class)
    public void formatNumberNullShouldThrowError() throws NumberParseException, EmptyFieldException {
        phoneNumberFormatter.formatNumber(null);
    }

    @Test(expected = EmptyFieldException.class)
    public void formatNumberEmptyShouldThrowError() throws NumberParseException, EmptyFieldException {
        phoneNumberFormatter.formatNumber("");
    }

    @Test(expected = NumberParseException.class)
    public void formatNumberNotNumberShouldThrowError() throws NumberParseException, EmptyFieldException {
        phoneNumberFormatter.formatNumber("letters");
    }

    @Test(expected = NumberParseException.class)
    public void formatNumberTooLongShouldThrowError() throws NumberParseException, EmptyFieldException {
        phoneNumberFormatter.formatNumber("99999999999999999999999999999999");
    }

    @Test(expected = NumberParseException.class)
    public void formatNumberTooShortShouldThrowError() throws NumberParseException, EmptyFieldException {
        phoneNumberFormatter.formatNumber("8");
    }

    @Test
    public void formatNumberValidCases() throws NumberParseException, EmptyFieldException {
        String expectedResult = "+15553334444";
        String result;

        result = phoneNumberFormatter.formatNumber(expectedResult);
        assertEquals(expectedResult, result);

        result = phoneNumberFormatter.formatNumber("5553334444");
        assertEquals(expectedResult, result);

        result = phoneNumberFormatter.formatNumber("555-333-4444");
        assertEquals(expectedResult, result);

        result = phoneNumberFormatter.formatNumber("555 333 4444");
        assertEquals(expectedResult, result);

        result = phoneNumberFormatter.formatNumber("15553334444");
        assertEquals(expectedResult, result);

        result = phoneNumberFormatter.formatNumber("+1 555-333-4444");
        assertEquals(expectedResult, result);
    }
}