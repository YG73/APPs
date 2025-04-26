package com.example.apps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaxConverseActivity extends AppCompatActivity implements View.OnClickListener,Runnable{
    private EditText inputRmb;
    private TextView text;
    private Button dollar_btn,won_btn,euro_btn;
    private static final String TAG = "tag";

    private volatile  double DOLLAR_RATE = 0.14,WON_RATE=200.91,EURO_RATE=0.13;

    Handler handler;

    private Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tax_converse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inputRmb = findViewById(R.id.input_text);
        text = findViewById(R.id.show_test);
        won_btn = findViewById(R.id.won_btn);
        euro_btn = findViewById(R.id.euro_btn);
        dollar_btn = findViewById(R.id.dollar_btn);

        //下面这段是优化前获取传入数据的方法
//        try {
//            intent = getIntent();
//            DOLLAR_RATE = intent.getDoubleExtra("DOLLAR_RATE",0.14);
//            WON_RATE = intent.getDoubleExtra("WON_RATE",200.91);
//            EURO_RATE = intent.getDoubleExtra("EURO_RATE",0.13);
//        } catch (Exception e) {
//            Log.e(TAG, "getIntent: ",e);
//        }

        //连接网络，实时获取汇率
        Log.i(TAG, "onCreate: 线程启动");
        Thread thread = new Thread(this);
        thread.start();
        //使用匿名函数
//        Thread th2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "run: ");
//            }
//        });
        //简化
//        Thread th3 = new Thread(()->{
//
//        });

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==7){
                    Bundle bnd = msg.getData();
                    EURO_RATE = bnd.getDouble("EURO",0.13);
                    WON_RATE = bnd.getDouble("WON",200.91);
                    DOLLAR_RATE = bnd.getDouble("DOLLAR",0.14);
                    //测试效果
                    //text.setText(str);
                    Log.i(TAG, "onCreate: 美元：" + DOLLAR_RATE);
                    Log.i(TAG, "onCreate: 欧元：" + EURO_RATE);
                    Log.i(TAG, "onCreate: 韩元：" + WON_RATE);
                    Log.i(TAG, "handleMessage: "+bnd);

                    // 更新完成后，提示用户或清空输入框，要求重新计算
                    Toast.makeText(TaxConverseActivity.this, "汇率已更新，请重新输入金额", Toast.LENGTH_SHORT).show();
                    inputRmb.setText(""); // 清空输入框，确保使用新汇率
                }
                super.handleMessage(msg);

            }
        };
    }

    @Override
    public void onClick(View v) {
        //获取输入
        String s = null;
        try {
            s = inputRmb.getText().toString();
        } catch (Exception e) {
            text.setText("输入不合法,请重新输入");
            //消息提示
            Toast.makeText(this, "输入不合法,请重新输入", Toast.LENGTH_SHORT).show();//弹出警告
        }
        double yuan = Double.parseDouble(s);
        String res;
        //计算结果
        if(v.getId()==R.id.dollar_btn){
            res = (yuan*DOLLAR_RATE) + "$";
        } else if (v.getId()==R.id.euro_btn) {
            res = (yuan*EURO_RATE) + "€";
        }else{
            res= (yuan*WON_RATE) +"₩";
        }
        //显示结果
        text.setText(String.valueOf(res));
    }

//    ActivityResultLauncher luncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            result ->{
//                if(requestCode==6 &&resultCode==3) {
//                    try {
//                        Bundle bn = data.getExtras();
//                        DOLLAR_RATE = bn.getDouble("DOLLAR_RATE", 0.14);
//                        WON_RATE = bn.getDouble("WON_RATE", 200.91);
//                        EURO_RATE = bn.getDouble("EURO_RATE", 0.13);
//                    } catch (Exception e) {
//                        Log.i(TAG, "onActivityResult: 获取数据报错: "+e);
//                    }
//
//                    Log.i(TAG, "onActivityResult: DOLLAR_RATE= " + DOLLAR_RATE);
//                    Log.i(TAG, "onActivityResult: EURO_RATE= " + EURO_RATE);
//                    Log.i(TAG, "onActivityResult: WON_RATE= " + WON_RATE);
//
//                }
//            }
//    )
    public void myConverseClick(View btn){
        //跳转到新页面
        Intent intent = new Intent(this,ConfigSetRatActivity.class);
        intent.putExtra("DOLLAR_RATE",DOLLAR_RATE);
        intent.putExtra("EURO_RATE",EURO_RATE);
        intent.putExtra("WON_RATE",WON_RATE);
        Log.i(TAG, "myConverseClick: DOLLAR_RATE= "+DOLLAR_RATE);
        Log.i(TAG, "myConverseClick: EURO_RATE= "+EURO_RATE);
        Log.i(TAG, "myConverseClick: WON_RATE= "+WON_RATE);

        startActivityForResult(intent,6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //从data中获取数据
        if(requestCode==6 &&resultCode==3) {
            try {
                Bundle bn = data.getExtras();
                DOLLAR_RATE = bn.getDouble("DOLLAR_RATE", 0.14);
                WON_RATE = bn.getDouble("WON_RATE", 200.91);
                EURO_RATE = bn.getDouble("EURO_RATE", 0.13);
            } catch (Exception e) {
                Log.i(TAG, "onActivityResult: 获取数据报错: "+e);
            }

            Log.i(TAG, "onActivityResult: DOLLAR_RATE= " + DOLLAR_RATE);
            Log.i(TAG, "onActivityResult: EURO_RATE= " + EURO_RATE);
            Log.i(TAG, "onActivityResult: WON_RATE= " + WON_RATE);

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void run() {
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
            trs.remove(0); // 移除表头行

            // 验证数据行存在
            if (trs.isEmpty()) {
                Log.e(TAG, "run: 数据行为空");
                return;
            }

            // 获取第一行数据（最新日期）
            Element firstDataRow = trs.get(0);
            Elements tds = firstDataRow.getElementsByTag("td");

            //OR
//            Element se_won = doc.select("#InfoTable > tbody > tr:nth-child(2) > td:nth-child(16)").first();
//            Element se_f_won = doc.selectFirst("#InfoTable > tbody > tr:nth-child(2) > td:nth-child(16)");
            // 提取美元、欧元、日元汇率（根据页面源码，列索引从0开始）
            String date = tds.get(0).text();   // 日期（第1列，索引0）
            String usdRate = tds.get(1).text(); // 美元（第2列，索引1）
            String eurRate = tds.get(2).text(); // 欧元（第3列，索引2）
            String wonRate = tds.get(15).text(); // 韩元（第15列，索引15）

            Log.i(TAG, "run: 最新汇率数据：");
            Log.i(TAG, "run: 日期：" + date);
            Log.i(TAG, "run: 美元：" + usdRate);
            Log.i(TAG, "run: 欧元：" + eurRate);
            Log.i(TAG, "run: 韩元：" + wonRate);

            //修改数据
            DOLLAR_RATE = 100/Double.parseDouble(usdRate);
            WON_RATE=100/Double.parseDouble(wonRate);
            EURO_RATE=100/Double.parseDouble(eurRate);

            bnd.putDouble("DOLLAR",100/Double.parseDouble(usdRate));
            bnd.putDouble("WON",100/Double.parseDouble(wonRate));
            bnd.putDouble("EURO",100/Double.parseDouble(eurRate));
            Log.i(TAG, "run: "+bnd);



            //操作参考
//            Elements tables = doc.getElementsByTag("table");
//            Element table = tables.get(1);
//            Log.i(TAG, "run: table"+table);
//
//            Elements trs = table.getElementsByTag("tr");
//            trs.remove(0);
//            String dollar_tr = trs.get(0).text();
//            String euro_tr = trs.get(1).text();
//            String won_tr = trs.get(14).text();
//            Log.i(TAG, "run: "+dollar_tr+"\n"+euro_tr+"\n"+won_tr);



//            for(Element tr : trs){
//                Elements tds = tr.children();
//                Element td1 = tds.first();
//                Element td2 = tds.get(5);
//                String str1 = td1.text();
//                String str2 = td2.text();
//                Log.i(TAG, "run: "+str1+"==>"+str2);
//            }


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