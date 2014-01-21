/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dbcp;

import com.codeferm.dbaccess.DbJdbcDs;
import com.codeferm.dbaccess.DbAccess;
import java.sql.SQLException;
import org.junit.Test;

/**
 * Test {@link com.codeferm.dbaccess.DbJdbcDs} implementation of
 * {@link com.codeferm.dbaccess.DbAccess} class. Test records are automatically
 * removed from table.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DbJdbcDsTest extends DbcpTest {

    /**
     * Test named and positional parameter methods of class DbAccess.
     *
     * @throws SQLException Possible exception.
     */
    @Test
    public void dbAccess() throws SQLException {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("dbAccess DbJdbcDs prime");
        DbAccess db = new DbJdbcDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccess(db, 1);
        log.info("dbAccess DbJdbcDs");
        dbAccess(db, loops);
        db.cleanUp();
        log.info("dbAccessNamed DbJdbcDs prime");
        db = new DbJdbcDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccessNamed(db, 1);
        log.info("dbAccessNamed DbJdbcDs");
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
        log.info("batch DbJdbcDs prime");
        DbAccess db = new DbJdbcDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batch DbJdbcDs");
        batch(db, loops);
        db.cleanUp();
        log.info("batch DbJdbcDs prime");
        db = new DbJdbcDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batchNamed DbJdbcDs");
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
        log.info("beanMap DbJdbcDs");
        DbAccess db = new DbJdbcDs(getDataSource());
        beanMap(db);
        db.cleanUp();
    }
}
