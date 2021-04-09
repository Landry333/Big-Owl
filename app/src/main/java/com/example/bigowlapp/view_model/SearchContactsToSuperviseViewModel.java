package com.example.bigowlapp.view_model;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.bigowlapp.activity.SearchContactsToSupervise;
import com.example.bigowlapp.model.LiveDataWithStatus;
import com.example.bigowlapp.model.User;
import com.example.bigowlapp.utils.PhoneNumberFormatter;

import java.util.ArrayList;

public class SearchContactsToSuperviseViewModel extends BaseViewModel {

    private LiveDataWithStatus<User> userToAddData;

    public LiveData<User> getUserToAdd(String number) {
        userToAddData = repositoryFacade.getUserRepository()
                .getDocumentByAttribute(User.Field.PHONE_NUMBER, number, User.class);
        return userToAddData;
    }

    @NonNull
    public String getNumberFromContactDataList(String contactDetails) {
        String contactNumber;
        if (contactDetails.split("\\n").length < 2) {
            contactNumber = contactDetails
                    .split("\\n")[0]
                    .replaceAll("[^+0-9]", "");
        } else {
            contactNumber = contactDetails
                    .split("\\n")[1]
                    .replaceAll("[^+0-9]", "");
        }
        return contactNumber;
    }

    public ArrayList<String> populateContactsList(PhoneNumberFormatter formatter, Cursor phoneResultsCursor) {
        ArrayList<String> contactsDataList = new ArrayList<>();

        while (phoneResultsCursor.moveToNext()) {
            String name = phoneResultsCursor.getString(SearchContactsToSupervise.INDEX_CONTACT_NAME);
            String number = phoneResultsCursor.getString(SearchContactsToSupervise.INDEX_CONTACT_NUMBER);

            try {
                String formattedNumber = formatter.formatNumber(number);
                contactsDataList.add(name + "\n" + formattedNumber);
            } catch (Exception ignored) {
                // Invalid numbers are skipped
            }
        }

        return contactsDataList;
    }
}
