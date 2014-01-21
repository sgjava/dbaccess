/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dbcp;

import com.codeferm.dbaccess.DbQueryRunnerDs;
import com.codeferm.dbaccess.DbAccess;
import java.sql.SQLException;
import org.junit.Test;

/**
 * Test {@link com.codeferm.dbaccess.DbQueryRunnerDs} implementation of
 * {@link com.codeferm.dbaccess.DbAccess} class. Test records are automatically
 * removed from table.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DbQueryRunnerDsTest extends DbcpTest {

    /**
     * Test named and positional parameter methods of class DbAccess.
     *
     * @throws SQLException Possible exception.
     */
    @Test
    public void dbAccess() throws SQLException {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("dbAccess DbQueryRunnerDs prime");
        DbAccess db = new DbQueryRunnerDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccess(db, 1);
        log.info("dbAccess DbQueryRunnerDs");
        dbAccess(db, loops);
        db.cleanUp();
        log.info("dbAccessNamed DbQueryRunnerDs prime");
        db = new DbQueryRunnerDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccessNamed(db, 1);
        log.info("dbAccessNamed DbQueryRunnerDs");
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
        log.info("batch DbQueryRunnerDs prime");
        DbAccess db = new DbQueryRunnerDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batch DbQueryRunnerDs");
        batch(db, loops);
        db.cleanUp();
        log.info("batch DbQueryRunnerDs prime");
        db = new DbQueryRunnerDs(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batchNamed DbQueryRunnerDs");
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
        log.info("beanMap DbQueryRunnerDs");
        DbAccess db = new DbQueryRunnerDs(getDataSource());
        beanMap(db);
        db.cleanUp();
    }
}
