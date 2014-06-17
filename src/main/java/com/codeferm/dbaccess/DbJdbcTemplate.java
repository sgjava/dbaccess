/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Spring JdbcTemplate extension of {@link com.codeferm.dbaccess.DbAccess}
 * abstract class.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbJdbcTemplate extends DbAccess {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
    private static final Logger log = LoggerFactory.getLogger(//NOPMD
            DbJdbcTemplate.class);
    //CHECKSTYLE:ON ConstantName
    /**
     * Implementation class.
     */
    private transient JdbcTemplate template = null;

    /**
     * Construct new {@code JdbcTemplate} with {@code DataSource}.
     *
     * @param dataSource Database data source.
     */
    public DbJdbcTemplate(final DataSource dataSource) {
        super();
        this.template = new JdbcTemplate(dataSource);
    }

    /**
     * Get {@code JdbcTemplate} object.
     *
     * @return JdbcTemplate template.
     */
    public final JdbcTemplate getTemplate() {
        return template;
    }

    /**
     * Set {@code JdbcTemplate} object.
     *
     * @param template JdbcTemplate template.
     */
    public final void setTemplate(final JdbcTemplate template) {
        this.template = template;
    }

    /**
     * Return query results as list of beans. {@code Connection} closed
     * automatically.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz {@code Class} to map results to.
     * @return {@code List} of {@code <T>} typed objects.
     */
    @Override
    public final <T> List<T> selectList(final String sql,
            final Object[] params, final Class clazz) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("selectBeanList: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        return template.query(sql, params,
                new BeanPropertyRowMapper(clazz));
    }

    /**
     * Return query results as list of Maps. {@code Connection} closed
     * automatically.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return {@code List} of Maps containing field name/value pair.
     */
    @Override
    public final List<Map<String, Object>> selectList(final String sql,
            final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("selectMapList: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        return template.queryForList(sql, params);
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with parameter
     * array. {@code Connection} closed automatically, so if many statements
     * need to be executed use batch update.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated.
     */
    @Override
    public final int update(final String sql, final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("update: sql=%s, params=%s", sql, Arrays.
                    asList(params)));
        }
        return template.update(sql, params);
    }

    /**
     * Executes the given INSERT statement with parameter array and returns auto
     * generate keys. JDBC driver needs to support RETURN_GENERATED_KEYS.
     * {@code Connection} closed automatically.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Field name/value pairs of keys.
     */
    @Override
    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification
            = "This mimics how Spring does it")
    public final Map<String, Object> updateReturnKeys(final String sql,
            final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("updateReturnKeys: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        // Holds keys after update
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        // Generic PreparedStatementCreator
        final PreparedStatementCreator psc = new PreparedStatementCreator() {

            @Override
            @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
                    justification = "This mimics how Spring does it")
            public final PreparedStatement createPreparedStatement(
                    final Connection con) throws SQLException {
                PreparedStatement preparedStatement = con.
                        prepareStatement(sql,
                                Statement.RETURN_GENERATED_KEYS);
                // Fill parameters
                int i = 1; //NOPMD
                for (Object o : params) {
                    preparedStatement.setObject(i++, o);
                }
                return preparedStatement;
            }
        };
        // Execute update
        template.update(psc, keyHolder);
        // Return keys
        return keyHolder.getKeys();
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with array of
     * parameter arrays.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated array.
     */
    @Override
    public final int[] batch(final String sql, final Object[][] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("batch: sql=%s", sql));
            for (Object[] param : params) {
                log.debug(
                        String.format("batch: params=%s",
                                Arrays.asList(param)));
            }
        }
        return template.batchUpdate(sql, Arrays.asList(params));
    }

    /**
     * Close connection quietly.
     */
    @Override
    public final void cleanUp() {
        // Nothing to do here since all methods clean up resources after each
        // operation.
    }
}
