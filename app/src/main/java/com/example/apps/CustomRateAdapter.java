// CustomRateAdapter.java
package com.example.apps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomRateAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;

    public CustomRateAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(mResource, parent, false);
        }

        String item = getItem(position);
        if (item != null) {
            String[] parts = item.split("汇率：");
            if (parts.length == 2) {
                String country = parts[0];
                String rate = parts[1];

                TextView countryText = view.findViewById(R.id.country_text);
                TextView rateText = view.findViewById(R.id.rate_text);

                countryText.setText(country);
                rateText.setText("汇率：" + rate);
            }
        }

        return view;
    }
}