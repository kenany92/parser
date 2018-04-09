/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kenany
 */
public class Result {
    
    Map<String, Integer> result = new HashMap<String, Integer>();
    
    /**
     * 
     * @param ipAddress 
     * add IP Address if it isn't exist in result object or increment the count value
     */
    public void addItem(String ipAddress){
        
        Integer count = 1;
        
        if(result.containsKey(ipAddress)){
            count = result.get(ipAddress) + 1;
            result.replace(ipAddress, count+1);
        }else{
            result.put(ipAddress, count);
        }
    }
    
    public Map<String, Integer> getResult(){
        return result;
    }
    
}
