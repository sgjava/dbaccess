/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on December 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.codeferm.dbaccess.DbAccess;
import com.codeferm.dbaccess.DbJdbcConn;
import com.codeferm.dbaccess.dbcp.BaseDbcpTest;
import org.junit.Test;

/**
 * Test {@link com.codeferm.dbaccess.transaction.Transaction} annotation using
 * {@link com.codeferm.dbaccess.DbJdbcConn} implementation of
 * {@link com.codeferm.dbaccess.DbAccess}. Test records are removed from table.
 *
 * @see com.codeferm.dbaccess.transaction.TransactionTest
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DbJdbcConnTest2 extends BaseDbcpTest {

    /**
     * Test of commit transaction using DbJdbcConn implementation.
     *
     * @throws Exception Possible exception.
     */
    @Test
    public void commit() throws Exception {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("commit DbJdbcConn prime");
        long startTime = System.currentTimeMillis();
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        TransactionTest test = TransactionFactory.createObject(
                TransactionTest.class, JdbcTransModule.class);
        DbAccess db = new DbJdbcConn(getDataSource());
        int id = test.commitRec(db, getSqlMap().get("insert.testtable"));
        test.verifyCommit(db, getSqlMap().get("select.testtable.by.id"),
                getSqlMap().get("delete.testtable.by.id"), id);
        // Close connection
        db.cleanUp();
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms", elapsedTime));
        log.info("commit DbJdbcConn");
        db = new DbJdbcConn(getDataSource());
        startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            id = test.commitRec(db, getSqlMap().get("insert.testtable"));
            test.verifyCommit(db, getSqlMap().get("select.testtable.by.id"),
                    getSqlMap().get("delete.testtable.by.id"), id);
        }
        // Close connection
        db.cleanUp();
        elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }

    /**
     * Test of rollback transaction using DbJdbcConn implementation.
     *
     * @throws Exception Possible exception.
     */
    @Test
    public void rollback() throws Exception {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("rollback DbJdbcConn prime");
        long startTime = System.currentTimeMillis();
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        TransactionTest test = TransactionFactory.createObject(
                TransactionTest.class, JdbcTransModule.class);
        DbAccess db = new DbJdbcConn(getDataSource());
        int id = 0;
        try {
            // This should cause rollback
            id = test.rollbackRec(db, getSqlMap().get("insert.testtable"));
        } catch (Throwable e) {
            // Verify rollback worked
            test.verifyRollback(db, getSqlMap().get("select.testtable.by.id"),
                    getSqlMap().get("delete.testtable.by.id"), id);
        }
        db.cleanUp();
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms", elapsedTime));
        log.info("rollback DbJdbcConn");
        db = new DbJdbcConn(getDataSource());
        startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            try {
                // This should cause rollback
                id = test.rollbackRec(db, getSqlMap().get("insert.testtable"));
            } catch (Throwable e) {
                // Verify rollback worked
                test.verifyRollback(db,
                        getSqlMap().get("select.testtable.by.id"), getSqlMap().
                        get("delete.testtable.by.id"), id);
            }
        }
        db.cleanUp();
        elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format(
                "Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }
}
