package com.projectsandbox.components.website.config;

import com.projectsandbox.components.website.interceptor.CsrfInterceptor;
import com.projectsandbox.components.website.interceptor.UserInterceptor;
import com.projectsandbox.components.website.service.AuthenticationService;
import com.projectsandbox.components.website.service.CsrfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
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
    /*
        800 KB; keep in sync with limit in:
        - ProfilePictureUploadForm
     */
    private static final long MAX_FILE_UPLOAD_SIZE = 819200;

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private CsrfService csrfService;

    @Autowired
    private AuthenticationService authenticationService;

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

    @Bean
    public LocalValidatorFactoryBean validator()
    {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource());
        return localValidatorFactoryBean;
    }

    @Bean
    public MessageSource messageSource()
    {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("validation_messages");
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        return resourceBundleMessageSource;
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver mutlipartResolver()
    {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(MAX_FILE_UPLOAD_SIZE);
        return resolver;
    }

    @Override
    public Validator getValidator()
    {
        return validator();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // Add CSRF protection to every request
        registry.addInterceptor(new CsrfInterceptor(csrfService));
        registry.addInterceptor(new UserInterceptor(authenticationService));
    }

}
