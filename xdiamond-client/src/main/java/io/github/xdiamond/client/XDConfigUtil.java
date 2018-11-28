package io.github.xdiamond.client;

import io.github.xdiamond.client.exception.XDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * API for app user invoke.
 * e.g
 * Integer taskInterval = XDConfigUtil.get("task.interval",Integer.class);
 * Integer taskInterval2 = XDConfigUtil.get("task.interval",Integer.class,15);
 * String logLevel = XDConfigUtil.get("log.level");
 * String logLevel2 = XDConfigUtil.get("log.level","INFO");
 *
 * @author fxltsbl3855
 *
 */
public class XDConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(XDConfigUtil.class);

    public static <T> T get(String key,Class<T> clazz){
        String value = get(key,"");
        if (String.class == clazz) {
            return (T) value;
        } else if (Integer.class == clazz || int.class == clazz) {
            return (T) Integer.valueOf(value);
        } else if (Long.class == clazz || long.class == clazz) {
            return (T) Long.valueOf(value);
        } else if (Double.class == clazz || double.class == clazz) {
            return (T) Double.valueOf(value);
        } else if (Short.class == clazz || short.class == clazz) {
            return (T) Short.valueOf(value);
        } else {
            return (T) value;
        }
    }

    public static <T> T get(String key,Class<T> clazz,T t){
        try {
            return get(key,clazz);
        }catch (Exception e){
            logger.error("get error",e);
            return t;
        }
    }

    public static String get(String key) {
       return get(key,"");
    }

    public static String get(String key,String defaultValueWhenError) {
        try {
            return XDConfigData.getIns().getValue(key);
        }catch (Exception e){
            logger.error("get config error",e);
            return defaultValueWhenError;
        }
    }
}
