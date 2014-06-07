/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.persist.Persist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persist extension of {@link com.codeferm.dbaccess.DbAccess} abstract class.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbPersist extends DbAccess {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF This is not a constant, so naming convenetion is correct
    private static final Logger log = LoggerFactory.getLogger(DbPersist.class);  //NOPMD
    //CHECKSTYLE:ON
    /**
     * Implementation class.
     */
    private transient Persist template = null;
    /**
     * This is the connection used to initialize Persist and as a reference to
     * close connection.
     */
    private transient Connection connection = null;

    /**
     * Construct new {@code Persist} object with {@code DataSource}.
     *
     * @param dataSource Database data source.
     * @throws SQLException Possible exception.
     */
    public DbPersist(final DataSource dataSource) throws SQLException {
        super();
        connection = dataSource.getConnection();
        template = new Persist(connection);
        // Automatically close prepared statements after reads
        template.setClosePreparedStatementsAfterRead(true);
    }

    /**
     * Construct new {@code Persist} object with {@code Connection}.
     *
     * @param connection Database connection.
     */
    public DbPersist(final Connection connection) {
        super();
        this.connection = connection;
        template = new Persist(connection);
        // Automatically close prepared statements after reads
        template.setClosePreparedStatementsAfterRead(true);
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
     * @param connection Connection Database connection.
     */
    public final void setConnection(final Connection connection) {
        this.connection = connection;
    }

    /**
     * Get {@code Persist} object.
     *
     * @return Persist template.
     */
    public final Persist getTemplate() {
        return template;
    }

    /**
     * Set {@code Persist} object.
     *
     * @param template Persist template.
     */
    public final void setTemplate(final Persist template) {
        this.template = template;
    }

    /**
     * Return query results as list of beans. {@code Connection} is not closed.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz Class to map results to.
     * @return {@code List} of {@code <T>} typed objects.
     */
    @Override
    public final <T> List<T> selectList(final String sql,
            final Object[] params, final Class clazz) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("selectBeanList: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        return (List<T>) template.readList(clazz, sql, params);
    }

    /**
     * Return query results as list of Maps. {@code Connection} is not closed.
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
        return template.readMapList(sql, params);
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
    public final int update(final String sql, final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("update: sql=%s, params=%s", sql, Arrays.
                    asList(params)));
        }
        return template.executeUpdate(sql, params);
    }

    /**
     * Executes the given INSERT statement with parameter array and returns auto
     * generate key. JDBC driver needs to support RETURN_GENERATED_KEYS.
     * {@code Connection} is not closed.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Field name/value pairs of keys.
     */
    @Override
    public final Map<String, Object> updateReturnKeys(final String sql,
            final Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("updateReturnKeys: sql=%s, params=%s", sql,
                    Arrays.asList(params)));
        }
        Map<String, Object> keys = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            // Fill parameters
            Persist.setParameters(preparedStatement, params);
            preparedStatement.executeUpdate();
            // Get keys as ResultSet
            resultSet = preparedStatement.getGeneratedKeys();
            // Prime the pump
            if (resultSet.next()) {
                // Get generated keys as Map of field name/value
                keys = Persist.loadMap(resultSet);
            }
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "updateReturnKeys: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            template.closePreparedStatement(preparedStatement);
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
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            // Add parameter arrays to batch
            for (Object[] param : params) {
                // Fill parameters
                Persist.setParameters(preparedStatement, param);
                // Add to batch
                preparedStatement.addBatch();
            }
            rows = preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "batch: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            template.closePreparedStatement(preparedStatement);
        }
        return rows;
    }

    /**
     * Close connection quietly.
     */
    @Override
    public final void cleanUp() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
}
