package com.example.apps;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CustomListActivity extends AppCompatActivity {
    private Handler handler;
    private static final String TAG = "CustomListActivity";
    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 绑定展示页面
        setContentView(R.layout.activity_custom_list);
        // 绑定List
        listView = findViewById(R.id.list);
        // 若爬取失败，就显示test
        String[] data = {"t", "e", "s", "t"};
        List<String> testList = new ArrayList<>();
        for (String item : data) {
            testList.add(item);
        }
        CustomAdapter testAdapter = new CustomAdapter(this, R.layout.list_item, testList);
        listView.setAdapter(testAdapter);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    Bundle bnd = msg.getData();
                    Log.i(TAG, "handleMessage: get bnd" + bnd);
                    if (bnd != null) {
                        List<String> list2 = new ArrayList<>();
                        // 遍历 Bundle 中的所有国家和汇率
                        for (String country : bnd.keySet()) {
                            double rate = bnd.getDouble(country);
                            list2.add(country + "汇率：" + rate);
                        }
                        CustomAdapter adapter = new CustomAdapter(
                                CustomListActivity.this,
                                R.layout.list_item,
                                list2
                        );
                        listView.setAdapter(adapter);
                    }
                }
                super.handleMessage(msg);
            }
        };

        // 启动子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                Bundle bnd = new Bundle();

                try {
                    URL url = new URL("https://www.huilvbiao.com/bank/spdb");
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    InputStream input = http.getInputStream();
                    String html = inputStream2String(input);
                    Document doc = Jsoup.connect(url.toString()).get();

                    Elements table = doc.select("body > main > div:nth-child(1) > div > div.table-responsive > table");
                    if (table.isEmpty()) {
                        Log.e(TAG, "未找到汇率表格");
                        return;
                    }

                    Elements trs = table.select("tbody tr");
                    if (trs.size() < 2) { // 至少需要表头和数据行
                        Log.e(TAG, "表格行数据不足");
                        return;
                    }
                    for (int i = 0; i < trs.size(); i++) {
                        Element row = trs.get(i);
                        // 提取货币名称
                        Element name_e = row.selectFirst("th > a > span");
                        // 提取汇率
                        Element price_e = row.selectFirst("td:nth-child(2)");
                        if (name_e != null && price_e != null) {
                            String country = name_e.text();
                            try {
                                double price = Double.parseDouble(price_e.text());
                                double rate = 100 / price;
                                bnd.putDouble(country, rate);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "价格转换为数字时出错: " + e.getMessage());
                            }
                        } else {
                            Log.e(TAG, "未找到国家或价格元素，索引: " + i);
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, "run:" + e);
                    return;
                }
                msg = handler.obtainMessage(7, bnd);
                msg.setData(bnd); // 存入 Bundle
                handler.sendMessage(msg);
                Log.i(TAG, "消息发送，Bundle 内容：" + bnd);
            }
        }).start();
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        // 从ppt copy
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0) break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}