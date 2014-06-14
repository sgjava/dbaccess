/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 24, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Runtime annotation for method level transaction.
 *
 * @see com.codeferm.dbaccess.transaction.AtomikosTransInterceptor
 * @see com.codeferm.dbaccess.transaction.AtomikosTransModule
 * @see com.codeferm.dbaccess.transaction.QueryRunnerTransInterceptor
 * @see com.codeferm.dbaccess.transaction.QueryRunnerTransModule
 * @see com.codeferm.dbaccess.transaction.JdbcTransInterceptor
 * @see com.codeferm.dbaccess.transaction.JdbcTransModule
 * @see com.codeferm.dbaccess.transaction.TransactionFactory
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transaction {
}
