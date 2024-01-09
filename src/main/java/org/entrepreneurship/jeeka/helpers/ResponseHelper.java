package org.entrepreneurship.jeeka.helpers;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {

    public static ResponseEntity returnError(String message) {
        Map<String, Object> err = new HashMap<>();
        err.put("message", message);
        err.put("status", 400);
        return ResponseEntity.status(400).body(err);
    }

    public static ResponseEntity returnSuccess(String message) {
        Map<String, Object> r = new HashMap<>();
        r.put("message", message);
        return ResponseEntity.ok(r);
    }

    public static ResponseEntity returnSuccessO(Object responseObj) {
        Map<String, Object> r = new HashMap<>();
        r.put("data", responseObj);
        return ResponseEntity.ok(r);
    }

    public static ResponseEntity returnSuccess(Object responseObj) {
        return ResponseEntity.ok(responseObj);
    }

    public static ResponseEntity returnError(String message, int code) {
        Map<String, Object> err = new HashMap<>();
        err.put("message", message);
        err.put("status", code);
        return ResponseEntity.status(code).body(err);
    }

    public static ResponseEntity respondFromWrapper(ResponseWrapper wrapper) {
        if (wrapper == null) {
            return ResponseHelper.returnError("Internal error", 500);
        }
        if (wrapper.getError() != null) {
            //error occured
            return ResponseHelper.returnError(wrapper.getError(), wrapper.getCode());
        }
        System.out.println("wrapper : "+ wrapper);
        System.out.println("wrapper data :::: "+ wrapper);
        return ResponseHelper.returnSuccess(wrapper.getData());
    }

    public static ResponseEntity respondFromWrapperNoLog(ResponseWrapper wrapper) {
        if (wrapper == null) {
            return ResponseHelper.returnError("Internal error", 500);
        }
        if (wrapper.getError() != null) {
            //error occured
            return ResponseHelper.returnError(wrapper.getError(), wrapper.getCode());
        }
        return ResponseHelper.returnSuccess(wrapper.getData());
    }

}
