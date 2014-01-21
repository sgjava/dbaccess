/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dbcp;

import com.codeferm.dbaccess.DbPersist;
import com.codeferm.dbaccess.DbAccess;
import java.sql.SQLException;
import org.junit.Test;

/**
 * Test {@link com.codeferm.dbaccess.DbPersist} implementation of
 * {@link com.codeferm.dbaccess.DbAccess} class. Test records are automatically
 * removed from table.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DbPersistTest extends DbcpTest {

    /**
     * Test named and positional parameter methods of class DbAccess.
     *
     * @throws SQLException Possible exception.
     */
    @Test
    public void dbAccess() throws SQLException {
        final int loops = Integer.parseInt(getProperties().getProperty(
                "db.pool.size")) * LOOP_MULTIPLIER;
        log.info("dbAccess DbPersist prime");
        DbAccess db = new DbPersist(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccess(db, 1);
        log.info("dbAccess DbPersist");
        dbAccess(db, loops);
        db.cleanUp();
        log.info("dbAccessNamed DbPersist prime");
        db = new DbPersist(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        dbAccessNamed(db, 1);
        log.info("dbAccessNamed DbPersist");
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
        log.info("batch DbPersist prime");
        DbAccess db = new DbPersist(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batch DbPersist");
        batch(db, loops);
        db.cleanUp();
        log.info("batch DbPersist prime");
        db = new DbPersist(getDataSource());
        // Run one time to force any lazy loading, so elapse time is more
        // accurate.
        batch(db, 1);
        log.info("batchNamed DbPersist");
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
        log.info("beanMap DbPersist");
        DbAccess db = new DbPersist(getDataSource());
        beanMap(db);
        db.cleanUp();
    }
}
