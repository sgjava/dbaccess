/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dbcp;

import com.codeferm.dbaccess.DbJdbcConn;
import com.codeferm.dbaccess.DbAccess;
import java.sql.SQLException;
import org.junit.Test;

/**
 * Test {@link com.codeferm.dbaccess.DbJdbcConn} implementation of
 * {@link com.codeferm.dbaccess.DbAccess} class. Test records are automatically
 * removed from table.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DbJdbcConnTest extends DbcpTest {

    /**
     * Test named and positional parameter methods of class DbAccess.
     *
     * @throws SQLException Possible exception.
     */
    @Test
    public void dbAccess() throws SQLException {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("dbAccess DbJdbcConn prime");
        DbAccess db = new DbJdbcConn(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccess(db, 1);
        log.info("dbAccess DbJdbcConn");
        dbAccess(db, loops);
        db.cleanUp();
        log.info("dbAccessNamed DbJdbcConn prime");
        db = new DbJdbcConn(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccessNamed(db, 1);
        log.info("dbAccessNamed DbJdbcConn");
        dbAccessNamed(db, loops);
        db.cleanUp();
    }

    /**
     * Test of batch method, of class DbAccess.
     *
     * @throws SQLException Possible exception.
     */
    @Test
    public void batch() throws SQLException {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("batch DbJdbcConn prime");
        DbAccess db = new DbJdbcConn(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batch DbJdbcConn");
        batch(db, loops);
        db.cleanUp();
        log.info("batch DbJdbcConn prime");
        db = new DbJdbcConn(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batchNamed DbJdbcConn");
        batchNamed(db, loops);
        db.cleanUp();
    }

    /**
     * Test of batch method, of class DbAccess.
     *
     * @throws SQLException Possible exception.
     */
    @Test
    public void beanMap() throws SQLException {
        log.info("beanMap DbJdbcConn");
        DbAccess db = new DbJdbcConn(getDataSource());
        beanMap(db);
        db.cleanUp();
    }
}
