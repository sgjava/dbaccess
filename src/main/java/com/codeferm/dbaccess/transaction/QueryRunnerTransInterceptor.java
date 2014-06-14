/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 24, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.codeferm.dbaccess.DbQueryRunnerConn;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercept methods annotated with
 * {@link com.codeferm.dbaccess.transaction.Transaction} and commit on success
 * or rollback if {@link java.lang.Exception} is thrown. The first method
 * parameter must be an instance of {@link com.codeferm.dbaccess.DbAccess} in
 * order for this class to access the connection prior to commit or rollback
 * operation as required by JDBC style transactions.
 * <p>
 * <p>
 * Currently this interceptor supports only
 * {@link com.codeferm.dbaccess.DbQueryRunnerConn}. Other {@code Connection}
 * based implementations of {@link com.codeferm.dbaccess.DbAccess} would work as
 * long as the getConnection method is implemented.
 * <p>
 * <p>
 * Limitations
 * <p>
 * Behind the scenes, method interception is implemented by generating bytecode
 * at runtime. Guice dynamically creates a subclass that applies interceptors by
 * overriding methods. If you are on a platform that doesn't support bytecode
 * generation (such as Android), you should use Guice without AOP support.
 * <p>
 * This approach imposes limits on what classes and methods can be intercepted:
 * <p>
 * <p>
 * Classes must be public or package-private. Classes must be non-final Methods
 * must be public, package-private or protected Methods must be non-final
 * Instances must be created by Guice by an @Inject-annotated or no-argument
 * constructor
 * <p>
 * <p>
 * It is not possible to use method interception on instances that aren't
 * constructed by Guice.
 *
 * @see com.codeferm.dbaccess.transaction.Transaction
 * @see com.codeferm.dbaccess.transaction.QueryRunnerTransModule
 * @see com.codeferm.dbaccess.transaction.TransactionFactory
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class QueryRunnerTransInterceptor implements MethodInterceptor {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
    private static final Logger log = LoggerFactory.getLogger( //NOPMD
            QueryRunnerTransInterceptor.class);
    //CHECKSTYLE:ON ConstantName

    /**
     * Invoke method wrapped in a transaction.
     *
     * @param invocation Method invocation.
     * @return Invoked Object.
     * @throws Throwable Possible exception.
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        final Transaction annotation = method.getAnnotation(Transaction.class);
        Object object = null;
        // Make sure interceptor was called for a @Transaction method
        if (annotation == null) {
            // Not a Transaction annotated method, so proceed without change
            object = invocation.proceed();

        } else {
            // First parameter must be DbAccess object
            if (invocation.getArguments().length == 0
                    || !(invocation.getArguments()[0] instanceof DbQueryRunnerConn)) {
                throw new IllegalArgumentException(String.format(
                        "First parameter must be a DbQueryRunnerConn instance, not %s",
                        method.getName()));
            }
            // Get DbQueryRunnerConn object
            final DbQueryRunnerConn dbAccess = (DbQueryRunnerConn) invocation.
                    getArguments()[0];
            // Begin transaction
            dbAccess.getConnection().setAutoCommit(false);
            try {
                // Proceed with the original method's invocation
                object = invocation.proceed();
                // Commit if successful
                dbAccess.getConnection().commit();
                dbAccess.getConnection().setAutoCommit(true);
                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                            "Committed transaction for method %s",
                            invocation.getMethod().getName()));
                }
            } catch (Exception e) {
                // Rollback on Exception
                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                            "Rollback transaction for method %s",
                            invocation.getMethod().getName()));
                }
                // Rollback on error
                dbAccess.getConnection().rollback();
                dbAccess.getConnection().setAutoCommit(true);
                throw e;
            }
        }
        return object;
    }
}
