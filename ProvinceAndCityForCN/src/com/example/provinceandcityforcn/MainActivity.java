package com.example.provinceandcityforcn;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.res.XmlResourceParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 本例实现Pull解析XML文件
 * @author zzp
 *
 */
public class MainActivity extends Activity {

    private Button getProvince4AssetsBtn;
    private Button getProvince4XmlBtn;
    private Button getProvince4UrlBtn;
    private Button getCityBtn;
    private Button clearBtn;
    private TextView showView;

    private String fileName = "provinceandcity.xml";
    private String provinceandcityStr = null;
    private XmlPullParser provinceandcityParser;
    public ArrayList<Province> provinceArray;
    public ArrayList<City> cityArray;
    public String provinceStr = "";
    public String cityStr = "";
    public String provinceAndCityUrl = "http://www.csw333.com/CityScene_I/getPlace.php";
    public boolean isFinishParser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getProvince4AssetsBtn = (Button)findViewById(R.id.getProvinceBtn1);
        getProvince4XmlBtn = (Button)findViewById(R.id.getProvinceBtn2);
        getProvince4UrlBtn = (Button)findViewById(R.id.getProvinceBtn3);

        getCityBtn = (Button)findViewById(R.id.getCityBtn);
        clearBtn = (Button)findViewById(R.id.clearBtn);
        showView = (TextView)findViewById(R.id.view);

        getProvince4AssetsBtn.setOnClickListener(mOnClickListener);
        getProvince4XmlBtn.setOnClickListener(mOnClickListener);
        getProvince4UrlBtn.setOnClickListener(mOnClickListener);
        getCityBtn.setOnClickListener(mOnClickListener);
        clearBtn.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.getProvinceBtn1:
                    /*/String 本地文件不推荐
                     provinceandcityStr = getStringFromAssets(fileName);
                    /*/
                    //   xml文件来源方式一：  assets   InputStream             
                    provinceArray = ProvincePullParse.Parse(getInputStreamFromAssets(fileName));                    
                    //*/

                    for(Province pro : provinceArray){
                        provinceStr += pro.getProvinceId() + " : " +pro.getProvinceName()+"\n";
                    }

                    showView.setText(provinceStr);
                    break;

                case R.id.getProvinceBtn2:
                    //  xml文件来源方式二： res/xml   XmlResourceParser
                    provinceandcityParser = getXMLFromResXml(fileName);                    
                    provinceArray = ProvincePullParse.ParseXml(provinceandcityParser);
                    for(Province pro : provinceArray){
                        provinceStr += pro.getProvinceId() + " : " +pro.getProvinceName()+"\n";
                    }

                    showView.setText(provinceStr);
                    break;

                case R.id.getProvinceBtn3:
                    //  xml文件来源方式三： url   采用多线程更新
                    isFinishParser = false;
                    parserWhitThread();
                    mHandler.postDelayed(mRunnable, 200);

                    break;

                case R.id.getCityBtn:
                    // 解析城市采用方式二
                    provinceandcityParser = getXMLFromResXml(fileName);

                    cityArray = CityPullParse.ParseXml(provinceandcityParser);
                    for(City city : cityArray){
                        cityStr += "省份ID["+city.getProvinceId() + "],城市ID[" + city.getCityId() +"], "+city.getCityName()+"\n";
                    }
                    showView.setText(cityStr);
                    break;

                case R.id.clearBtn:
                    showView.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 直接获取Assets文件夹中的XML文件转成String，在parse时会失败，（异常信息：PI must not start with xml）
     * 后来排查发现去掉首行<?xml version="1.0" encoding="utf-8"?> ，解析成功，不推荐该方式，多一些不必要的string转换
     * 直接采用 getInputStreamFromAssets
     * 
     * @param fileName
     * @return : 读取到Assets文件夹下的xml文件字符串
     */
    public String getStringFromAssets(String fileName){ 
        String line="";
        String Result="";       

        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName)); 
            BufferedReader bufReader = new BufferedReader(inputReader);

            while((line = bufReader.readLine()) != null)
                Result += line;

            return Result;
        } catch (Exception e) { 
            e.printStackTrace();
        }
        return Result;
    }

    /**
     * 同样删除首行，才能解析成功，
     * @param fileName
     * @return 返回xml文件的inputStream
     */     
    public InputStream getInputStreamFromAssets(String fileName){
        try {
            InputStream inputStream = getResources().getAssets().open(fileName);
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取XML文件，xml文件放到res/xml文件夹中，若XML为本地文件，则推荐该方法
     * 
     * @param fileName
     * @return : 读取到res/xml文件夹下的xml文件，返回XmlResourceParser对象（XmlPullParser的子类）
     */
    public XmlResourceParser getXMLFromResXml(String fileName){
        XmlResourceParser xmlParser = null;
        try {
            //*/
            //  xmlParser = this.getResources().getAssets().openXmlResourceParser("assets/"+fileName);        // 失败,找不到文件
            xmlParser = this.getResources().getXml(R.xml.provinceandcity);
            /*/
            // xml文件在res目录下 也可以用此方法返回inputStream
            InputStream inputStream = this.getResources().openRawResource(R.xml.provinceandcity);
            /*/
            return xmlParser;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return xmlParser;
    }

    /**
     * 读取url的xml资源 转成String
     * @param url
     * @return 返回 读取url的xml字符串
     */
    public String getStringByUrl(String url) {
        String outputString = "";
        // DefaultHttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();
        // HttpGet
        HttpGet httpget = new HttpGet(url);
        // ResponseHandler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        try {
            outputString = httpclient.execute(httpget, responseHandler);
            outputString = new String(outputString.getBytes("ISO-8859-1"), "utf-8");    // 解决中文乱码

            Log.i("HttpClientConnector", "连接成功");
        } catch (Exception e) {
            Log.i("HttpClientConnector", "连接失败");
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return outputString;
    }

    /**
     * 解析SDcard xml文件
     * @param fileName
     * @return 返回xml文件的inputStream
     */     
    public InputStream getInputStreamFromSDcard(String fileName){
        try {
            // 路径根据实际项目修改
            String path = Environment.getExternalStorageDirectory().toString() + "/test_xml/";

            Log.v("", "path : " + path);

            File xmlFlie = new File(path+fileName);

            InputStream inputStream = new FileInputStream(xmlFlie);

            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  多线程加载网络端的xml，若xml文件过大也需要用该方式加载
     */
    Handler mHandler = new Handler();   
    Runnable mRunnable = new Runnable() {
        public void run() {
            if(!isFinishParser){

                mHandler.postDelayed(mRunnable, 1000);    
            }else{
                showView.setText(provinceStr);
                mHandler.removeCallbacks(mRunnable);
            }
        }
    };

    /**
     * 比较耗时操作新建一个线程，避免UI线程ANR
     */
    public void parserWhitThread(){
        new Thread(){
            @Override
            public void run() {                
                provinceandcityStr = getStringByUrl(provinceAndCityUrl);
                provinceArray = ProvincePullParse.Parse(provinceandcityStr);
                for(Province pro : provinceArray){
                    provinceStr += pro.getProvinceId() + " : " +pro.getProvinceName()+"\n";
                }
                isFinishParser = true;
            }
        }.start();
    }
}
