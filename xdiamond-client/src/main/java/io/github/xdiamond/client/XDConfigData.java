package io.github.xdiamond.client;

import io.github.xdiamond.common.ResolvedConfigVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * config value container
 *
 * @author fxltsbl3855
 *
 */
public class XDConfigData {
    private static final Logger logger = LoggerFactory.getLogger(XDConfigData.class);
    private static XDConfigData ourInstance = new XDConfigData();
    private ConcurrentHashMap<String,String> datamap = new ConcurrentHashMap<String,String>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public static XDConfigData getIns() {
        return ourInstance;
    }

    private XDConfigData() {
    }



    public  String getValue(String key)  {
        lock.readLock().lock();
        try {
            if(!datamap.containsKey(key)){
                return null;
            }
            return datamap.get(key);
        }finally {
            lock.readLock().unlock();
        }
    }

    public void transData(Map<String, ResolvedConfigVO> voMap){
        lock.writeLock().lock();
        try {
            this.datamap.clear();
            if(voMap != null && voMap.size() > 0) {
                for (Map.Entry<String, ResolvedConfigVO> entry : voMap.entrySet()) {
                    this.datamap.put(entry.getValue().getConfig().getKey(), entry.getValue().getConfig().getValue());
                }
            }
        }finally {
            lock.writeLock().unlock();
        }
        logger.debug("current config data is : {}",toString());
    }

    public void putValue(String key,String value){
        lock.readLock().lock();
        try {
            datamap.put(key,value);
        }finally {
            lock.readLock().unlock();
        }
    }

    public void updateValue(String key,String value){
        lock.readLock().lock();
        try {
            if(!datamap.containsKey(key)) {
                logger.error("update key,but not find key,key="+key);
                return;
            }
            datamap.put(key,value);
        }finally {
            lock.readLock().unlock();
        }

    }
    public String delValue(String key){
        lock.readLock().lock();
        try {
            return datamap.remove(key);
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString(){
        if(datamap.size() == 0){
            return "empty map";
        }
        StringBuilder sb = new StringBuilder();
        for(Map.Entry entry : datamap.entrySet()){
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(", ");
        }
        return sb.toString();
    }
}
