package io.github.xdiamond.client.event;

import io.github.xdiamond.client.XDiamondConfig;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MethodInvoker;

/**
 * 用于回调一个对象的函数的Listener的包装类
 *
 * @author hengyunabc
 *
 */
public class ObjectListenerMethodInvokeWrapper extends MethodInvoker {
	static private final Logger logger = LoggerFactory
			.getLogger(ObjectListenerMethodInvokeWrapper.class);

	/**
	 * Listener的class
	 * name，目前只支持io.github.xdiamond.client.event.OneKeyListener，io
	 * .github.xdiamond.client.event.AllKeyListener
	 */
	String listenerClassName;
	String key;
	XDiamondConfig xDiamondConfig;

	public void init() {
		if (listenerClassName.equals(OneKeyListener.class.getName())) {
			xDiamondConfig.addOneKeyListener(key, new OneKeyListener() {
				@Override
				public void onConfigEvent(ConfigEvent event) {
					try {
						setArguments(new Object[] { event });
						if(!isPrepared()){
							prepare();
						}
						invoke();
					} catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
						logger.error("XDiamond Listener invoke error! event:"
								+ event, e);
					}
				}
			});
		} else if (listenerClassName.equals(AllKeyListener.class.getName())) {
			xDiamondConfig.addAllKeyListener(new AllKeyListener() {
				@Override
				public void onConfigEvent(ConfigEvent event) {
					try {
						setArguments(new Object[] { event });
						if(!isPrepared()){
							prepare();
						}
						invoke();
					} catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
						logger.error("XDiamond Listener invoke error! event:"
								+ event, e);
					}
				}
			});
		}
	}

	public String getListenerClassName() {
		return listenerClassName;
	}

	public void setListenerClassName(String listenerClassName) {
		this.listenerClassName = listenerClassName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public XDiamondConfig getxDiamondConfig() {
		return xDiamondConfig;
	}

	public void setxDiamondConfig(XDiamondConfig xDiamondConfig) {
		this.xDiamondConfig = xDiamondConfig;
	}
}
