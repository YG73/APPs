package com.example.apps;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class RateListRealActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private RateDatabaseHelper dbHelper;
    private Handler handler;
    private static final String TAG = "RateListReallAcitivity";
    private ListView listView;
    private CustomRateAdapter adapter;
    private List<String> rateList = new ArrayList<>(); // 添加数据列表成员变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list_reall);
        
        dbHelper = new RateDatabaseHelper(this);
        listView = findViewById(R.id.rate_List);

        // 若爬取失败，就显示test
        String[] data = {"t", "e", "s", "t"};
        List<String> testList = new ArrayList<>();
        for (String item : data) {
            testList.add(item);
        }
        CustomRateAdapter testAdapter = new CustomRateAdapter(this, R.layout.custom_list_item, testList);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(testAdapter);
        listView.setEmptyView(findViewById(R.id.noData));
        ProgressBar progressBar = findViewById(R.id.prgBar);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setVisibility(GONE);
                if (msg.what == 7) {
                    Bundle bnd = msg.getData();
                    Log.i(TAG, "handleMessage: get bnd" + bnd);
                    if (bnd != null) {
                        // 清空数据库旧数据
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete(RateDatabaseHelper.TABLE_RATES, null, null);
                        
                        // 存入新数据
                        ContentValues values = new ContentValues();
                        for (String country : bnd.keySet()) {
                            values.put(RateDatabaseHelper.COLUMN_COUNTRY, country);
                            values.put(RateDatabaseHelper.COLUMN_RATE, bnd.getDouble(country));
                            db.insert(RateDatabaseHelper.TABLE_RATES, null, values);
                            values.clear();
                        }
                        db.close();
                        
                        // 从数据库读取数据
                        loadRatesFromDatabase();
                    }
                }
                super.handleMessage(msg);
            }
        };

        // 设置列表项点击监听
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // 获取当前点击项的数据
            String itemData = (String) listView.getItemAtPosition(position);

            // 解析国家和汇率（"汇率：XX.XX  国家"）
            String[] parts = itemData.split("汇率：");
            if (parts.length == 2) {
                String country = parts[0].trim();
                try {
                    double rate = Double.parseDouble(parts[1].trim());

                    // 跳转到计算页面
                    Intent intent = new Intent(RateListRealActivity.this, RateCalculateActivity.class);
                    intent.putExtra("country", country);  // 传递国家名称
                    intent.putExtra("rate", rate);        // 传递汇率
                    startActivity(intent);
                } catch (NumberFormatException e) {
                    Toast.makeText(RateListRealActivity.this, "无效的汇率数据", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RateListRealActivity.this, "数据格式错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 启动子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(VISIBLE);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 保存点击位置到最终变量，以便在内部类中访问
        final int clickedPosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("请确认是否删除当前数据")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: 删除位置=" + clickedPosition);
                        // 删除数据项（确保在UI线程执行）
                        if (clickedPosition >= 0 && clickedPosition < rateList.size()) {
                            rateList.remove(clickedPosition); // 使用 rateList 进行删除
                            // 更新适配器
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "无效的删除位置: " + clickedPosition);
                        }
                    }
                })
                .setNegativeButton("否", null);
        builder.create().show();
        return true;
    }

    private void loadRatesFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                RateDatabaseHelper.TABLE_RATES,
                new String[]{RateDatabaseHelper.COLUMN_COUNTRY, RateDatabaseHelper.COLUMN_RATE},
                null, null, null, null, null);
        
        rateList.clear();
        while (cursor.moveToNext()) {
            String country = cursor.getString(0);
            double rate = cursor.getDouble(1);
            rateList.add(country + "汇率：" + rate);
        }
        cursor.close();
        db.close();
        
        adapter = new CustomRateAdapter(
                RateListRealActivity.this,
                R.layout.custom_list_item,
                rateList
        );
        listView.setAdapter(adapter);
    }
}