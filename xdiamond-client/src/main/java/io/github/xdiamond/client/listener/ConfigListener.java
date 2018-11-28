package io.github.xdiamond.client.listener;

import io.github.xdiamond.client.XDConfigData;
import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;
import io.github.xdiamond.client.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * properties listener
 *
 * @author fxltsbl3855
 */
@Component
class ConfigListener {
    private static final Logger logger = LoggerFactory.getLogger(ConfigListener.class);

    @AllKeyListener
    public void allKeyListener(ConfigEvent event) {
        logger.debug("allKeyListener triggered ,key : {}, oldValue : {}, newValue : {}",event.getKey() ,event.getOldValue(),event.getKey());
        if(event.getEventType() == EventType.ADD){
            XDConfigData.getIns().putValue(event.getKey(),event.getValue());
        }else if(event.getEventType() == EventType.DELETE){
            XDConfigData.getIns().delValue(event.getKey());
        }else if(event.getEventType() == EventType.UPDATE){
            XDConfigData.getIns().updateValue(event.getKey(),event.getValue());
        }
    }
}
