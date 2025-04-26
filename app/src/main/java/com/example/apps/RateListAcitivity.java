package com.example.apps;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


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

public class RateListAcitivity extends ListActivity {
    private Handler handler=null;

    private static final String TAG = "RateListAcitivity";
    private ListView listView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String>  list = new ArrayList<>();
        //listView = findViewById(R.id.myList);


        for (int i = 0; i < 10; i++) {
            list.add("item"+i);
        }
        String[] list_data = {"oo","aa","pp"};
        ListAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list_data);
        listView.setAdapter(adapter);


        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(
                            RateListAcitivity.this,
                            android.R.layout.simple_list_item_1,
                            list2);

                    listView.setAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=null;

                Log.i(TAG, "run: ");
                List<String>  list = new ArrayList<>();

                Log.i(TAG, "run: 线程运行");
                Bundle bnd = new Bundle();//打包回数据
                //获取网络数据
                URL url =null;
                try {
                    //测试连接
                    url = new URL("https://www.safe.gov.cn/AppStructured/hlw/RMBQuery.do");
                    HttpURLConnection http =(HttpURLConnection) url.openConnection();
                    InputStream input = http.getInputStream();

                    String html = inputStream2String(input);
                    Log.i(TAG, "run: html="+html);

                    //爬取数据

                    Document doc = Jsoup.connect("https://www.safe.gov.cn/AppStructured/hlw/RMBQuery.do").get();
                    Log.i(TAG, "run: title"+doc.title());
                    // 精准定位目标表格（类名为 'list'）
                    Element table = doc.getElementsByClass("list").first();
                    if (table == null) {
                        Log.e(TAG, "run: 未找到汇率表格");
                        return;
                    }

                    // 获取表格行（排除表头）
                    Elements trs = table.getElementsByTag("tr");
                    if (trs.size() < 1) {
                        Log.e(TAG, "run: 未找到表格行");
                        return;
                    }
                    //trs.remove(0); //国家

                    // 验证数据行存在
                    if (trs.isEmpty()) {
                        Log.e(TAG, "run: 数据行为空");
                        return;
                    }

                    // 获取第0行数据（国家）
                    Element zeroDataRow = trs.get(0);
                    Elements td_contries = zeroDataRow.getElementsByTag("td");

                    // 获取第一行数据（最新日期）
                    Element firstDataRow = trs.get(1);
                    Elements tds = firstDataRow.getElementsByTag("td");

                    String date = tds.get(0).text();
                    Log.i(TAG, "run: 最新汇率数据：");
                    Log.i(TAG, "run: 日期：" + date);

                    for (int i = 1; i < tds.size(); i++) {
                        String contry = td_contries.get(i).text();
                        String data = tds.get(i).text();
                        Log.i(TAG, "run: "+contry+"==>"+data);
                        bnd.putDouble(contry,100/Double.parseDouble(data));

                    }
                    Log.i(TAG, "run: bnd"+bnd);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "run: ",e );
                }

                //向主线程发送消息
                Message msg = handler.obtainMessage(7,bnd);
                msg.setData(bnd); // 必须调用 setData(),不然后边的bnd为null
                handler.sendMessage(msg);
                Log.i(TAG, "run: 消息发送,将Bundle传回主线程");


            }
        });

    }

    private String inputStream2String(InputStream inputStream)
            throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}