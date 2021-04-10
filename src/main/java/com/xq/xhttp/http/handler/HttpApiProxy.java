package com.xq.xhttp.http.handler;

import com.xq.xhttp.http.annotation.*;
import com.xq.xhttp.http.execption.Try;
import com.xq.xhttp.http.execption.XHttpException;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpApiProxy<T> implements InvocationHandler {
    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;

    private static final Class<?>[] CLASSES = new Class<?>[]{
            Data.class, Header.class, Param.class, PathVal.class
    };

    private final ConcurrentHashMap<Method, MethodHandle> methodHandleMap = new ConcurrentHashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (method.isDefault()) {
            // 使用map可以提高性能，以后的不需要重复构建对象
            MethodHandle defaultMethodHandle = methodHandleMap.computeIfAbsent(method, Try.ignoreException(key -> {
                Constructor<MethodHandles.Lookup> declaredConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                declaredConstructor.setAccessible(true);
                MethodHandles.Lookup lookup = declaredConstructor.newInstance(declaringClass, ALLOWED_MODES);
                return lookup.unreflectSpecial(method, declaringClass);
            }));

            MethodHandle methodHandle = defaultMethodHandle.bindTo(proxy);
            return methodHandle.invokeWithArguments(params);
        }
        // 普通类类
        Class<?> clazz = declaringClass;
        if (clazz.equals(Object.class)) {
            throw new XHttpException(String.format("<%s>应该是一个接口"));
        }
        HttpBuild build = HttpBuild.build();
        classAnnotationParse(declaringClass, build);
        methodAnnotationParse(method, build);

        paramsAnnotationParse(method, params, build);
        Type type = method.getGenericReturnType();
        // 泛型
        if (type instanceof ParameterizedType) {
            ParameterizedType returnType = (ParameterizedType) type;
            if (ResponseEntity.class.equals(returnType.getRawType())) {
                Type[] actualTypeArguments = returnType.getActualTypeArguments();
                String typeName = actualTypeArguments[0].getTypeName();
                Class<?> aClass = Class.forName(typeName);
                return build.exchange(aClass);
            }
        }

        return build.exchangeObject(method.getReturnType());
    }

    private HttpBuild paramsAnnotationParse(Method method, Object[] params, HttpBuild build) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            paramAnnotationParse(parameter, params[i], build);
        }
        return build;
    }

    private HttpBuild paramAnnotationParse(Parameter parameter, Object data, HttpBuild build) {
        if (data == null) {
            return build;
        }
        Annotation[] annotations = parameter.getAnnotations();
        if (annotations.length != 1) {
            throw new XHttpException(String.format("参数<%s>应该有且仅有注解%s中的一个", parameter.getName(), Arrays.toString(CLASSES)));
        }

        Annotation annotation = annotations[0];

        if (annotation instanceof Data) {
            build.data(data);
        } else if (annotation instanceof Header) {
            Header header = (Header) annotation;
            build.addHeader(header.value(), String.valueOf(data));
        } else if (annotation instanceof Param) {
            if (data instanceof Map) {
                build.param((Map<String, String>) data);
            } else {
                throw new XHttpException(String.format("注解Param的参数<%s>类型应该是 Map<String, String>", parameter.getName()));
            }
        } else if (annotation instanceof PathVal) {
            PathVal pathVal = (PathVal) annotation;
            if (StringUtils.hasText(pathVal.value())) {
                build.addPathParam(pathVal.value(), String.valueOf(data));
            } else {
                build.addPathParam(String.valueOf(data));
            }
        } else {
            throw new XHttpException(String.format("参数<%s>应该有且仅有注解%s中的一个", parameter.getName(), Arrays.toString(CLASSES)));
        }
        return build;
    }

    /**
     * 处理方法上面的注解
     *
     * @param method
     * @param build
     * @return
     */
    private HttpBuild methodAnnotationParse(Method method, HttpBuild build) {
        // 处理HttpApiPath注解
        HttpApiPath httpApiHost = method.getAnnotation(HttpApiPath.class);
        if (httpApiHost != null) {
            build.path(checkBlank(httpApiHost.value(), httpApiHost.path()));
            build.method(httpApiHost.method());
        }

        // 处理HttpApiHeader注解，可能有多个
        HttpApiHeaders annotation = method.getAnnotation(HttpApiHeaders.class);
        if (annotation == null) {
            return build;
        }
        HttpApiHeader[] HttpApiHeaders = annotation.value();
        HttpApiHeader httpApiHeader = method.getAnnotation(HttpApiHeader.class);
        for (HttpApiHeader apiHeader : HttpApiHeaders) {
            build.addHeader(apiHeader.key(), apiHeader.value());
        }
        return build;
    }


    /**
     * 处理class上面的注解
     *
     * @param clazz
     * @param build
     * @return
     */
    private HttpBuild classAnnotationParse(Class<?> clazz, HttpBuild build) {
        HttpApiHost httpApiHost = clazz.getAnnotation(HttpApiHost.class);
        if (httpApiHost != null) {
            build.host(checkBlank(httpApiHost.value(), httpApiHost.host()));
        }
        return build;
    }

    /**
     * 返回两个参数中不为空的，连个都不为空返回str1
     *
     * @param str1
     * @param str2
     * @return
     */
    private String checkBlank(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == null ? str2 : str1;
        }
        return StringUtils.hasText(str1) ? str1 : str2;
    }

}
