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
 * ����ʵ��Pull����XML�ļ�
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
                    /*/String �����ļ����Ƽ�
                     provinceandcityStr = getStringFromAssets(fileName);
                    /*/
                    //   xml�ļ���Դ��ʽһ��  assets   InputStream             
                    provinceArray = ProvincePullParse.Parse(getInputStreamFromAssets(fileName));                    
                    //*/

                    for(Province pro : provinceArray){
                        provinceStr += pro.getProvinceId() + " : " +pro.getProvinceName()+"\n";
                    }

                    showView.setText(provinceStr);
                    break;

                case R.id.getProvinceBtn2:
                    //  xml�ļ���Դ��ʽ���� res/xml   XmlResourceParser
                    provinceandcityParser = getXMLFromResXml(fileName);                    
                    provinceArray = ProvincePullParse.ParseXml(provinceandcityParser);
                    for(Province pro : provinceArray){
                        provinceStr += pro.getProvinceId() + " : " +pro.getProvinceName()+"\n";
                    }

                    showView.setText(provinceStr);
                    break;

                case R.id.getProvinceBtn3:
                    //  xml�ļ���Դ��ʽ���� url   ���ö��̸߳���
                    isFinishParser = false;
                    parserWhitThread();
                    mHandler.postDelayed(mRunnable, 200);

                    break;

                case R.id.getCityBtn:
                    // �������в��÷�ʽ��
                    provinceandcityParser = getXMLFromResXml(fileName);

                    cityArray = CityPullParse.ParseXml(provinceandcityParser);
                    for(City city : cityArray){
                        cityStr += "ʡ��ID["+city.getProvinceId() + "],����ID[" + city.getCityId() +"], "+city.getCityName()+"\n";
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
     * ֱ�ӻ�ȡAssets�ļ����е�XML�ļ�ת��String����parseʱ��ʧ�ܣ����쳣��Ϣ��PI must not start with xml��
     * �����Ų鷢��ȥ������<?xml version="1.0" encoding="utf-8"?> �������ɹ������Ƽ��÷�ʽ����һЩ����Ҫ��stringת��
     * ֱ�Ӳ��� getInputStreamFromAssets
     * 
     * @param fileName
     * @return : ��ȡ��Assets�ļ����µ�xml�ļ��ַ���
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
     * ͬ��ɾ�����У����ܽ����ɹ���
     * @param fileName
     * @return ����xml�ļ���inputStream
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
     * ��ȡXML�ļ���xml�ļ��ŵ�res/xml�ļ����У���XMLΪ�����ļ������Ƽ��÷���
     * 
     * @param fileName
     * @return : ��ȡ��res/xml�ļ����µ�xml�ļ�������XmlResourceParser����XmlPullParser�����ࣩ
     */
    public XmlResourceParser getXMLFromResXml(String fileName){
        XmlResourceParser xmlParser = null;
        try {
            //*/
            //  xmlParser = this.getResources().getAssets().openXmlResourceParser("assets/"+fileName);        // ʧ��,�Ҳ����ļ�
            xmlParser = this.getResources().getXml(R.xml.provinceandcity);
            /*/
            // xml�ļ���resĿ¼�� Ҳ�����ô˷�������inputStream
            InputStream inputStream = this.getResources().openRawResource(R.xml.provinceandcity);
            /*/
            return xmlParser;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return xmlParser;
    }

    /**
     * ��ȡurl��xml��Դ ת��String
     * @param url
     * @return ���� ��ȡurl��xml�ַ���
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
            outputString = new String(outputString.getBytes("ISO-8859-1"), "utf-8");    // �����������

            Log.i("HttpClientConnector", "���ӳɹ�");
        } catch (Exception e) {
            Log.i("HttpClientConnector", "����ʧ��");
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return outputString;
    }

    /**
     * ����SDcard xml�ļ�
     * @param fileName
     * @return ����xml�ļ���inputStream
     */     
    public InputStream getInputStreamFromSDcard(String fileName){
        try {
            // ·������ʵ����Ŀ�޸�
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
     *  ���̼߳�������˵�xml����xml�ļ�����Ҳ��Ҫ�ø÷�ʽ����
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
     * �ȽϺ�ʱ�����½�һ���̣߳�����UI�߳�ANR
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
