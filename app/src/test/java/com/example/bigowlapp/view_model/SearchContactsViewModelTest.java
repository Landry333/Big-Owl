package com.example.bigowlapp.view_model;

import android.database.Cursor;

import com.example.bigowlapp.activity.SearchContactsToSupervise;
import com.example.bigowlapp.utils.PhoneNumberFormatter;
import com.google.i18n.phonenumbers.NumberParseException;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchContactsViewModelTest {
    @Mock
    private Cursor mockContactsCursor;
    private final int numContacts = 5;
    private int cursorindex;
    private List<String[]> contactsData;

    @Mock
    private PhoneNumberFormatter mockPhoneNumberFormatter;

    private SearchContactsViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        cursorindex = -1;
        contactsData = this.getFakeContactsData();
        when(mockContactsCursor.moveToNext()).thenAnswer(answer -> {
            cursorindex += 1;
            return cursorindex < numContacts;
        });
        when(mockContactsCursor.getString(SearchContactsToSupervise.INDEX_CONTACT_NAME))
                .thenAnswer(answer -> contactsData.get(cursorindex)[SearchContactsToSupervise.INDEX_CONTACT_NAME]);
        when(mockContactsCursor.getString(SearchContactsToSupervise.INDEX_CONTACT_NUMBER))
                .thenAnswer(answer -> contactsData.get(cursorindex)[SearchContactsToSupervise.INDEX_CONTACT_NUMBER]);

        when(mockPhoneNumberFormatter.formatNumber(any())).then(returnsFirstArg());

        viewModel = new SearchContactsViewModel();
    }

    @Test
    public void populateContactsList() throws NumberParseException {
        List<String> result = viewModel.populateContactsList(mockPhoneNumberFormatter, mockContactsCursor);
        assertEquals(contactsData.size(), result.size());
        assertEquals("Joe Doe0\n+14381234560", result.get(0));
        assertEquals("Joe Doe4\n+14381234564", result.get(4));
        MatcherAssert.assertThat(result, hasItem("Joe Doe2\n+14381234562"));

        // try case with a bad number
        String badNumber = "not a number";
        contactsData.get(2)[SearchContactsToSupervise.INDEX_CONTACT_NUMBER] = badNumber;
        when(mockPhoneNumberFormatter.formatNumber(badNumber))
                .thenThrow(new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, ""));

        cursorindex = -1;
        result = viewModel.populateContactsList(mockPhoneNumberFormatter, mockContactsCursor);
        assertEquals(contactsData.size() - 1, result.size());
        assertEquals("Joe Doe0\n+14381234560", result.get(0));
        assertEquals("Joe Doe4\n+14381234564", result.get(3));
        MatcherAssert.assertThat(result, not(hasItem("Joe Doe2\n+14381234562")));
    }

    @Test
    public void getNumberFromContactDataList() {
        String normalDetails = "Joe Doe0\n+14381234560";
        assertEquals("+14381234560", viewModel.getNumberFromContactDataList(normalDetails));

        String noNameDetails = "+14381234560";
        assertEquals("+14381234560", viewModel.getNumberFromContactDataList(noNameDetails));
    }

    private List<String[]> getFakeContactsData() {
        List<String[]> data = new ArrayList<>();
        for (int i = 0; i < numContacts; i++) {
            data.add(new String[]{"Joe Doe" + i, "+1438123456" + i});
        }
        return data;
    }
}