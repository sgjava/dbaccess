/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.atomikos;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.codeferm.dbaccess.BaseTest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base test class with common methods used by all other Atomikos based tests.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class BaseAtomikosTest extends BaseTest {

    /**
     * Database DataSource.
     */
    private static DataSource dataSource;

    /**
     * Get BasicDataSource.
     *
     * @return DataSource Atomikos DataSource.
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
        // Create AtomikosXADataSourceBean
        final AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("BaseAtomikosTest");
        ds.setXaDataSourceClassName(
                getProperties().getProperty("xa.driver"));
        Properties p = new Properties();
        p.setProperty("user", getProperties().getProperty("xa.user"));
        p.setProperty("password", getProperties().getProperty("xa.password"));
        p.setProperty("serverName", getProperties().getProperty(
                "xa.server.name"));
        p.setProperty("portNumber", getProperties().getProperty(
                "xa.port.number"));
        p.setProperty("databaseName", getProperties().getProperty(
                "xa.database.name"));
        ds.setXaProperties(p);
        ds.setTestQuery(getSqlMap().get("select.test"));
        ds.setPoolSize(Integer.parseInt(getProperties().getProperty(
                "xa.pool.size")));
        dataSource = ds;
    }

    /**
     * Close {@code DataSource}.
     *
     * @throws SQLException Possible exception.
     */
    @AfterClass
    public static void tearDownClass() throws SQLException {
        ((AtomikosDataSourceBean) dataSource).close();
    }
}
