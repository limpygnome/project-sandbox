package com.limpygnome.projectsandbox.website.config;

import com.limpygnome.projectsandbox.website.interceptor.CsrfInterceptor;
import com.limpygnome.projectsandbox.website.service.CsrfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;

import javax.annotation.PostConstruct;

/**
 * Created by limpygnome on 20/07/15.
 */
@Configuration
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter
{
    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private CsrfService csrfService;

    @PostConstruct
    public void init()
    {
        requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
    {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/resources/favicon.ico");
        registry.addResourceHandler("/content/**").addResourceLocations("/resources/content/");
    }

    @Bean
    public TilesConfigurer setupTilesConfigurer()
    {
        TilesConfigurer configurer = new TilesConfigurer();
        configurer.setDefinitions(new String[]{"/WEB-INF/views.xml"});
        return configurer;
    }

    @Bean
    public UrlBasedViewResolver setupTilesViewResolver()
    {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setViewClass(TilesView.class);
        return viewResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // Add CSRF protection to every request
        registry.addInterceptor(new CsrfInterceptor(csrfService));
    }

    @Bean
    public MessageSource messageSource()
    {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("validation-errors");
        return resourceBundleMessageSource;
    }
}
