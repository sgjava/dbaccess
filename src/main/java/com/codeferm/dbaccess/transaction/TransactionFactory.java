/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.codeferm.dbaccess.DbAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple factory class that returns and Object with methods wrapped in a
 * transaction if the method is annotated with
 * {@link com.codeferm.dbaccess.transaction.Transaction}. The method interceptor
 * {@link com.codeferm.dbaccess.transaction.AtomikosTransInterceptor},
 * {@link com.codeferm.dbaccess.transaction.QueryRunnerTransInterceptor} or
 * {@link com.codeferm.dbaccess.transaction.JdbcTransInterceptor} is binded
 * with Guice using {@link com.codeferm.dbaccess.transaction.AtomikosTransModule}.
 *
 * @see com.codeferm.dbaccess.transaction.Transaction
 * @see com.codeferm.dbaccess.transaction.AtomikosTransInterceptor
 * @see com.codeferm.dbaccess.transaction.AtomikosTransModule
 * 
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TransactionFactory {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF This is not a constant, so naming convenetion is correct
    private static final Logger log = LoggerFactory.getLogger( //NOPMD
            TransactionFactory.class);
    //CHECKSTYLE:ON

    /**
     * Suppress default constructor for non-instantiability.
     */
    private TransactionFactory() {
        throw new AssertionError();
    }

    /**
     * Create object of type T wrapped in transaction.
     *
     * @param <T> Return type.
     * @param clazz Type of object to create.
     * @param transModule Transaction module must extend AbstractModule.
     * @return Wrapped object.
     */
    public static <T> T createObject(final Class<T> clazz,
            final Class<? extends AbstractModule> transModule) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Creating transactional object for %s",
                    clazz.getName()));
        }
        T object = null;
        try {
            object = Guice.createInjector(transModule.newInstance()).getInstance(
                    clazz);
        } catch (InstantiationException e) {
            throw new DbAccessException(e);
        } catch (IllegalAccessException e) {
            throw new DbAccessException(e);
        }
        return object;
    }
}
