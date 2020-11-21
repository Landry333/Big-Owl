package com.example.bigowlapp.adapter;

import android.content.Context;
import android.location.Address;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.List;

public class AddressAdapter extends ArrayAdapter<Address> {

    private Context context;
    private List<Address> addressList;

    public AddressAdapter(@NonNull Context context, int resource, @NonNull List<Address> addressList) {
        super(context, resource, addressList);

        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = addressList;
                results.count = addressList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}
