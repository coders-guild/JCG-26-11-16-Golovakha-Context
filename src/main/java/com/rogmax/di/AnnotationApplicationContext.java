/*
 * Copyright 2016 the original author or authors.
 * Rogmax Digital, rogmax.com
 */
package com.rogmax.di;

import com.rogmax.di.annotations.Bean;
import com.rogmax.di.annotations.Init;
import com.rogmax.di.annotations.Inject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.rogmax.di.utils.ReflectionUtils.*;

/**
 * @author Roman Golovakha
 *         Date: 12.11.2016.
 */
public class AnnotationApplicationContext implements ApplicationContext {

    private Map<Class, Object> beansContainer;
    private Map<Class, List<Class>> beanInterfaces;

    public AnnotationApplicationContext(String path) throws Exception {
        List<Class> classes = getClassesAnnotatedWith(path, Bean.class);
        List<Object> beans = createInstances(classes);
        beansContainer = beans.stream().collect(Collectors.toMap(Object::getClass, i -> i));
        beanInterfaces = getInterfaces(classes);
        beans.stream().forEach(this::processInject);
        beans.stream().forEach(o -> invokeMethodAnnotatedWith(o, Init.class));
    }

    public <T> T getBean(Class<T> type) {
        return (T) beansContainer.get(type);
    }

    private void processInject(Object o) {
        Arrays.stream(o.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .forEach(f -> processInject(o, f));
    }

    private void processInject(Object o, Field f) {
        Class injectedType = getImpl(f.getType(), f.getName());
        if (injectedType == null || !beansContainer.containsKey(injectedType)) {
            throw new RuntimeException("Can't inject not a @Bean class " + f.getType());
        }
        f.setAccessible(true);
        try {
            f.set(o, beansContainer.get(injectedType));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Class getImpl(Class type, String name) {
        if (!type.isInterface()) {
            return type;
        }
        return beanInterfaces.get(type).stream()
                .filter(c -> c.getSimpleName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

}
