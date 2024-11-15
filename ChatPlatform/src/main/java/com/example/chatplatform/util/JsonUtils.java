package com.example.chatplatform.util;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson2.*;
import com.fasterxml.jackson.core.json.JsonWriteFeature;

import java.util.List;

/**
 * @author archi
 */
public class JsonUtils {

    //使用FastJson2
    public static String objToJsonString(Object o){
        try {
            return JSON.toJSONString(0);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    /*
     将String转化为JSONObject
     */
    public static JSONObject stringToJsonObj(String text){
        try {
            return JSON.parseObject(text);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    /*
        将Json转化为Java对象
     */
    public static <T> T jsonStrToJavaObj(String text, Class<T>beanType){
        try {
            return JSON.parseObject(text,beanType);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    /*
        将Java对象序列化为Json
     */
    public static String javaBeanToJsonString(Object data){
        try {
            return JSON.toJSONString(data, JSONWriter.Feature.WriteMapNullValue);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    /*
        将Json转化为BeanList
     */
    public static <T> List<T> jsonToBeanList(String text, Class<T>beanType){
        try {
            JSONArray array = JSON.parseArray(text);
            return array.toJavaList(beanType);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}