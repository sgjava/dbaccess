/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.transaction;

import com.codeferm.dbaccess.DbAccess;
import com.codeferm.dbaccess.DbAccessException;
import com.codeferm.dbaccess.dto.TestTableDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Atomikos transactions test. Test records are automatically removed from
 * table.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransactionTest {

    /**
     * Logger.
     */
    //CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
    private static final Logger log = LoggerFactory.getLogger(//NOPMD
            TransactionTest.class);
    //CHECKSTYLE:ON ConstantName
    /**
     * BigDecimal test value.
     */
    private static final BigDecimal BIGDECIMAL_TEST_VALUE = BigDecimal.valueOf(
            1234567.99);
    /**
     * Float test value.
     */
    private static final Float FLOAT_TEST_VALUE = Float.valueOf(123.6F);
    /**
     * Double test value.
     */
    private static final Double DOUBLE_TEST_VALUE = Double.valueOf(123.456D);

    /**
     * Commit transaction.
     *
     * @param db dbAccess implementation.
     * @param sql SQL for insert.
     * @return ID of record inserted.
     * @throws Exception Possible exception.
     */
    //CHECKSTYLE:OFF DesignForExtension - Guice methods cannot be final
    @Transaction
    //CHECKSTYLE:ON DesignForExtension
    public int commitRec(final DbAccess db, final String sql) throws
            Exception {
        // Create parameter array
        Object[] params = new Object[]{"dbAccess", "varchar", Boolean.TRUE,
            new java.sql.Timestamp(System.currentTimeMillis()),
            new java.sql.Timestamp(System.currentTimeMillis()),
            new java.sql.Timestamp(System.currentTimeMillis()), Long.MAX_VALUE,
            Integer.MAX_VALUE, Short.MAX_VALUE, BIGDECIMAL_TEST_VALUE,
            FLOAT_TEST_VALUE, DOUBLE_TEST_VALUE};
        int id = db.updateReturnKey(sql, params, "id");
        if (id <= 0) {
            throw new DbAccessException("Update return value should be > 0");
        }
        log.debug(String.format("Key value returned %d", id));
        return id;
    }

    /**
     * Verify commit and remove record.
     *
     * @param db dbAccess implementation.
     * @param selectSql SQL for selectList.
     * @param deleteSql SQL for delete.
     * @param id Key of recored to check/delete.
     * @throws SQLException possible exception.
     */
    //CHECKSTYLE:OFF DesignForExtension - Guice methods cannot be final
    @Transaction
    //CHECKSTYLE:ON DesignForExtension
    public void verifyCommit(final DbAccess db, final String selectSql,
            final String deleteSql, final int id) throws
            SQLException {
        // Get list of DTOs of type TestTableDto
        List<TestTableDto> list = db.selectList(selectSql, new Object[]{id},
                TestTableDto.class);
        if (list.isEmpty()) {
            throw new DbAccessException("Record not commited");
        }
        // Remove test record
        db.update(deleteSql, new Object[]{id});
    }

    /**
     * Rollback transaction by causing exception condition.
     *
     * @param db dbAccess implementation.
     * @param sql SQL for insert.
     * @return ID of record inserted.
     * @throws Exception Possible exception.
     */
    //CHECKSTYLE:OFF DesignForExtension - Guice methods cannot be final
    @Transaction
    //CHECKSTYLE:ON DesignForExtension
    public int rollbackRec(final DbAccess db, final String sql) throws
            Exception {
        // Create parameter array
        Object[] params = new Object[]{"dbAccess", "varchar", Boolean.TRUE,
            new java.sql.Timestamp(System.currentTimeMillis()),
            new java.sql.Timestamp(System.currentTimeMillis()),
            new java.sql.Timestamp(System.currentTimeMillis()), Long.MAX_VALUE,
            Integer.MAX_VALUE, Short.MAX_VALUE, BIGDECIMAL_TEST_VALUE,
            FLOAT_TEST_VALUE, DOUBLE_TEST_VALUE};
        // This should go through without error, but not be committed
        int id = db.updateReturnKey(sql, params, "id");
        // Create empty parameter array to cause rollback
        params = new Object[]{};
        db.update(sql, params);
        if (id <= 0) {
            throw new DbAccessException("Update return value should be > 0");
        }
        log.debug(String.format("Key value returned %d", id));
        return id;
    }

    /**
     * Verify rollback by checking if record was committed.
     *
     * @param db dbAccess implementation.
     * @param selectSql SQL for selectList.
     * @param deleteSql SQL for delete.
     * @param id Key of recored to check/delete.
     * @throws SQLException possible exception.
     */
    //CHECKSTYLE:OFF DesignForExtension - Guice methods cannot be final
    @Transaction
    //CHECKSTYLE:ON DesignForExtension
    public void verifyRollback(final DbAccess db, final String selectSql,
            final String deleteSql, final int id) throws
            SQLException {
        // Get list of DTOs of type TestTableDto
        List<TestTableDto> list = db.selectList(selectSql, new Object[]{id},
                TestTableDto.class);
        if (!list.isEmpty()) {
            throw new DbAccessException(
                    "Record commited and should have been rolled back");
        }
    }
}
