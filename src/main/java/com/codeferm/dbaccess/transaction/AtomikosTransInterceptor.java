/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 24, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.codeferm.dbaccess.DbAccess;
import java.lang.reflect.Method;
import javax.transaction.UserTransaction;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercept methods annotated with
 * {@link com.codeferm.dbaccess.transaction.Transaction} and commit on success
 * or rollback if {@link java.lang.Exception} is thrown. The first method
 * parameter must be an instance of {@link com.codeferm.dbaccess.DbAccess} in
 * order for this class to close the connection prior to commit or rollback
 * operation as required by Atomikos.
 * <p>
 * <p>
 * Currently this class supports Geronimo JTA for
 * {@link javax.transaction.UserTransaction} and Atomikos
 * {@code UserTransactionImp} for the implementation. Other JTA implementations
 * could be leveraged as well.
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
 * @see com.codeferm.dbaccess.transaction.AtomikosTransModule
 * @see com.codeferm.dbaccess.transaction.TransactionFactory
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class AtomikosTransInterceptor implements MethodInterceptor {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
    private static final Logger log = LoggerFactory.getLogger(//NOPMD
            AtomikosTransInterceptor.class);
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
                    || !(invocation.getArguments()[0] instanceof DbAccess)) {
                throw new IllegalArgumentException(String.format(
                        "First parameter must be a DbAccess instance, not %s",
                        method.getName()));
            }
            // Get DbAccess object
            final DbAccess dbAccess = (DbAccess) invocation.getArguments()[0];
            final UserTransaction userTransaction = new UserTransactionImp();
            // Begin transaction
            userTransaction.begin();
            try {
                // Proceed with the original method's invocation
                object = invocation.proceed();
                // Close connection
                dbAccess.cleanUp();
                // Commit if successful
                userTransaction.commit();
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
                // Close connection
                dbAccess.cleanUp();
                // Rollback on error
                userTransaction.rollback();
                throw e;
            }
        }
        return object;
    }
}
