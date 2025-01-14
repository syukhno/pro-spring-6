/*
 * This file is generated by jOOQ.
 */
package com.apress.prospring6.seven.jooq.generated.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Singer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer version;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public Singer() {}

    public Singer(Singer value) {
        this.id = value.id;
        this.version = value.version;
        this.firstName = value.firstName;
        this.lastName = value.lastName;
        this.birthDate = value.birthDate;
    }

    public Singer(
        Integer id,
        Integer version,
        String firstName,
        String lastName,
        LocalDate birthDate
    ) {
        this.id = id;
        this.version = version;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    /**
     * Getter for <code>musicdb.SINGER.ID</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>musicdb.SINGER.ID</code>.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for <code>musicdb.SINGER.VERSION</code>.
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Setter for <code>musicdb.SINGER.VERSION</code>.
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Getter for <code>musicdb.SINGER.FIRST_NAME</code>.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Setter for <code>musicdb.SINGER.FIRST_NAME</code>.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for <code>musicdb.SINGER.LAST_NAME</code>.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Setter for <code>musicdb.SINGER.LAST_NAME</code>.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for <code>musicdb.SINGER.BIRTH_DATE</code>.
     */
    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    /**
     * Setter for <code>musicdb.SINGER.BIRTH_DATE</code>.
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Singer (");

        sb.append(id);
        sb.append(", ").append(version);
        sb.append(", ").append(firstName);
        sb.append(", ").append(lastName);
        sb.append(", ").append(birthDate);

        sb.append(")");
        return sb.toString();
    }
}
