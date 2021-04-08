package com.example.bigowlapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.example.bigowlapp.repository.exception.EmptyFieldException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberFormatter {
    private Context context;
    private CountryCodeGetter countryCodeGetter;

    public PhoneNumberFormatter(Context context) {
        this.context = context;
        countryCodeGetter = new CountryCodeGetter(context);
    }

    public String formatNumber(String number) throws NumberParseException {
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

    @SuppressLint("MissingPermission")
    public String getFormattedSMSNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String devicePhoneNumber = telephonyManager.getLine1Number();
        String formattedDevicePhoneNum = null;
        try {
            formattedDevicePhoneNum = this.formatNumber(devicePhoneNumber);
        } catch (NumberParseException e) {
            Toast.makeText(context, "FAILED to format phone number. Process failed", Toast.LENGTH_LONG).show();
        }
        return formattedDevicePhoneNum;
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
