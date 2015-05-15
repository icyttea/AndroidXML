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
 * 解析xml，获取省份
 * @author zzp
 *
 */
public class ProvincePullParse {

    public static ArrayList<Province> Parse(String provinceString){
        ArrayList<Province> provinceArray = new ArrayList<Province>();

        try {
            //定义工厂 XmlPullParserFactory
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            //定义解析器 XmlPullParser
            XmlPullParser parser = factory.newPullParser();            

            //获取xml输入数据       
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
            //定义工厂 XmlPullParserFactory
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            //定义解析器 XmlPullParser
            XmlPullParser parser = factory.newPullParser();            

            //获取xml输入数据
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
            //开始解析事件
            int eventType = parser.getEventType();

            //处理事件，不碰到文档结束就一直处理
            while (eventType != XmlPullParser.END_DOCUMENT) {                
                //因为定义了一堆静态常量，所以这里可以用switch
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // 不做任何操作或初开始化数据
                        break;

                    case XmlPullParser.START_TAG:
                        // 解析XML节点数据
                        // 获取当前标签名字
                        String tagName = parser.getName();

                        if(tagName.equals("province")){
                            provinceTemp = new Province();
                            provinceTemp.setProvinceId(Integer.parseInt(parser.getAttributeValue(0)));
                            provinceTemp.setProvinceName(parser.getAttributeValue(1));

                            provinceArray.add(provinceTemp);                            
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        // 单节点完成，可往集合里边添加新的数据
                        break;
                    case XmlPullParser.END_DOCUMENT:

                        break;
                }

                // 别忘了用next方法处理下一个事件，忘了的结果就成死循环#_#
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
