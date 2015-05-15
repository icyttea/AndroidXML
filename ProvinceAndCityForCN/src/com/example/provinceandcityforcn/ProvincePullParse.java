package com.example.provinceandcityforcn;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * ����xml����ȡʡ��
 * @author zzp
 *
 */
public class ProvincePullParse {

    public static ArrayList<Province> Parse(String provinceString){
        ArrayList<Province> provinceArray = new ArrayList<Province>();

        try {
            //���幤�� XmlPullParserFactory
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            //��������� XmlPullParser
            XmlPullParser parser = factory.newPullParser();            

            //��ȡxml��������       
            parser.setInput(new StringReader(provinceString));

            provinceArray = ParseXml(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return provinceArray;
    }

    public static ArrayList<Province> Parse(InputStream provinceIS){
        ArrayList<Province> provinceArray = new ArrayList<Province>();
        try {
            //���幤�� XmlPullParserFactory
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            //��������� XmlPullParser
            XmlPullParser parser = factory.newPullParser();            

            //��ȡxml��������
            parser.setInput(provinceIS,"utf-8");

            provinceArray = ParseXml(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return provinceArray;
    }

    public static ArrayList<Province> ParseXml(XmlPullParser parser){
        ArrayList<Province> provinceArray = new ArrayList<Province>();
        Province provinceTemp = null;

        try {
            //��ʼ�����¼�
            int eventType = parser.getEventType();

            //�����¼����������ĵ�������һֱ����
            while (eventType != XmlPullParser.END_DOCUMENT) {                
                //��Ϊ������һ�Ѿ�̬�������������������switch
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // �����κβ��������ʼ������
                        break;

                    case XmlPullParser.START_TAG:
                        // ����XML�ڵ�����
                        // ��ȡ��ǰ��ǩ����
                        String tagName = parser.getName();

                        if(tagName.equals("province")){
                            provinceTemp = new Province();
                            provinceTemp.setProvinceId(Integer.parseInt(parser.getAttributeValue(0)));
                            provinceTemp.setProvinceName(parser.getAttributeValue(1));

                            provinceArray.add(provinceTemp);                            
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        // ���ڵ���ɣ����������������µ�����
                        break;
                    case XmlPullParser.END_DOCUMENT:

                        break;
                }

                // ��������next����������һ���¼������˵Ľ���ͳ���ѭ��#_#
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return provinceArray;
    }
}
