/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class adding simple bean mapping from database field names with
 * underscore to camelCase bean properties.
 *
 * @see com.codeferm.dbaccess.DbAccess
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DbBeanMapper extends DbAccess { //NOPMD, really, I have to call it AbstractXXX, screw that

    /**
     * Convert bean property names from camelCase to underscore and return
     * mapping. This way mapping only occurs one time for the entire
     * {@code ResultSet}.
     *
     * @param fields {@code Array} containing bean field names
     * @return {@code Map} of bean to database field names
     */
    public final Map<String, String> fromCamelCase(final Field[] fields) {
        final Map<String, String> map = new HashMap<String, String>();
        String[] splitArr = null;
        StringBuilder fieldName = null;
        for (Field field : fields) {
            // Split words based on capital letters
            splitArr = field.getName().split("(?=\\p{Upper})");
            fieldName = new StringBuilder(); //NOPMD OK to create new StringBuilder in loop
            for (int i = 0; i < splitArr.length; i += 1) {
                fieldName.append(splitArr[i]);
                // Add underscore if not last word
                if (i < splitArr.length - 1) {
                    fieldName.append("_");
                }
                // Field name to database field name mapping
                map.put(field.getName(), fieldName.toString());
            }
        }
        return map;
    }

    /**
     * Get write method of each property and store in Map by name.
     *
     * @param fields {@code Array} containing bean field names
     * @param clazz {@code Class} of bean
     * @return {@code Map} of bean write methods
     */
    public final Map<String, Method> getWriteMethods(final Field[] fields,
            final Class clazz) {
        final Map<String, Method> map = new HashMap<String, Method>();
        try {
            for (Field field : fields) {
                // Ignore synthetic classes or dynamic proxies.
                if (!field.isSynthetic()) {
                    PropertyDescriptor propertyDescriptor
                            = new PropertyDescriptor(field.getName(), clazz);  //NOPMD OK to create new PropertyDescriptor in loop
                    map.put(field.getName(), propertyDescriptor.
                            getWriteMethod());
                }
            }
        } catch (IntrospectionException e) {
            throw new DbAccessException(e);
        }
        return map;
    }

    /**
     * Return list of beans mapped from {@code ResultSet}.
     *
     * @param <T> Type of beans
     * @param resultSet {@code ResultSet} to process
     * @param clazz {@code Class} of bean
     * @return {@code List} of {@code <T>} type beans
     */
    public final <T> List<T> createObjects(final ResultSet resultSet,
            final Class clazz) {
        final List<T> list = new ArrayList<T>();
        // Get bean fields
        final Field[] fields = clazz.getDeclaredFields();
        // Get bean to database field name mappings
        final Map<String, String> dbMap = fromCamelCase(fields);
        // Get bean write methods
        final Map<String, Method> beanMap = getWriteMethods(fields, clazz);
        try {
            // Process ResultSet
            while (resultSet.next()) {
                // New bean
                final T instance = (T) clazz.newInstance();
                // Map ResultSet to bean properties
                for (Field field : fields) {
                    // Ignore synthetic classes or dynamic proxies.
                    if (!field.isSynthetic()) {
                        beanMap.get(field.getName()).invoke(instance, resultSet.
                                getObject(dbMap.get(field.getName())));
                    }
                }
                list.add(instance);
            }
        } catch (SQLException e) {
            throw new DbAccessException(e);
        } catch (InvocationTargetException e) {
            throw new DbAccessException(e);
        } catch (IllegalAccessException e) {
            throw new DbAccessException(e);
        } catch (InstantiationException e) {
            throw new DbAccessException(e);
        }
        return list;
    }
}
