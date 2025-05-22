package com.example.apps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 跑一下，自定义适配器*/
//WHY?非要自定义，为了能回调数据
public class CustomAdapter extends ArrayAdapter {
    private static final String Tag = "CustomAdapter";
    //为了能回调数据!
    private Context mContext;
    private int mResource;
    private List<String> mData;
    public CustomAdapter(Context context, int resource, List<String> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mData = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(mResource, parent, false);
        }

        // 获取列表项数据
        String item = mData.get(position);
        String[] parts = item.split("汇率：");
        String country = parts[0];
        String rate = parts[1];

        // 绑定视图组件
        TextView countryText = view.findViewById(R.id.country_text);
        TextView rateText = view.findViewById(R.id.rate_text);

        // 设置文本内容
        countryText.setText(country);
        rateText.setText("汇率：" + rate);

        return view;
    }
}
