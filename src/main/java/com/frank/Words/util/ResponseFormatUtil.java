/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frank.Words.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author FRANK
 */
public class ResponseFormatUtil {
    
    public static Map<String,Object> OK(Object object){
        Map<String,Object> map = new HashMap<>(2);
        map.put("success", true);
        map.put("data", object);
        return map;
    }
    
    public static Map<String,Object> OK(List list){
        Map<String,Object> map = new HashMap<>(2);
        map.put("success", true);
        map.put("data", list);
        map.put("total", list != null ? list.size() : 0);
        return map;
    }
    
    public static Map<String,Object> ERROR(String msj){
        Map<String,Object> map = new HashMap<>(2);
        map.put("success", false);
        map.put("message", msj);
        return map;
    }
}
