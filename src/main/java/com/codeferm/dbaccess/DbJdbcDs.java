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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home grown extension of {@link com.codeferm.dbaccess.DbAccess} abstract class
 * that uses JDBC API directly. {@code DataSource} based implementation.
 *
 * @see com.codeferm.dbaccess.DbAccess
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbJdbcDs extends DbBeanMapper {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
    private static final Logger log = LoggerFactory.getLogger(DbJdbcDs.class); //NOPMD
    //CHECKSTYLE:ON ConstantName
    /**
     * DataSource reference.
     */
    private transient DataSource dataSource = null;

    /**
     * Construct new {@code DbJdbcDs} with {@code DataSource}.
     *
     * @param dataSource Database data source
     * @throws SQLException Possible exception
     */
    public DbJdbcDs(final DataSource dataSource) throws SQLException {
        super();
        this.dataSource = dataSource;
    }

    /**
     * Get {@code DataSource}.
     *
     * @return DataSource
     */
    public final DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Set DataSource.
     *
     * @param dataSource {@code DataSource} to set
     */
    public final void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Return query results as list of beans. {@code Connection} closed
     * automatically.
     *
     * @param <T> Type of object that the handler returns
     * @param sql SQL statement to execute
     * @param params Initialize the PreparedStatement's IN parameters
     * @param clazz Class to map results to
     * @return {@code List} of {@code <T>} typed objects
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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> list = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            // Fill parameters
            int i = 1; //NOPMD
            for (Object o : params) {
                preparedStatement.setObject(i++, o);
            }
            resultSet = preparedStatement.executeQuery();
            list = createObjects(resultSet, clazz);
        } catch (Exception e) {
            throw new DbAccessException(String.format(
                    "selectBeanList: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    /**
     * Return query results as list of Maps. {@code Connection} closed
     * automatically.
     *
     * @param sql SQL statement to execute
     * @param params Initialize the PreparedStatement's IN parameters
     * @return {@code List} of Maps containing field name/value pair
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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> list = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            // Fill parameters
            int i = 1; //NOPMD
            for (Object o : params) {
                preparedStatement.setObject(i++, o);
            }
            resultSet = preparedStatement.executeQuery();
            final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            final int numberOfColumns = resultSetMetaData.getColumnCount();
            list = new ArrayList<Map<String, Object>>();
            final Map map = new HashMap();
            while (resultSet.next()) {
                for (i = 1; i <= numberOfColumns; i++) {
                    map.put(resultSetMetaData.getColumnName(i), resultSet.
                            getObject(i));
                }
                list.add(map);
            }
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "selectMapList: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with parameter
     * array. {@code Connection} closed automatically.
     *
     * @param sql SQL statement to execute
     * @param params Initialize the PreparedStatement's IN parameters
     * @return Number of rows updated
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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rows = -1;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            // Fill parameters
            int i = 1; //NOPMD OK to use short name for loop iteration variable
            for (Object o : params) {
                preparedStatement.setObject(i++, o);
            }
            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "update: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return rows;
    }

    /**
     * Executes the given INSERT statement with parameter array and returns auto
     * generate key. JDBC driver needs to support RETURN_GENERATED_KEYS.
     * {@code Connection} closed automatically.
     *
     * @param sql SQL statement to execute
     * @param params Initialize the PreparedStatement's IN parameters
     * @return Field name/value pairs of keys
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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<String, Object> keys = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            // Fill parameters
            int i = 1; //NOPMD
            for (Object o : params) {
                preparedStatement.setObject(i++, o);
            }
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            final int numberOfColumns = resultSetMetaData.getColumnCount();
            // Prime the pump
            if (resultSet.next()) {
                keys = new HashMap<String, Object>();
                for (i = 1; i <= numberOfColumns; i++) {
                    keys.put(resultSetMetaData.getColumnName(i),
                            resultSet.getObject(i));
                }
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
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return keys;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with array of
     * parameter arrays. {@code Connection} closed automatically.
     *
     * @param sql SQL statement to execute
     * @param params Initialize the PreparedStatement's IN parameters
     * @return Number of rows updated array
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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int[] rows = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            // Add parameter arrays to batch
            for (Object[] param : params) {
                // Fill parameters
                int i = 1; //NOPMD
                for (Object o : param) {
                    preparedStatement.setObject(i++, o);
                }
                // Add to batch
                preparedStatement.addBatch();
            }
            rows = preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DbAccessException(String.format(
                    "batch: sql=%s, params=%s", sql, Arrays.asList(
                            params)), e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return rows;
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
