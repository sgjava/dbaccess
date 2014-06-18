/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.dbutils.BeanProcessor;

/**
 * DbUtils {@code BeanProcessor} to convert underscore "_" database field names
 * to camelCase.
 *
 *
 * @see com.codeferm.dbaccess.DbQueryRunnerConn
 * @see com.codeferm.dbaccess.DbQueryRunnerDs
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbBeanProcessor extends BeanProcessor {

    /**
     * Map ResultSet to bean properties.
     *
     * @param rsmd ResultSet to map.
     * @param props Bean property descriptor.
     * @return Array mapping field to bean.
     * @throws SQLException Possible exception.
     */
    @Override
    protected final int[] mapColumnsToProperties(final ResultSetMetaData rsmd,
            final PropertyDescriptor[] props) throws SQLException {
        final int cols = rsmd.getColumnCount();
        final int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
        for (int propIndex = 0; propIndex < props.length; propIndex++) {
            final String propName = props[propIndex].getName();
            for (int col = 1; col <= cols; col++) {
                final String columnName = this.toCamelCase(rsmd.getColumnName(
                        col));
                if (columnName.equalsIgnoreCase(propName)) {
                    columnToProperty[col] = propIndex;
                    break;
                }
            }
        }
        return columnToProperty;
    }

    /**
     * Convert string with '_' to camelCase.
     *
     * @param underscoreStr Input string.
     * @return camelCase String.
     */
    private String toCamelCase(final String underscoreStr) {
        final StringBuffer sbStr = new StringBuffer();
        final String[] str = underscoreStr.split("_");
        boolean firstTime = true;
        for (String temp : str) {
            if (firstTime) {
                sbStr.append(temp.toLowerCase(Locale.ENGLISH));
                firstTime = false;
            } else {
                sbStr.append(Character.toUpperCase(temp.charAt(0)));
                sbStr.append(temp.substring(1).toLowerCase());
            }
        }
        return sbStr.toString();
    }
}
