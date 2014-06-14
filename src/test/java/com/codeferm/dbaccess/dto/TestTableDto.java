/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 * Created by Steven P. Goldsmith on November 6, 2011
 * sgoldsmith@com.codeferm
 */
package com.codeferm.dbaccess.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import net.sf.persist.annotations.NoTable;

/**
 * Represents test_table as {@code Object}.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@NoTable // Used by Persist or bean mapping fails
public final class TestTableDto implements Serializable {

    /**
     * Unique identifier.
     */
    private Integer id;
    /**
     * CHAR field.
     */
    private String charField;
    /**
     * VARCHAR field.
     */
    private String varcharField;
    /**
     * BOOLEAN field.
     */
    private Boolean booleanField;
    /**
     * DATE field.
     */
    private Date dateField;
    /**
     * TIME field.
     */
    private Date timeField;
    /**
     * TIMESTAMP field.
     */
    private Date timestampField;
    /**
     * BIGINT field.
     */
    private Long bigintField;
    /**
     * INT field.
     */
    private Integer intField;
    /**
     * smallint is a Types.SMALLINT which is mapped to java.lang.Integer by the
     * JDBC spec. See appendix B of the spec.
     */
    private Integer smallintField;
    /**
     * NUMERIC field.
     */
    private BigDecimal numericField;
    /**
     * REAL field.
     */
    private Float realField;
    /**
     * DOUBLE field.
     */
    private Double doubleField;

    /**
     * Default constructor.
     */
    public TestTableDto() {
    }

    /**
     * Constructor used to populate all fields.
     *
     * @param id Unique identifier
     * @param charField CHAR
     * @param varcharField VARCHAR
     * @param booleanField BOOLEAN
     * @param dateField DATE
     * @param timeField TIME
     * @param timestampField TIMESTAMP
     * @param bigintField BIGINT
     * @param intField INT
     * @param smallintField SMALLINT
     * @param numericField NUMERIC
     * @param realField REAL
     * @param doubleField DOUBLE
     */
    //CHECKSTYLE:OFF ParameterNumber
    public TestTableDto(final Integer id, final String charField, //CHECKSTYLE:ON ParameterNumber
            final String varcharField, final Boolean booleanField,
            final Date dateField, final Date timeField,
            final Date timestampField, final Long bigintField,
            final Integer intField, final Integer smallintField,
            final BigDecimal numericField, final Float realField,
            final Double doubleField) {
        this.id = id;
        this.charField = charField;
        this.varcharField = varcharField;
        this.booleanField = booleanField;
        this.dateField = new Date(dateField.getTime());
        this.timeField = new Date(timeField.getTime());
        this.timestampField = new Date(timestampField.getTime());
        this.bigintField = bigintField;
        this.intField = intField;
        this.smallintField = smallintField;
        this.numericField = numericField;
        this.realField = realField;
        this.doubleField = doubleField;
    }

    /**
     * Get BIGINT field.
     *
     * @return Long mapping
     */
    public Long getBigintField() {
        return bigintField;
    }

    /**
     * Set BIGINT field.
     *
     * @param bigintField Long mapping
     */
    public void setBigintField(final Long bigintField) {
        this.bigintField = bigintField;
    }

    /**
     * Get BOOLEAN field.
     *
     * @return Boolean mapping
     */
    public Boolean getBooleanField() {
        return booleanField;
    }

    /**
     * Set BOOLEAN field.
     *
     * @param booleanField Boolean mapping
     */
    public void setBooleanField(final Boolean booleanField) {
        this.booleanField = booleanField;
    }

    /**
     * Get CHAR field.
     *
     * @return String mapping
     */
    public String getCharField() {
        return charField;
    }

    /**
     * Set CHAR field.
     *
     * @param charField String mapping
     */
    public void setCharField(final String charField) {
        this.charField = charField;
    }

    /**
     * Get DATE field.
     *
     * @return Date mapping
     */
    public Date getDateField() {
        return new Date(dateField.getTime());
    }

    /**
     * Set DATE field.
     *
     * @param dateField Date mapping
     */
    public void setDateField(final Date dateField) {
        this.dateField = new Date(dateField.getTime());
    }

    /**
     * Get DOUBLE field.
     *
     * @return Double mapping
     */
    public Double getDoubleField() {
        return doubleField;
    }

    /**
     * Set DOUBLE field.
     *
     * @param doubleField Double mapping
     */
    public void setDoubleField(final Double doubleField) {
        this.doubleField = doubleField;
    }

    /**
     * Get key field.
     *
     * @return Integer mapping
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set key field.
     *
     * @param id Integer mapping
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Get INT field.
     *
     * @return Integer mapping
     */
    public Integer getIntField() {
        return intField;
    }

    /**
     * Set INT field.
     *
     * @param intField Integer mapping
     */
    public void setIntField(final Integer intField) {
        this.intField = intField;
    }

    /**
     * Get NUMERIC field.
     *
     * @return BigDecimal mapping
     */
    public BigDecimal getNumericField() {
        return numericField;
    }

    /**
     * Set NUMERIC field.
     *
     * @param numericField BigDecimal mapping
     */
    public void setNumericField(final BigDecimal numericField) {
        this.numericField = numericField;
    }

    /**
     * Get REAL field.
     *
     * @return Float mapping
     */
    public Float getRealField() {
        return realField;
    }

    /**
     * Set REAL field.
     *
     * @param realField Float mapping
     */
    public void setRealField(final Float realField) {
        this.realField = realField;
    }

    /**
     * Get SMALLINT field.
     *
     * @return Integer mapping
     */
    public Integer getSmallintField() {
        return smallintField;
    }

    /**
     * Set SMALLINT field.
     *
     * @param smallintField Integer mapping
     */
    public void setSmallintField(final Integer smallintField) {
        this.smallintField = smallintField;
    }

    /**
     * Get TIME field.
     *
     * @return Date mapping
     */
    public Date getTimeField() {
        return new Date(timeField.getTime());
    }

    /**
     * Set TIME field.
     *
     * @param timeField Date mapping
     */
    public void setTimeField(final Date timeField) {
        this.timeField = new Date(timeField.getTime());
    }

    /**
     * Get TIMESTAMP field.
     *
     * @return Date mapping
     */
    public Date getTimestampField() {
        return new Date(timestampField.getTime());
    }

    /**
     * Set TIMESTAMP field.
     *
     * @param timestampField Date mapping
     */
    public void setTimestampField(final Date timestampField) {
        this.timestampField = new Date(timestampField.getTime());
    }

    /**
     * Get VARCHAR field.
     *
     * @return String mapping
     */
    public String getVarcharField() {
        return varcharField;
    }

    /**
     * Set VARCHAR field.
     *
     * @param varcharField String mapping
     */
    public void setVarcharField(final String varcharField) {
        this.varcharField = varcharField;
    }
}
