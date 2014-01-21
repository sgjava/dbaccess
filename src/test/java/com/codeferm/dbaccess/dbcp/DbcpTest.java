/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 27, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dbcp;

import com.codeferm.dbaccess.DbAccess;
import com.codeferm.dbaccess.dto.TestTableDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test all implementations of {@link com.codeferm.dbaccess.DbAccess} class. Test
 * records are automatically removed from table.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbcpTest extends BaseDbcpTest {

    /**
     * {@code BigDecimal} test value.
     */
    private static final BigDecimal BIGDECIMAL_TEST_VALUE = BigDecimal.valueOf(
            1234567.99);
    /**
     * {@code Float} test value.
     */
    private static final Float FLOAT_TEST_VALUE = Float.valueOf(123.6F);
    /**
     * {@code Double} test value.
     */
    private static final Double DOUBLE_TEST_VALUE = Double.valueOf(123.456D);
    /**
     * Batch records.
     */
    private static final int BATCH_RECS = 3;
    /**
     * Bean mapping records.
     */
    private static final int BEAN_MAP_RECS = 100;

    /**
     * Test positional parameter methods of class DbAccess.
     *
     * @param db DbAccess implementation.
     * @param loops Number of times to execute test.
     * @throws SQLException possible exception.
     */
    public final void dbAccess(final DbAccess db, final int loops) throws
            SQLException {
        final long startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            // Test statement without parameters
            List<Map<String, Object>> listMap = db.selectList(
                    getSqlMap().get("select.test"));
            assertFalse("List must have at least one element",
                    listMap.isEmpty());
            Map<String, Object> map = db.select(getSqlMap().get("select.test"));
            assertFalse("Map must have at least one element",
                    map.isEmpty());
            // Test getting field by name without parameters
            Integer testInt = db.select(getSqlMap().get("select.test"),
                    "testval");
            assertTrue("Integer should be 1", testInt == 1);
            // Set parameter array for record insert
            Object[] params = new Object[]{"dbAccess", "varchar", Boolean.TRUE,
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE,
                BIGDECIMAL_TEST_VALUE, FLOAT_TEST_VALUE, DOUBLE_TEST_VALUE};
            // Insert test_table record and save key value generated by database
            // sequence. This will be used later to delete record
            final int id = db.updateReturnKey(getSqlMap().get(
                    "insert.testtable"), params, "id");
            assertTrue("Update return value should be > 0", id > 0);
            log.debug(String.format("Key value returned %d", id));
            params = new Object[]{id};
            // Get list of DTOs of type TestTableDto
            List<TestTableDto> list = db.selectList(getSqlMap().get(
                    "select.testtable.by.id"), params, TestTableDto.class);
            assertFalse("List must have at least one element", list.isEmpty());
            // Display timestamp_field
            for (TestTableDto dto : list) {
                log.debug(String.format("timestamp_field: %s",
                        dto.getTimestampField()));
            }
            // Get single DTO of type TestTableDto
            TestTableDto dto = db.select(getSqlMap().get(
                    "select.testtable.by.id"), params, TestTableDto.class);
            assertTrue("DTO getVarcharField must = varchar",
                    dto.getVarcharField().equals("varchar"));
            // Test getting field by name with parameters
            testInt = db.select(getSqlMap().get("select.testtable.by.id"),
                    params, "id");
            assertTrue("Integer should equal id", testInt == id);
            // Get list of Maps
            listMap = db.selectList(
                    getSqlMap().get("select.testtable.by.id"),
                    params);
            assertFalse("List must have at least one element",
                    listMap.isEmpty());
            // Display key/value pairs
            for (Map<String, Object> lMap : listMap) {
                for (Map.Entry<String, Object> entry : lMap.entrySet()) {
                    log.debug(entry.getKey() + ": " + entry.getValue());
                }
            }
            params = new Object[]{"update", id};
            // Update test record
            int rows = db.update(getSqlMap().get("update.testtable.by.id"),
                    params);
            assertTrue("Update return value should be > 0", rows > 0);
            params = new Object[]{id};
            // Get updated record
            list = db.selectList(getSqlMap().get("select.testtable.by.id"),
                    params, TestTableDto.class);
            // Make sure value was updated
            assertTrue("Update return value should be update", list.get(0).
                    getVarcharField().equals("update"));
            // Remove test record
            rows = db.update(getSqlMap().get("delete.testtable.by.id"), params);
            assertTrue("Update return value should be > 0", rows > 0);
            log.debug(String.format("%d rows deleted", rows));
        }
        final long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }

    /**
     * Test named parameter methods of class DbAccess.
     *
     * @param db DbAccess implementation.
     * @param loops Number of times to execute test.
     * @throws SQLException possible exception.
     */
    public final void dbAccessNamed(final DbAccess db, final int loops) throws
            SQLException {
        final long startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            // Create parameter map
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("char_field", "dbAccessNamed");
            params.put("varchar_field", "varchar");
            params.put("boolean_field", Boolean.TRUE);
            params.put("date_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params.put("time_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params.put("timestamp_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params.put("bigint_field", Long.MAX_VALUE);
            params.put("int_field", Integer.MAX_VALUE);
            params.put("smallint_field", Short.MAX_VALUE);
            params.put("numeric_field", BIGDECIMAL_TEST_VALUE);
            params.put("real_field", FLOAT_TEST_VALUE);
            params.put("double_field", DOUBLE_TEST_VALUE);
            // Insert test_table record and save key value generated by database
            // sequence. This will be used later to delete record
            final int id = db.updateReturnKey(getSqlMap().get(
                    "insert.testtable.named"), params, "id");
            assertTrue("Update return value should be > 0", id > 0);
            log.debug(String.format("Key value returned %d", id));
            params.put("int_field", id);
            // Get list of DTOs of type TestTableDto
            List<TestTableDto> list = db.selectList(getSqlMap().get(
                    "select.testtable.by.id.named"), params,
                    TestTableDto.class);
            assertFalse("List must have at least one element", list.isEmpty());
            // Display timestamp_field
            for (TestTableDto dto : list) {
                log.debug(String.format("timestamp_field: %s",
                        dto.getTimestampField()));
            }
            // Get list of Maps
            List<Map<String, Object>> listMap = db.selectList(getSqlMap().get(
                    "select.testtable.by.id.named"), params);
            assertFalse("List must have at least one element",
                    listMap.isEmpty());
            // Display key/value pairs
            for (Map<String, Object> map : listMap) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    log.debug(entry.getKey() + ": " + entry.getValue());
                }
            }
            params.put("varchar_field", "update");
            // Update test record
            int rows = db.update(getSqlMap().get(
                    "update.testtable.by.id.named"), params);
            assertTrue("Update return value should be > 0", rows > 0);
            // Get updated record
            list = db.selectList(getSqlMap().get(
                    "select.testtable.by.id.named"), params,
                    TestTableDto.class);
            // Make sure value was updated
            assertTrue("Update return value should be update", list.get(0).
                    getVarcharField().equals("update"));
            // Remove test record
            rows = db.update(getSqlMap().get("delete.testtable.by.id.named"),
                    params);
            assertTrue("Update return value should be > 0", rows > 0);
            log.debug(String.format("%d rows deleted", rows));
        }
        final long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }

    /**
     * Test of batch method, of class DbAccess using positional parameter
     * methods.
     *
     * @param db DbAccess implementation.
     * @param loops Number of times to execute test.
     * @throws SQLException possible exception.
     */
    public final void batch(final DbAccess db, final int loops) throws
            SQLException {
        final long startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            // Create parameter array
            Object[][] batchParams = new Object[][]{new Object[]{"dbAccess",
                    "batch", Boolean.TRUE,
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE,
                    BIGDECIMAL_TEST_VALUE, FLOAT_TEST_VALUE, DOUBLE_TEST_VALUE},
                new Object[]{"dbAccess", "batch", Boolean.TRUE,
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE,
                    BIGDECIMAL_TEST_VALUE, FLOAT_TEST_VALUE, DOUBLE_TEST_VALUE},
                new Object[]{"dbAccess", "batch", Boolean.TRUE,
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE,
                    BIGDECIMAL_TEST_VALUE, FLOAT_TEST_VALUE, DOUBLE_TEST_VALUE}};
            final int[] rows = db.batch(getSqlMap().get("insert.testtable"),
                    batchParams);
            // Make sure each row returned a value > 0
            for (int row : rows) {
                assertTrue("Update return value should be > 0", row > 0);
                log.debug(String.format("%d rows inserted", row));
            }
            // Create parameter object
            Object[] params = new Object[]{"batch"};
            // Remove test records
            final int delrows = db.update(getSqlMap().get(
                    "delete.testtable.by.varchar.field"), params);
            assertTrue("Update return value should equal 3", delrows
                    == BATCH_RECS);
            log.debug(String.format("%d rows deleted", delrows));
        }
        final long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }

    /**
     * Test of batch method, of class DbAccess using named parameter methods.
     *
     * @param db DbAccess implementation.
     * @param loops Number of times to execute test.
     * @throws SQLException possible exception.
     */
    public final void batchNamed(final DbAccess db, final int loops) throws
            SQLException {
        final long startTime = System.currentTimeMillis();
        for (int number = 1; number <= loops; number++) {
            // Create parameter maps
            Map<String, Object> params1 = new HashMap<String, Object>();
            params1.put("char_field", "batchNamed");
            params1.put("varchar_field", "batch");
            params1.put("boolean_field", Boolean.TRUE);
            params1.put("date_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params1.put("time_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params1.put("timestamp_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params1.put("bigint_field", Long.MAX_VALUE);
            params1.put("int_field", Integer.MAX_VALUE);
            params1.put("smallint_field", Short.MAX_VALUE);
            params1.put("numeric_field", BIGDECIMAL_TEST_VALUE);
            params1.put("real_field", FLOAT_TEST_VALUE);
            params1.put("double_field", DOUBLE_TEST_VALUE);
            Map<String, Object> params2 = new HashMap<String, Object>();
            params2.put("char_field", "batchNamed");
            params2.put("varchar_field", "batch");
            params2.put("boolean_field", Boolean.TRUE);
            params2.put("date_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params2.put("time_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params2.put("timestamp_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params2.put("bigint_field", Long.MAX_VALUE);
            params2.put("int_field", Integer.MAX_VALUE);
            params2.put("smallint_field", Short.MAX_VALUE);
            params2.put("numeric_field", BIGDECIMAL_TEST_VALUE);
            params2.put("real_field", FLOAT_TEST_VALUE);
            params2.put("double_field", DOUBLE_TEST_VALUE);
            Map<String, Object> params3 = new HashMap<String, Object>();
            params3.put("char_field", "batchNamed");
            params3.put("varchar_field", "batch");
            params3.put("boolean_field", Boolean.TRUE);
            params3.put("date_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params3.put("time_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params3.put("timestamp_field", new java.sql.Timestamp(System.
                    currentTimeMillis()));
            params3.put("bigint_field", Long.MAX_VALUE);
            params3.put("int_field", Integer.MAX_VALUE);
            params3.put("smallint_field", Short.MAX_VALUE);
            params3.put("numeric_field", BIGDECIMAL_TEST_VALUE);
            params3.put("real_field", FLOAT_TEST_VALUE);
            params3.put("double_field", DOUBLE_TEST_VALUE);
            List<Map<String, Object>> batchParams =
                    new ArrayList<Map<String, Object>>(BATCH_RECS);
            batchParams.add(params1);
            batchParams.add(params2);
            batchParams.add(params3);
            final int[] rows = db.batch(
                    getSqlMap().get("insert.testtable.named"), batchParams);
            // Make sure each row returned a value > 0
            for (int row : rows) {
                assertTrue("Update return value should be > 0", row > 0);
                log.debug(String.format("%d rows inserted", row));
            }
            // Create parameter map
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("varchar_field", "batch");
            // Remove test records
            final int delrows = db.update(getSqlMap().get(
                    "delete.testtable.by.varchar.field.named"), params);
            assertTrue("Update return value should equal 3", delrows
                    == BATCH_RECS);
            log.debug(String.format("%d rows deleted", delrows));
        }
        final long elapsedTime = System.currentTimeMillis() - startTime;
        log.info(String.format("Elapsed time: %d ms, loops: %d, average: %d ms",
                elapsedTime, loops, elapsedTime / loops));
    }

    /**
     * Test bean mapping performance of class DbAccess.
     *
     * @param db DbAccess implementation.
     * @throws SQLException possible exception.
     */
    public final void beanMap(final DbAccess db) throws
            SQLException {
        // Create parameter maps
        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("char_field", "batchNamed");
        params1.put("varchar_field", "batch");
        params1.put("boolean_field", Boolean.TRUE);
        params1.put("date_field", new java.sql.Timestamp(System.
                currentTimeMillis()));
        params1.put("time_field", new java.sql.Timestamp(System.
                currentTimeMillis()));
        params1.put("timestamp_field", new java.sql.Timestamp(System.
                currentTimeMillis()));
        params1.put("bigint_field", Long.MAX_VALUE);
        params1.put("int_field", Integer.MAX_VALUE);
        params1.put("smallint_field", Short.MAX_VALUE);
        params1.put("numeric_field", BIGDECIMAL_TEST_VALUE);
        params1.put("real_field", FLOAT_TEST_VALUE);
        params1.put("double_field", DOUBLE_TEST_VALUE);
        List<Map<String, Object>> batchParams =
                new ArrayList<Map<String, Object>>(BEAN_MAP_RECS);
        // Add records to batch
        for (int number = 1; number <= BEAN_MAP_RECS; number++) {
            batchParams.add(params1);
        }
        db.batch(getSqlMap().get("insert.testtable.named"), batchParams);
        // Set parameter array for record insert
        Object[] params2 = new Object[]{"batch"};
        final long startTime = System.currentTimeMillis();
        // Get list of DTOs of type TestTableDto
        List<TestTableDto> list = db.selectList(getSqlMap().get(
                "select.testtable.by.varchar.field"), params2,
                TestTableDto.class);
        final long elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue("List size incorrect", list.size() == BEAN_MAP_RECS);
        log.info(String.format(
                "Elapsed time: %d ms, bean mappings: %d, average: %d ms",
                elapsedTime, BEAN_MAP_RECS, elapsedTime / BEAN_MAP_RECS));
        // Create parameter map
        Map<String, Object> params3 = new HashMap<String, Object>();
        params3.put("varchar_field", "batch");
        // Remove test records
        final int delrows = db.update(getSqlMap().get(
                "delete.testtable.by.varchar.field.named"), params3);
        assertTrue("Update return value incorrect", delrows
                == BEAN_MAP_RECS);
        log.debug(String.format("%d rows deleted", delrows));
    }
}
