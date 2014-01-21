/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;

/**
 * Abstract class to simplify JDBC database access across all JDBC wrapper
 * implementations. The goal is to have a common database API without coding to
 * a specific implementation such as Apache DBUtils or Spring JdbcTemplate. This
 * allows you to swap out the implementation as needed without impacting
 * dependant code. All implementations support named parameters as well.
 * Transactional support can be added using the
 * {@link com.codeferm.dbaccess.transaction.Transaction} annotation.
 *
 * Implementations should automatically handle database field names with
 * underscores and map them to camelCase bean fields.
 *
 * @see com.codeferm.dbaccess.DbBeanMapper
 * @see com.codeferm.dbaccess.DbJdbcConn
 * @see com.codeferm.dbaccess.DbJdbcDs
 * @see com.codeferm.dbaccess.DbJdbcTemplate
 * @see com.codeferm.dbaccess.DbPersist
 * @see com.codeferm.dbaccess.DbQueryRunnerConn
 * @see com.codeferm.dbaccess.DbQueryRunnerDs
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DbAccess {  //NOPMD, this is an API , so it's OK to have "too many" methods

    /**
     * Return SQL with ? type parameter markers in place of named parameters.
     *
     * @param sql SQL with named parameters.
     * @return SQL with ? markers.
     */
    public final String namedParamsToMarkers(final String sql) {
        return NamedParameterUtils.parseSqlStatementIntoString(sql);
    }

    /**
     * Return SQL with ? type parameter markers in place of named parameters.
     *
     * @param sql SQL with named parameters.
     * @param params Named parameters.
     * @return SQL with ? markers.
     */
    public final Object[] paramMapToArray(final String sql,
            final Map<String, Object> params) {
        // Params are in the proper order for the SQL statement
        return NamedParameterUtils.buildValueArray(sql, params);
    }

    /**
     * Return {@code Object[][]} from named parameters in
     * {@code List<Map<String, Object>>}. This is handy for batch
     * operations.
     *
     * @param sql SQL to map list of named parameters to ? markers.
     * @param params List of parameter maps.
     * @return Array of Object arrays.
     */
    public final Object[][] mapListToArray(final String sql,
            final List<Map<String, Object>> params) {
        final Object[][] batchParams = new Object[params.size()][];
        int i = 0; //NOPMD
        // Convert list of List<Map<String, Object>> to Object[][]
        for (Map<String, Object> param : params) {
            // Make sure parameters are in correct sequence
            batchParams[i++] = paramMapToArray(sql, param);
        }
        return batchParams;
    }

    /**
     * Return parameterized query results as list of beans.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz Class to map results to.
     * @return List of <T> typed objects.
     */
    public abstract <T> List<T> selectList(final String sql,
            final Object[] params, final Class clazz);

    /**
     * Return query results as list of beans.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param clazz Class to map results to.
     * @return List of <T> typed objects.
     */
    public final <T> List<T> selectList(final String sql, final Class clazz) {
        return selectList(sql, new Object[]{}, clazz);
    }

    /**
     * Return parameterized query results as a single bean.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz Class to map results to.
     * @return <T> typed object.
     */
    public final <T> T select(final String sql, final Object[] params,
            final Class clazz) {
        final List<T> list = selectList(sql, params, clazz);
        T object = null;
        if (!list.isEmpty()) {
            // Get first item in List
            object = list.get(0);
        }
        return object;
    }

    /**
     * Return query results as a single bean.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param clazz Class to map results to.
     * @return <T> typed object.
     */
    public final <T> T select(final String sql, final Class clazz) {
        final List<T> list = selectList(sql, new Object[]{}, clazz);
        T object = null;
        if (!list.isEmpty()) {
            // Get first item in List
            object = list.get(0);
        }
        return object;
    }

    /**
     * Return parameterized query results as list of beans. Named parameters
     * are converted to parameter markers.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz Class to map results to.
     * @return List of <T> typed objects.
     */
    public final <T> List<T> selectList(final String sql,
            final Map<String, Object> params, final Class clazz) {
        return selectList(namedParamsToMarkers(sql), paramMapToArray(sql,
                params), clazz);
    }

    /**
     * Return parameterized query results as a single bean. Named parameters
     * are converted to parameter markers.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param clazz Class to map results to.
     * @return <T> typed object.
     */
    public final <T> T select(final String sql, final Map<String, Object> params,
            final Class clazz) {
        final List<T> list = selectList(sql, params, clazz);
        T object = null;
        if (!list.isEmpty()) {
            // Get first item in List
            object = list.get(0);
        }
        return object;
    }

    /**
     * Return parameterized query results as list of maps.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return List of Maps containing field name/value pair.
     */
    public abstract List<Map<String, Object>> selectList(final String sql,
            final Object[] params);

    /**
     * Return query results as list of maps.
     *
     * @param sql SQL statement to execute.
     * @return List of Maps containing field name/value pair.
     */
    public final List<Map<String, Object>> selectList(final String sql) {
        return selectList(sql, new Object[]{});
    }

    /**
     * Return parameterized query results as a single Map.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return List of Maps containing field name/value pair.
     */
    public final Map<String, Object> select(final String sql,
            final Object[] params) {
        final List<Map<String, Object>> list = selectList(sql, params);
        Map<String, Object> map = null;
        if (!list.isEmpty()) {
            // Get first item in List
            map = list.get(0);
        }
        return map;
    }

    /**
     * Return query results as a single Map.
     *
     * @param sql SQL statement to execute.
     * @return List of Maps containing field name/value pair.
     */
    public final Map<String, Object> select(final String sql) {
        final List<Map<String, Object>> list = selectList(sql, new Object[]{});
        Map<String, Object> map = null;
        if (!list.isEmpty()) {
            // Get first item in List
            map = list.get(0);
        }
        return map;
    }

    /**
     * Return parameterized query results as a single typed Object.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param fieldName Name of field to return.
     * @return Object by field name.
     */
    public final <T> T select(final String sql, final Object[] params,
            final String fieldName) {
        T object = null;
        Map<String, Object> map = null;
        map = select(sql, params);
        if (map != null) {
            // Get first item in List
            object = (T) map.get(fieldName);
        }
        return object;
    }

    /**
     * Return parameterized query results as a single typed Object.
     *
     * @param <T> Type of object that the handler returns.
     * @param sql SQL statement to execute.
     * @param fieldName Name of field to return.
     * @return Object by field name.
     */
    public final <T> T select(final String sql, final String fieldName) {
        T object = null;
        Map<String, Object> map = null;
        map = select(sql, new Object[]{});
        if (map != null) {
            // Get first item in List
            object = (T) map.get(fieldName);
        }
        return object;
    }

    /**
     * Return parameterized query results as list of maps. Named parameters
     * are converted to parameter markers.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return List of Maps containing field name/value pair.
     */
    public final List<Map<String, Object>> selectList(final String sql,
            final Map<String, Object> params) {
        return selectList(namedParamsToMarkers(sql), paramMapToArray(sql,
                params));
    }

    /**
     * Return parameterized query results as a single Map. Named parameters are
     * converted to parameter markers.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return List of Maps containing field name/value pair.
     */
    public final Map<String, Object> select(final String sql,
            final Map<String, Object> params) {
        final List<Map<String, Object>> list = selectList(namedParamsToMarkers(
                sql),
                paramMapToArray(sql, params));
        Map<String, Object> map = null;
        if (!list.isEmpty()) {
            // Get first item in List
            map = list.get(0);
        }
        return map;
    }

    /**
     * Executes parameterized INSERT, UPDATE, or DELETE SQL statement.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated.
     */
    public abstract int update(final String sql, final Object[] params);

    /**
     * Executes INSERT, UPDATE, or DELETE SQL statement.
     *
     * @param sql SQL statement to execute.
     * @return Number of rows updated.
     */
    public final int update(final String sql) {
        return update(sql, new Object[]{});
    }

    /**
     * Executes parameterized INSERT, UPDATE, or DELETE SQL statement. Named
     * parameters are converted to parameter markers.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated.
     */
    public final int update(final String sql, final Map<String, Object> params) {
        return update(namedParamsToMarkers(sql), paramMapToArray(sql, params));
    }

    /**
     * Executes parameterized INSERT statement and returns auto generated keys.
     * JDBC driver needs to support RETURN_GENERATED_KEYS.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Field name/value pairs of keys.
     */
    public abstract Map<String, Object> updateReturnKeys(final String sql,
            final Object[] params);

    /**
     * Executes INSERT statement and returns auto generated keys. JDBC driver
     * needs to support RETURN_GENERATED_KEYS.
     *
     * @param sql SQL statement to execute.
     * @return Field name/value pairs of keys.
     */
    public final Map<String, Object> updateReturnKeys(final String sql) {
        return updateReturnKeys(sql, new Object[]{});
    }

    /**
     * Executes parameterized INSERT statement and returns auto generated keys.
     * Named parameters are converted to parameter markers. JDBC driver needs to
     * support RETURN_GENERATED_KEYS.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Field name/value pairs of keys.
     */
    public final Map<String, Object> updateReturnKeys(final String sql,
            final Map<String, Object> params) {
        return updateReturnKeys(namedParamsToMarkers(sql), paramMapToArray(sql,
                params));
    }

    /**
     * Executes INSERT statement and returns auto generated key by name. JDBC
     * driver needs to support RETURN_GENERATED_KEYS.
     *
     * @param sql SQL statement to execute.
     * @param keyName Key name to return as int.
     * @return key value of key.
     */
    public final int updateReturnKey(final String sql, final String keyName) {
        return Integer.parseInt(updateReturnKeys(sql, new Object[]{}).get(
                keyName).toString());
    }

    /**
     * Executes parameterized INSERT statement and returns auto generated key by
     * name. JDBC driver needs to support RETURN_GENERATED_KEYS.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param keyName Key name to return as int.
     * @return key value of key.
     */
    public final int updateReturnKey(final String sql, final Object[] params,
            final String keyName) {
        return Integer.parseInt(updateReturnKeys(sql, params).get(keyName).
                toString());
    }

    /**
     * Executes parameterized INSERT statement and returns auto generated key by
     * name. Named parameters are converted to parameter markers. JDBC driver
     * needs to support RETURN_GENERATED_KEYS.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @param keyName Key name to return as int.
     * @return key value of key.
     */
    public final int updateReturnKey(final String sql,
            final Map<String, Object> params, final String keyName) {
        return Integer.parseInt(updateReturnKeys(namedParamsToMarkers(sql),
                paramMapToArray(sql, params)).get(keyName).toString());
    }

    /**
     * Executes INSERT, UPDATE, or DELETE SQL statement with batch parameters.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated array.
     */
    public abstract int[] batch(final String sql, final Object[][] params);

    /**
     * Executes INSERT, UPDATE, or DELETE SQL statement with batch parameters.
     * Named parameters are converted to parameter markers.
     *
     * @param sql SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters.
     * @return Number of rows updated array.
     */
    public final int[] batch(final String sql,
            final List<Map<String, Object>> params) {
        return batch(namedParamsToMarkers(sql), mapListToArray(sql, params));
    }

    /**
     * Clean up resources such as open Connections.
     */
    public abstract void cleanUp();
}
