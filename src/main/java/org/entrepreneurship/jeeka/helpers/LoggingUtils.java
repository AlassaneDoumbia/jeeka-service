/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entrepreneurship.jeeka.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.util.Date;


public class LoggingUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);

    public static void error(String message, String user) {
        loggingMethod(user, message, ConstUtil.ERROR_LEVEL, LOGGER, null);
    }

    public static void error(String message) {
        loggingMethod(null, message, ConstUtil.ERROR_LEVEL, LOGGER, null);
    }

    public static void info(String message, String user) {
        loggingMethod(user, message, ConstUtil.INFO_LEVEL, LOGGER, null);
    }

    public static void info(String message) {
        loggingMethod(null, message, ConstUtil.INFO_LEVEL, LOGGER, null);
    }

    public static void loggingMethod(String user, String message, String level, Logger logger, HttpServletRequest httpServletRequest) {
        if (httpServletRequest != null) {
            MDC.put("ip", httpServletRequest.getRemoteHost());
        }
        MDC.put("user", user);
        if (ConstUtil.INFO_LEVEL.equals(level)) {
            logger.info(ManagementFactory.getRuntimeMXBean().getName() + " - "+ConstUtil.DATE_FORMAT.format(new Date()) + " =O=> " + message);
        } else if (ConstUtil.WARNING_LEVEL.equals(level)) {
            logger.warn(ManagementFactory.getRuntimeMXBean().getName() + " - "+ConstUtil.DATE_FORMAT.format(new Date()) + " =O=> " + message);
        } else if (ConstUtil.ERROR_LEVEL.equals(level)) {
            logger.error(ManagementFactory.getRuntimeMXBean().getName() + " - "+ConstUtil.DATE_FORMAT.format(new Date()) + " =O=> " + message);
        } else if (ConstUtil.TRACE_LEVEL.equals(level)) {
            logger.trace(ManagementFactory.getRuntimeMXBean().getName() + " - "+ConstUtil.DATE_FORMAT.format(new Date()) + " =O=> " + message);
        } else {// Level is debug
            logger.debug(ManagementFactory.getRuntimeMXBean().getName() + " - "+ConstUtil.DATE_FORMAT.format(new Date()) + " =O=> " + message);
        }
        MDC.remove("ip");
        MDC.remove("user");
    }

    public LoggingUtils() {
    }
}
