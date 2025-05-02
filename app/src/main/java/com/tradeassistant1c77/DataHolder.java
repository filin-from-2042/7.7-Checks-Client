package com.tradeassistant1c77;

import java.util.HashMap;
import java.util.Map;

public class DataHolder {
    private static Map<String, Object> data = new HashMap<>()  ;
    public static Object getData(String key) {return data.get(key);}
    public static void setData(String key, Object newData) {data.put(key,newData);}
}