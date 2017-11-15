package io.github.xdiamond.client.spring;

/**
 * properties file tool
 *
 * @author fxltsbl3855
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyUtils {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    public static Properties getProperties(String filename) {
        FileInputStream fis = null;
        Properties p = new Properties();
        try {
            fis = new FileInputStream(filename);
            p.load(fis);
        }catch (Exception e) {
            logger.error("read properties file error",e);
        }finally {
            if(fis != null) {
                try {
                    fis.close();
                }catch (Exception e) {
                    logger.error("close properties file error",e);
                }
            }
        }
        return p;
    }
}
