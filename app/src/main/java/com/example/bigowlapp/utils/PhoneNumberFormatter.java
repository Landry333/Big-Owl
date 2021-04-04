package com.example.bigowlapp.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.example.bigowlapp.repository.exception.EmptyFieldException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberFormatter {
    CountryCodeGetter countryCodeGetter;

    public PhoneNumberFormatter(Context context) {
        countryCodeGetter = new CountryCodeGetter(context);
    }

    public String formatNumber(String number) throws NumberParseException, EmptyFieldException {
        if (number == null || number.isEmpty()) {
            throw new EmptyFieldException("Please enter a valid phone number.");
        }

        PhoneNumberUtil numbUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phonenumber = numbUtil.parseAndKeepRawInput(number, countryCodeGetter.getCountryCode());
        return numbUtil.format(phonenumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setCountryCodeGetter(CountryCodeGetter countryCodeGetter) {
        this.countryCodeGetter = countryCodeGetter;
    }

    public static class CountryCodeGetter {

        private final Context context;

        public CountryCodeGetter(Context context) {
            this.context = context;
        }

        @NonNull
        public String getCountryCode() {
            return context.getResources().getConfiguration().getLocales().get(0).getCountry();
        }
    }
}
