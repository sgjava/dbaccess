/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 24, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * Binds {@link com.codeferm.dbaccess.transaction.AtomikosTransInterceptor} in the
 * Guice module for methods annotated with
 * {@link com.codeferm.dbaccess.transaction.Transaction}.
 *
 * @see com.codeferm.dbaccess.transaction.Transaction
 * @see com.codeferm.dbaccess.transaction.JdbcTransInterceptor
 * @see com.codeferm.dbaccess.transaction.TransactionFactory
 * 
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class JdbcTransModule extends AbstractModule {

    /**
     * Inject transaction interceptor annotated with Transaction.
     */
    @Override
    protected void configure() {
        final JdbcTransInterceptor interceptor = new JdbcTransInterceptor();
        // Request injection of JdbcTransInterceptor
        requestInjection(interceptor);
        // Match all methods of class with @Transaction annotation
        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(Transaction.class), interceptor);
    }
}
