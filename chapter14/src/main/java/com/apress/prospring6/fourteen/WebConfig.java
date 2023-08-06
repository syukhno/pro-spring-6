/*
Freeware License, some rights reserved

Copyright (c) 2023 Iuliana Cosmina

Permission is hereby granted, free of charge, to anyone obtaining a copy 
of this software and associated documentation files (the "Software"), 
to work with the Software within the limits of freeware distribution and fair use. 
This includes the rights to use, copy, and modify the Software for personal use. 
Users are also allowed and encouraged to submit corrections and modifications 
to the Software for the benefit of other users.

It is not allowed to reuse,  modify, or redistribute the Software for 
commercial use in any way, or for a user's educational materials such as books 
or blog articles without prior permission from the copyright holder. 

The above copyright notice and this permission notice need to be included 
in all copies or substantial portions of the software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS OR APRESS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.apress.prospring6.fourteen;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Created by iuliana on 25/12/2022
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.apress.prospring6.fourteen"})
public class WebConfig  implements WebMvcConfigurer, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        return multipartResolver;
    }

     @Bean
    public SpringResourceTemplateResolver templateResolver(){
        var resolver = new SpringResourceTemplateResolver();
         resolver.setApplicationContext(this.applicationContext);
         resolver.setPrefix("/WEB-INF/views/");
         resolver.setSuffix(".html");
         resolver.setTemplateMode(TemplateMode.HTML);
         resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    @Description("Thymeleaf Template Engine")
    public SpringTemplateEngine templateEngine() {
        var engine = new SpringTemplateEngine();
        engine.addDialect(new Java8TimeDialect());
        engine.setTemplateResolver(templateResolver());
        engine.setTemplateEngineMessageSource(messageSource());
        engine.setEnableSpringELCompiler(true);
        return engine;
    }

    @Bean
    @Description("Thymeleaf View Resolver")
    public ViewResolver viewResolver() {
        var viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Bean
    ResourceBundleThemeSource themeSource() {
        return new ResourceBundleThemeSource();
    }

    @Bean
    public Validator validator() {
        final var validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

    @Override
    public Validator getValidator() {
        return validator();
    }

    //Declare our static resources. I added cache to the java config but it?s not required.
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/images/**", "/styles/**")
                .addResourceLocations("/images/", "/styles/");
    }

    /*@Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }*/

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/home");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/*");
        registry.addInterceptor(themeChangeInterceptor());
        registry.addInterceptor(webChangeInterceptor());
    }

    @Bean
    MessageSource messageSource() {
        var messageResource = new ReloadableResourceBundleMessageSource();
        messageResource.setBasename("classpath:i18n/global");
        messageResource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageResource.setUseCodeAsDefaultMessage(true);
        messageResource.setFallbackToSystemLocale(true);
        // # -1 : never reload, 0 always reload
        //messageResource.setCacheSeconds(0);
        return messageResource;
    }

    /*The LocaleChangeInterceptor and CookieLocaleResolver work together to provide a seamless mechanism for handling and
        managing the user's preferred locale in a Spring MVC application.

    Here's how these two components collaborate:

    Locale Change Request:
    The LocaleChangeInterceptor monitors incoming requests for indications of a desired locale change.
    This can be achieved through request parameters, headers, or any other custom mechanism you configure.
    When the LocaleChangeInterceptor detects a request indicating a locale change, it updates the user's locale based on
    the provided information. This updated locale is temporarily stored in the user's session for the duration of the request processing.

    Locale Resolution with CookieLocaleResolver:
    After the LocaleChangeInterceptor has updated the user's locale, the CookieLocaleResolver comes into play.
    When the CookieLocaleResolver is invoked during the request processing, it first checks if there's a cookie with the
    specified name (e.g., "myLocaleCookie") in the incoming request.

    Using Stored Locale from Cookie:
    If the cookie is found and contains a valid locale value, the CookieLocaleResolver retrieves the locale information
    from the cookie and uses it as the user's preferred locale for the current request.
    This allows the user's preference stored in the cookie to override any temporary changes made by the LocaleChangeInterceptor.

    Storing Updated Locale in Cookie:
    If the LocaleChangeInterceptor changes the user's locale based on the request, and if the application is configured to
    store locale changes in cookies, the CookieLocaleResolver can update the response by setting a cookie with the updated locale information.
    This cookie will be sent back to the user's browser and will be included in subsequent requests, ensuring that the user's
    locale preference is consistently applied across multiple interactions with the application.
    By working together, the LocaleChangeInterceptor allows for temporary changes to the user's preferred locale during
    a specific request, while the CookieLocaleResolver ensures that the user's long-term preferred locale, stored in a cookie,
    remains intact and influences subsequent requests. This collaboration provides a comprehensive and flexible way to manage
    user locale preferences in a Spring MVC application, enabling seamless language and region handling for internationalization
    and localization needs.*/

    @Bean
    LocaleChangeInterceptor localeChangeInterceptor() {
        var localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    ThemeChangeInterceptor themeChangeInterceptor() {
        var themeChangeInterceptor = new ThemeChangeInterceptor();
        themeChangeInterceptor.setParamName("theme");
        return themeChangeInterceptor;
    }

    @Bean
    CookieLocaleResolver localeResolver() {
        var cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        cookieLocaleResolver.setCookieMaxAge(3600);
        cookieLocaleResolver.setCookieName("locale");
        return cookieLocaleResolver;
    }

    @Bean
    CookieThemeResolver themeResolver() {
        var cookieThemeResolver = new CookieThemeResolver();
        cookieThemeResolver.setDefaultThemeName("green");
        cookieThemeResolver.setCookieMaxAge(3600);
        cookieThemeResolver.setCookieName("theme");
        return cookieThemeResolver;
    }

    @Bean
    WebContentInterceptor webChangeInterceptor() {
        var webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.setCacheSeconds(0);
        webContentInterceptor.setSupportedMethods("GET", "POST", "PUT", "DELETE");
        return webContentInterceptor;
    }

}
