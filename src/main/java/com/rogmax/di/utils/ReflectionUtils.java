/*
 * Copyright 2016 the original author or authors.
 * Rogmax Digital, rogmax.com
 */
package com.rogmax.di.utils;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Roman Golovakha
 *         Date: 12.11.2016.
 */
public class ReflectionUtils {

    public static List<Class> getClassesAnnotatedWith(String path, Class annotation) {
        List<String> classesNames = new FastClasspathScanner(path).scan().getNamesOfClassesWithAnnotation(annotation);
        return getClasses(classesNames);
    }

    public static List<Class> getClasses(List<String> names) {
        return names.stream().map(ReflectionUtils::getClass).collect(Collectors.toList());
    }

    public static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List createInstances(List<Class> classes) {
        return classes.stream().map(ReflectionUtils::createInstance).collect(Collectors.toList());
    }

    public static Object createInstance(Class beanType) {
        if (beanType.isInterface()) {
            throw new RuntimeException("Can't create an instance of " + beanType);
        }
        try {
            return beanType.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<Class, List<Class>> getInterfaces(List<Class> classes) {
        Map<Class, List<Class>> interfaces = new HashMap<>();
        classes.stream().forEach(c->Arrays.stream(c.getInterfaces()).forEach(i->putIfAbsent(c, i, interfaces)));
        return interfaces;
    }

    private static void putIfAbsent(Class type, Class classInterface, Map<Class, List<Class>> interfaces) {
        if (!interfaces.containsKey(classInterface)) {
            interfaces.put(classInterface, new ArrayList<>());
        }
        interfaces.get(classInterface).add(type);
    }


    public static void invokeMethodAnnotatedWith(Object o, Class annotation) {
        Arrays.stream(o.getClass().getMethods()).filter(m -> m.isAnnotationPresent(annotation)).forEach(m -> invoke(o, m));
    }

    public static void invoke(Object o, Method m) {
        try {
            m.invoke(o);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
