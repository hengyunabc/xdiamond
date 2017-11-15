package io.github.xdiamond.client.spring;

import io.github.xdiamond.client.XDConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.Properties;

/**
 * load config properties to container
 *
 * @author fxltsbl3855
 *
 */
public class XDConfigLoader extends PropertyPlaceholderConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(XDConfigLoader.class);
    protected Resource[] locations;

    public void loadDataToXDConfig(){
        if(locations == null || locations.length == 0){
            logger.info("properties file is empty.");
            return;
        }
        for(Resource location : locations){
            try {
                Properties prop = PropertyUtils.getProperties(location.getFile().getAbsolutePath());
                for(Map.Entry entry : prop.entrySet()){
                    XDConfigData.getIns().putValue(entry.getKey().toString(),entry.getValue().toString());
                }
            } catch (Exception e) {
                logger.error("loadDataToXDConfig error",e);
            }
        }
    }

    @Override
    public void setLocations(Resource[] locations) {   //由于location是父类私有，所以需要记录到本类的locations中
        super.setLocations(locations);
        this.locations = locations;
        loadDataToXDConfig();
    }

    @Override
    public void setLocation(Resource location) {   //由于location是父类私有，所以需要记录到本类的locations中
        super.setLocation(location);
        this.locations = new Resource[]{location};
    }

}
