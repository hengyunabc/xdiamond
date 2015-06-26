package io.github.xdiamond.config;

import io.github.xdiamond.web.csrf.CsrfFilter;
import io.github.xdiamond.web.shiro.UserServletFilter;

import java.util.EnumSet;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;

@Configuration
public class WebConfigurer {
  private final Logger logger = LoggerFactory.getLogger(WebConfigurer.class);

  @Autowired
  ServletContext servletContext;

  private MetricRegistry metricRegistry;

  @Value("${metrics.registryName}")
  String registryName;

  @Autowired
  Filter shiroFilter;

  @Autowired
  Filter shiroCacheCleanFilter;

  @PostConstruct
  public void init() {
    initMetricsFilter();
    initCsrfFilter();
    initDruid();
    initShiroFilter();
    initUserServletFilter();
  }

  private void initUserServletFilter() {
    // TODO 这里应该拦截哪些url?
    UserServletFilter userServletFilter = new UserServletFilter();
    Dynamic filter = servletContext.addFilter("userServletFilter", userServletFilter);
    filter.addMappingForUrlPatterns(null, true, "/*");
  }

  private void initDruid() {
    ServletRegistration.Dynamic druidStatServlet =
        servletContext.addServlet("druidStatServlet", new StatViewServlet());

    druidStatServlet.addMapping("/druid/*");
    druidStatServlet.setAsyncSupported(true);

    // TODO 这里改为只监控 /api, /clientapi 的数据？
    WebStatFilter druidWebStatFilter = new WebStatFilter();
    Dynamic filter = servletContext.addFilter("druidWebStatFilter", druidWebStatFilter);
    filter.setInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
    filter.addMappingForUrlPatterns(null, true, "/*");
  }

  private void initShiroFilter() {
    Dynamic addShirofilter = servletContext.addFilter("shiroFilter", shiroFilter);
    addShirofilter.addMappingForUrlPatterns(null, true, "/*");

    Dynamic addShiroCacheCleanFilter =
        servletContext.addFilter("shiroCacheCleanFilter", shiroCacheCleanFilter);
    addShiroCacheCleanFilter.addMappingForUrlPatterns(null, true, "/api/*");
  }

  private void initMetricsFilter() {
    metricRegistry = SharedMetricRegistries.getOrCreate(registryName);

    logger.debug("Initializing Metrics registries");
    servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, metricRegistry);
    servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, metricRegistry);

    logger.debug("Registering Metrics Filter");
    FilterRegistration.Dynamic metricsFilter =
        servletContext.addFilter("webappMetricsFilter", new InstrumentedFilter());

    EnumSet<DispatcherType> disps =
        EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

    metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
    metricsFilter.setAsyncSupported(true);

    logger.debug("Registering Metrics Servlet");
    ServletRegistration.Dynamic metricsAdminServlet =
        servletContext.addServlet("metricsServlet", new MetricsServlet());

    metricsAdminServlet.addMapping("/api/metrics/*");
    metricsAdminServlet.setAsyncSupported(true);
    metricsAdminServlet.setLoadOnStartup(2);
  }

  private void initCsrfFilter() {
    // 这里只拦截 /api/* 的请求，对于 /druid/不拦截
    Dynamic filter = servletContext.addFilter("csrfFilter", new CsrfFilter());
    filter.addMappingForUrlPatterns(null, true, "/api/*");
  }

}
