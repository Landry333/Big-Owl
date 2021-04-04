package com.example.bigowlapp.utils;

import android.content.Context;

import com.example.bigowlapp.repository.exception.EmptyFieldException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberFormatter {

    private PhoneNumberFormatter() {
        // This is a util class, it has no state
    }

    public static String formatNumber(String number, Context context) throws NumberParseException, EmptyFieldException {
        if (number == null || number.isEmpty()) {
            throw new EmptyFieldException("Please enter a valid phone number.");
        }

        PhoneNumberUtil numbUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phonenumber = numbUtil.parseAndKeepRawInput(number, context.getResources().getConfiguration().getLocales().get(0).getCountry());
        return numbUtil.format(phonenumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

}
