package com.bobocode.context.impl;

import com.bobocode.context.ApplicationContext;
import com.bobocode.context.annotation.Bean;
import com.bobocode.context.exception.NoSuchBeanException;
import com.bobocode.context.exception.NoUniqueBeanException;
import lombok.SneakyThrows;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private final Map<String, Object> beans = new ConcurrentHashMap<>();

    public AnnotationConfigApplicationContext(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> beanClasses = reflections.getTypesAnnotatedWith(Bean.class);
        init(beanClasses);
    }

    @SneakyThrows
    private void init(Set<Class<?>> classes) {
        for (Class<?> beanClass : classes) {
            String name = resolveBeanName(beanClass);
            Object instance = beanClass.getConstructor().newInstance();
            beans.put(name, instance);
        }
    }

    private String resolveBeanName(Class<?> beanClass) {
        String declaredName = beanClass.getAnnotation(Bean.class).value();
        return declaredName.isBlank() ? resolveBeanClassName(beanClass) : declaredName;
    }

    private String resolveBeanClassName(Class<?> beanClass) {
        String className = beanClass.getSimpleName();
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        Map<String, T> matchingBeans = getAllBeans(beanType);
        if (matchingBeans.isEmpty()) {
            throw new NoUniqueBeanException();
        }
        return matchingBeans.values().stream()
                .findAny()
                .map(beanType::cast)
                .orElseThrow(NoSuchBeanException::new);
    }

    @Override
    public <T> T getBean(String name, Class<T> beanType) {
        return beans.entrySet().stream()
                .filter(beanEntry -> name.equals(beanEntry.getKey()))
                .findAny()
                .map(Map.Entry::getValue)
                .map(beanType::cast)
                .orElseThrow(NoSuchBeanException::new);
    }

    @Override
    public <T> Map<String, T> getAllBeans(Class<T> beanType) {
        return beans.entrySet()
                .stream()
                .filter(entry -> beanType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> beanType.cast(entry.getValue())));
    }

}
