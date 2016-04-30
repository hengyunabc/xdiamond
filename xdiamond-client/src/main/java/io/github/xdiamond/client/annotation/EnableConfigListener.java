package io.github.xdiamond.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 *
 * 这个类已废弃，无实际作用，在xml里配置的bean，不声明这个注解也可以扫描到。
 *
 * <pre>
 * 对于在xml文件里配置的bean，不是用@Service, @Controller这样子的bean，需要在类前面配置这个注解，才可以扫描到
 * </pre>
 * @author hengyunabc
 *
 */
@Deprecated
public @interface EnableConfigListener {

}
