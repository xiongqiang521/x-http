package com.xq.xhttp.http.handler;


import com.xq.xhttp.http.annotation.HttpApiScan;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HttpApiScanRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        System.out.println("com.xq.xtool.http.handler.HttpApiScanRegister.registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.beans.factory.support.BeanNameGenerator)");
        HttpApiScan httpApiScan = importingClassMetadata.getAnnotations().get(HttpApiScan.class).synthesize();
        String[] paths = httpApiScan.value();

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition();

        Set<String> basePackages = new HashSet<>();
        Collections.addAll(basePackages, paths);
        basePackages.add(ClassUtils.getPackageName(HttpApiScanRegister.class.getPackage().getName()));
        beanDefinitionBuilder.getBeanDefinition().setBeanClass(HttpApiConfig.class);
        beanDefinitionBuilder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
        registry.registerBeanDefinition(HttpApiConfig.class.getName(), beanDefinitionBuilder.getBeanDefinition());

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        System.out.println("com.xq.xtool.http.handler.HttpApiScanRegister.registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        System.out.println("com.xq.xtool.http.handler.HttpApiScanRegister#setResourceLoader");
    }
}
