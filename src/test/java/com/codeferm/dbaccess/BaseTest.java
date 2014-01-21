/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.dbutils.QueryLoader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertNotNull;

/**
 * Base test class with common methods used by all other tests. Primarily those
 * are externalizing SQL statements and application properties.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class BaseTest {

    /**
     * This would usually be private static final, but I want the extended
     * class name to display for tests.
     */
    //CHECKSTYLE:OFF Read comment above
    protected final Logger log = LoggerFactory.getLogger(getClass());
    //CHECKSTYLE:ON
    /**
     * Used for all tests to multiply loops by connection pool size.
     */
    public static final int LOOP_MULTIPLIER = 10;
    /**
     * Application level properties.
     */
    private static Properties properties;
    /**
     * Global query map.
     */
    private static Map<String, String> sqlMap;

    /**
     * Get properties.
     *
     * @return Properties App properties.
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * Get SQL statement Map.
     *
     * @return map of statements.
     */
    public static Map<String, String> getSqlMap() {
        return sqlMap;
    }

    /**
     * Initialize class.
     *
     * @throws IOException Possible exception.
     */
    @BeforeClass
    public static void setUpClass() throws IOException {
        // Config path for externalized files
        final String configPath = System.getProperty("user.home")
                + "/config/dbaccess/";
        // Set log4j.properties location
        File file = new File(configPath + "log4j.properties");
        System.setProperty("log4j.configuration", "file:" + file);
        properties = new Properties();
        // Get external application properties
        properties.load(new FileInputStream(configPath + "app.properties"));
        // Load SQL statements from classpath
        sqlMap = QueryLoader.instance().load("/sql.properties");
    }

    /**
     * Test that properties is not null and has specific value.
     */
    @Test
    public final void testProperties() {
        log.info("testProperties");
        log.info(String.format("user.dir = %s", System.getProperty("user.dir")));
        assertNotNull("Properties should not be ", properties);
        final String property = properties.getProperty("db.driver");
        assertNotNull("Property should not be null", property);
        log.debug("Property = " + property);
    }

    /**
     * Test that SQL {@code Map} is not null and list contents.
     */
    @Test
    public final void testSqlMap() {
        log.info("testSqlMap");
        assertNotNull("Properties should not be ", sqlMap);
        for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
            log.debug(entry.getKey() + ": " + entry.getValue());
        }
    }
}
