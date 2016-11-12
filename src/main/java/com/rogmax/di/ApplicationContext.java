/*
 * Copyright 2016 the original author or authors.
 * Rogmax Digital, rogmax.com
 */
package com.rogmax.di;

/**
 * @author Roman Golovakha
 *         Date: 12.11.2016.
 */
public interface ApplicationContext {

    <T> T getBean(Class<T> type);

}
