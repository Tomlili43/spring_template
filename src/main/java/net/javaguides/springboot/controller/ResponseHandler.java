package net.javaguides.springboot.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", message);
            map.put("status", status.value());
            if (responseObj==null){
                map.put("data", "");
            } else {
                map.put("data", responseObj);
            }
            // System.out.println(map);
            return new ResponseEntity<Object>(map,status);
    }
}