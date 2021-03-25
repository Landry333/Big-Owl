package com.example.bigowlapp.utils;

import android.content.Context;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberFormatter {

    private PhoneNumberFormatter() {
        // This is a util class, it has no state
    }

    public static String formatNumber(String number, Context context) throws NumberParseException {
        PhoneNumberUtil numbUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = numbUtil.parseAndKeepRawInput(number, context.getResources().getConfiguration().getLocales().get(0).getCountry());
        String formattedNumber = numbUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        return formattedNumber;
    }

}
