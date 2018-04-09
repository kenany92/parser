/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

import com.ef.enums.Duration;
import com.ef.model.Result;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author kenany
 */
public class Parser {
    
    private static final String ACCESS_LOG_PARAM_KEY = "accesslog";
    
    private static final String START_DATE_PARAM_KEY = "startDate";
    
    private static final String DURATION_PARAM_KEY = "duration";
    
    private static final String THRESHOLD_PARAM_KEY = "threshold";
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        
    private static final DateTimeFormatter entryFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String pathToFile = null;
        
        String startDateStr = null;
        
        int threshold = 0;
        
        Duration duration = null;
        
        for (String arg : args) {
            
            String[] param = arg.split("=");
            
            String key = param[0];
            
            if(key.contains(ACCESS_LOG_PARAM_KEY)){
                pathToFile = param[1];
            }
            
            if(key.contains(START_DATE_PARAM_KEY)){
                startDateStr = param[1];
            }
            
            if(key.contains(DURATION_PARAM_KEY)){
                duration = Duration.valueOf(param[1]);
            }
            
            if(key.contains(THRESHOLD_PARAM_KEY)){
                threshold = Integer.valueOf(param[1]);
            }
        }
        
        LocalDateTime startDate = LocalDateTime.parse(startDateStr, entryFormatter);
        
        // calculation of the end date
        LocalDateTime endDate = duration.equals(Duration.daily) ? 
                ChronoUnit.DAYS.addTo(startDate, 1) : 
                ChronoUnit.HOURS.addTo(startDate, 1);
                        
        Map<String, Integer> result = getResult(pathToFile, startDate, endDate, threshold);
                
        result.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            Integer value = entry.getValue();
            
            System.out.println(key);
        });
    }
    
    /**
     * 
     * @param pathToFile
     * @param startDate
     * @param endDate
     * @param threshold
     * @return Map<key, value> where key is the IP Address and value is the number of requests
     */
    public static Map<String, Integer> getResult(String pathToFile, LocalDateTime startDate, LocalDateTime endDate, int threshold){
        
        Path path = Paths.get(pathToFile);
        
        Result filtered = new Result();
        
        // check if file exists
        if(!path.toFile().exists()){
            System.out.println("Check the file path");
            return null;
        }
        
        try {
            
            // usage of new java input output package for more efficient reading of file
            Stream<String> stream = Files.lines(path);
            
            // usage of stream iteration for more efficient
            stream.forEach(str -> {
                
                // split all line of file by the | separator
                String[] split = str.split("\\|");
                
                // the first item is the date as string
                String dateStr = split[0];
                
                // usage of formatter to convert string date to LocalDateTime
                LocalDateTime currentDate = LocalDateTime.parse(dateStr, formatter);
                
                // selection of IP Address which date is between startDate and enDate 
                // or date is equal of startDate
                if((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && currentDate.isBefore(endDate)){
                    
                    String ipAddress = split[1];
                    
                    filtered.addItem(ipAddress);
                    
                }
                
            });
            
        } catch (IOException e) {
            
            System.out.println("Check the file path");
            
        }
        
        Map<String, Integer> result = new HashMap<>();
        
        // filter all IP Address which count is equal or higher than threshold
        filtered.getResult().entrySet().forEach((entry) -> {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value >= threshold) {
                result.put(key, value);
            }
        });
        
        return result;
    }
    
}
