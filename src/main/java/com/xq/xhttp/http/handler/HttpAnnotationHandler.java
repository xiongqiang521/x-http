package com.xq.xhttp.http.handler;


import com.xq.xhttp.http.annotation.HttpApiHost;
import com.xq.xhttp.http.execption.Try;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class HttpAnnotationHandler
        implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, ResourceLoaderAware {
    private ApplicationContext applicationContext;
    private ResourcePatternResolver resourcePatternResolver;
    private MetadataReaderFactory metadataReaderFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Class<?>> classes = new LinkedHashSet<>();

        BeanDefinition registerDefinition = registry.getBeanDefinition(HttpApiConfig.class.getName());
        String basePackages = Objects.requireNonNull(registerDefinition.getPropertyValues().get("basePackage")).toString();
        if (basePackages == null) {
            return;
        }
        Arrays.stream(basePackages.split(","))
                // 执行 待异常的方法
                .map(Try.check(this::scannerPackages))
                .filter(Try::isSuccess)
                .map(Try::getResult)
                .forEach(classes::addAll);

        for (Class<?> clazz : classes) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
            beanDefinition.setBeanClass(HttpApiFactionBean.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinition);
        }
    }

    private Set<Class<?>> scannerPackages(String basePackage) throws Exception {
        Environment environment = applicationContext.getEnvironment();
        String placeholders = environment.resolveRequiredPlaceholders(basePackage);
        String resourcePath = ClassUtils.convertClassNameToResourcePath(placeholders);

        Set<Class<?>> set = new LinkedHashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resourcePath + "/**/*.class";

        Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(className);
                if (clazz.getAnnotation(HttpApiHost.class) != null) {
                    set.add(clazz);
                }
            }
        }

        return set;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }
}
