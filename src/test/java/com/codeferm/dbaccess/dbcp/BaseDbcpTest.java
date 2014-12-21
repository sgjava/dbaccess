/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dbcp;

import com.codeferm.dbaccess.BaseTest;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Base test class with common methods used by all other DBCP based tests.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class BaseDbcpTest extends BaseTest {

    /**
     * Database {@code DataSource}.
     */
    private static DataSource dataSource;

    /**
     * Get {@code BasicDataSource}.
     *
     * @return DataSource DBCP DataSource.
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Initialize class.
     *
     * @throws IOException Possible exception.
     */
    @BeforeClass
    public static void setUpClass() throws IOException {
        // Call super setUpClass
        BaseTest.setUpClass();
        // Create DBCP DataSource
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(getProperties().getProperty("db.driver"));
        ds.setUsername(getProperties().getProperty("db.user"));
        ds.setPassword(getProperties().getProperty("db.password"));
        ds.setUrl(getProperties().getProperty("db.url"));
        ds.setMaxTotal(Integer.parseInt(getProperties().getProperty(
                "db.pool.size")));
        dataSource = ds;
    }

    /**
     * Close {@code DataSource}.
     *
     * @throws SQLException Possible exception.
     */
    @AfterClass
    public static void tearDownClass() throws SQLException {
        ((BasicDataSource) dataSource).close();
    }

    /**
     * Do nothing for now.
     */
    @Before
    public void setUp() {
    }

    /**
     * Do nothing for now.
     */
    @After
    public void tearDown() {
    }
}
