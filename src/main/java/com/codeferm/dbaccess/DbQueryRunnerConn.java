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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DBUtils QueryRunner extension of {@link com.codeferm.dbaccess.DbAccess}
 * abstract class that uses a {@code Connection} instead of a
 * {@code DataSource}.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbQueryRunnerConn extends DbAccess {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
    private static final Logger log = LoggerFactory.getLogger(//NOPMD
            DbQueryRunnerConn.class);
    //CHECKSTYLE:ON ConstantName
    /**
     * Implementation class.
     */
    private transient QueryRunner template = null;
    /**
     * This allows the use of the same connection across multiple
     * {@code QueryRunner} calls. The calling code should manage the connection.
     * This is only a reference because {@code QueryRunner} methods that use a
     * {@code DataSource} close connection after each operation.
     */
    private transient Connection connection = null;

    /**
     * Construct new {@code QueryRunner} with {@code DataSource}.
     *
     * @param dataSource Database data source.
     * @throws SQLException Possible exception.
     */
    public DbQueryRunnerConn(final DataSource dataSource) throws SQLException {
        super();
        this.connection = dataSource.getConnection();
        this.template = new QueryRunner();
    }

    /**
     * Construct new {@code QueryRunner} and persist connection in template in
     * order to use across multiple calls.
     *
     * @param connection Database connection.
     */
    public DbQueryRunnerConn(final Connection connection) {
        super();
        this.connection = connection;
        this.template = new QueryRunner();
    }

    /**
     * Get {@code Connection}.
     *
     * @return Connection Database connection.
     */
    public final Connection getConnection() {
        return connection;
    }

    /**
     * Set {@code Connection}.
     *
     * @param connection Database connection.
     */
    public final void setConnection(final Connection connection) {
        this.connection = connection;
    }

    /**
     * Get {@code QueryRunner} object.
     *
     * @return QueryRunner template.
     */
    public final QueryRunner getTemplate() {
        return template;
    }

    /**
     * Set {@code QueryRunner} object.
     *
     * @param template QueryRunner template.
     */
    public final void setTemplate(final QueryRunner template) {
        this.template = template;
    }

    /**
     * Return query results as list of beans. DBUtils doesn't handle underscore
     * to camelCase conversions for you. A custom DbBeanProcessor is implemented
     * to handle underscores properly. Connection is not closed.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz Class to map results to.
     * @return {@code List} of {@code <T>} typed objects.
     */
    @Override
    @SuppressFBWarnings(value
            = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification
            = "SQL libraries are allowed to accept SQL as parameter")
    public final <T> List<T> selectList(final String sql,
            final Object[] params, final Class clazz) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("selectBeanList: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        List<T> list = null;
        try {
            list = (List<T>) template.query(connection, sql,
                    new BeanListHandler(clazz,
                            new BasicRowProcessor(new DbBeanProcessor())),
                    params);
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "selectBeanList: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        }
        return list;
    }

    /**
     * Return query results as list of Maps. {@code Connection} is not closed.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return {@code List} of Maps containing field name/value pair.
     */
    @Override
    @SuppressFBWarnings(value
            = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification
            = "SQL libraries are allowed to accept SQL as parameter")
    public final List<Map<String, Object>> selectList(final String sql,
            final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("selectMapList: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        List<Map<String, Object>> list = null;
        try {
            list = template.query(connection, sql, new MapListHandler(),
                    params);
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "selectMapList: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        }
        return list;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with parameter
     * array. {@code Connection} is not closed.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated.
     */
    @Override
    @SuppressFBWarnings(value
            = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification
            = "SQL libraries are allowed to accept SQL as parameter")
    public final int update(final String sql, final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("update: sql=%s, params=%s", sql, Arrays.
                    asList(params)));
        }
        int rows = -1;
        try {
            rows = template.update(connection, sql, params);
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "update: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        }
        return rows;
    }

    /**
     * Executes the given INSERT statement with parameter array and returns auto
     * generate key. JDBC driver needs to support RETURN_GENERATED_KEYS.
     * Connection is not closed.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Field name/value pairs of keys.
     */
    @Override
    @SuppressFBWarnings(value
            = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification
            = "SQL libraries are allowed to accept SQL as parameter")
    public final Map<String, Object> updateReturnKeys(final String sql,
            final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("updateReturnKeys: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null; //NOPMD, DbUtils uses closeQuietly
        Map<String, Object> keys = null;
        try {
            // Connection from template
            preparedStatement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            // Fill parameters
            template.fillStatement(preparedStatement, params);
            preparedStatement.executeUpdate();
            // Get keys as ResultSet
            resultSet = preparedStatement.getGeneratedKeys();
            // Get generated keys as Object array
            keys = new MapHandler().handle(resultSet);
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "updateReturnKeys: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);
        }
        return keys;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with array of
     * parameter arrays. {@code Connection} is not closed.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated array.
     */
    @Override
    @SuppressFBWarnings(value
            = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
            justification
            = "SQL libraries are allowed to accept SQL as parameter")
    public final int[] batch(final String sql, final Object[][] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("batch: sql=%s", sql));
            for (Object[] param : params) {
                log.debug(
                        String.format("batch: params=%s",
                                Arrays.asList(param)));
            }
        }
        int[] rows = null;
        try {
            rows = template.batch(connection, sql, params);
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "batch: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        }
        return rows;
    }

    /**
     * Close connection quietly.
     */
    @Override
    public final void cleanUp() {
        DbUtils.closeQuietly(connection);
    }
}
