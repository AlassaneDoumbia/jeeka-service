/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entrepreneurship.jeeka.helpers;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author alassanedoumbia
 */
@Component
public class ConstUtil {

    public final static String APP_VERSION = "V0.0.1";
    public final static String APP_NAME = "JEEKA";
    //logging level const
    public static final String INFO_LEVEL = "info";
    public static final String WARNING_LEVEL = "warning";
    public static final String ERROR_LEVEL = "error";
    public static final String TRACE_LEVEL = "info";
    public static final String DEBUG_LEVEL = "info";

    public static final String APPLI_SOURCE = "JEEKA";

    public static final DateFormat DATE_FORMAT_HEURE = new SimpleDateFormat("HH:mm:ss");
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   

    public static final String MSISDN_HEADER_NAME = "__msisdn__";
    public static final String USER_LOCALISATION_HEADER_NAME = "__user_localisation__";
    public static final String USER_LOCALISATION_ADDRESS_HEADER_NAME = "__user_localisation_address__";
    public static final String TERMINAL_UIID_HEADER_NAME = "__terminal_uuid__";
    public static final String TERMINAL_OS_HEADER_NAME = "__terminal_os__";
    public static final String TERMINAL_VERSION_HEADER_NAME = "__terminal_version__";
    public static final String TERMINAL_MANUFACTURER_HEADER_NAME = "__terminal_manufacturer__";
    public static final String TERMINAL_MODEL_HEADER_NAME = "__terminal_model__";
    public static final String USER_HEADER_NAME = "__user__";
    public static final String LOCALISATION_HEADER_NAME = "__localisation__";
    
    public static final String USERNAME_HEADER_NAME = "__username__";
    public static final String FEE_HEADER_NAME = "__fee__";
    public static final String APP_VERSION_HEADER_NAME = "__app_version__";
    public static String ENABLED = "ENABLED";
    public static String DISABLED = "DISABLED";

    public static String LANG = "fr";

    public final static String[] FREE_URL = {
        "/", "/files/*", "/api/auth/**", "/api/**",
        "/apple-app-site-association", "/.well-known/*"};
    
    public static void catchException(Throwable exception) {
        LoggingUtils.error(exception.getMessage());
        exception.printStackTrace(System.out);
        exception.printStackTrace(System.err);
    }


}
