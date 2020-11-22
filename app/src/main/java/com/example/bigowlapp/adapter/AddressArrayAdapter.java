package com.example.bigowlapp.adapter;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bigowlapp.R;

import java.util.List;

public class AddressArrayAdapter extends ArrayAdapter<Address> {

    private Context context;
    private List<Address> addressList;

    public AddressArrayAdapter(@NonNull Context context, int resource, @NonNull List<Address> addressList) {
        super(context, resource, addressList);

        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null)
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_address, parent,false);

        Address currentAddress = addressList.get(position);

        TextView addressLineTextView = itemView.findViewById(R.id.address_line);
        TextView addressLocationTextView = itemView.findViewById(R.id.address_location);

        addressLineTextView.setText(currentAddress.getAddressLine(0));
        addressLocationTextView.setText(currentAddress.getLocality() + ", " + currentAddress.getCountryCode());

        return itemView;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = addressList;
                results.count = getCount();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}
