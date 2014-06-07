/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.codeferm.dbaccess.DbJdbcDs;
import com.codeferm.dbaccess.atomikos.BaseAtomikosTest;
import org.junit.Test;

/**
 * Test {@link com.codeferm.dbaccess.transaction.Transaction} annotation using
 * {@link com.codeferm.dbaccess.DbJdbcDs} implementation of
 * {@link com.codeferm.dbaccess.DbAccess}. Test records are removed from table.
 *
 * @see com.codeferm.dbaccess.transaction.TransactionTest
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DbJdbcDsTest extends BaseAtomikosTest {

    /**
     * Test of commit transaction using DbJdbcDs implementation.
     *
     * @throws Exception Possible exception.
     */
    @Test
    public void commit() throws Exception {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "xa.pool.size")) * LOOP_MULTIPLIER;
        log.info("commit DbJdbcDs prime");
        long startTime = System.currentTimeMillis();
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        TransactionTest test = TransactionFactory.createObject(
                TransactionTest.class, AtomikosTransModule.class);
        int id = test.commitRec(new DbJdbcDs(getDataSource()), getSqlMap().get(
                "insert.testtable"));
        test.verifyCommit(new DbJdbcDs(getDataSource()), getSqlMap().get(
                "select.testtable.by.id"), getSqlMap().get(
                        "delete.testtable.by.id"), id);
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms", elapsedTime));
        log.info("commit DbJdbcDs");
        startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            id = test.commitRec(new DbJdbcDs(getDataSource()), getSqlMap().get(
                    "insert.testtable"));
            test.verifyCommit(new DbJdbcDs(getDataSource()), getSqlMap().get(
                    "select.testtable.by.id"), getSqlMap().get(
                            "delete.testtable.by.id"), id);
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }

    /**
     * Test of rollback transaction using DbJdbcDs implementation.
     *
     * @throws Exception Possible exception.
     */
    @Test
    public void rollback() throws Exception {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "xa.pool.size")) * LOOP_MULTIPLIER;
        log.info("rollback DbJdbcDs prime");
        long startTime = System.currentTimeMillis();
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        TransactionTest test = TransactionFactory.createObject(
                TransactionTest.class, AtomikosTransModule.class);
        int id = 0;
        try {
            // This should cause rollback
            id = test.rollbackRec(new DbJdbcDs(getDataSource()), getSqlMap().
                    get("insert.testtable"));
        } catch (Throwable e) {
            // Verify rollback worked
            test.verifyRollback(new DbJdbcDs(getDataSource()), getSqlMap().get(
                    "select.testtable.by.id"), getSqlMap().get(
                            "delete.testtable.by.id"), id);
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms", elapsedTime));
        log.info("rollback DbJdbcDs");
        startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            try {
                // This should cause rollback
                id = test.rollbackRec(new DbJdbcDs(getDataSource()),
                        getSqlMap().
                        get("insert.testtable"));
            } catch (Throwable e) {
                // Verify rollback worked
                test.verifyRollback(new DbJdbcDs(getDataSource()), getSqlMap().
                        get(
                                "select.testtable.by.id"), getSqlMap().get(
                                "delete.testtable.by.id"), id);
            }
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format(
                "Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }
}
