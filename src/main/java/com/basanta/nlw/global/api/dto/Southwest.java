
package com.basanta.nlw.global.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Southwest implements Serializable
{

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    private final static long serialVersionUID = -937161084307694770L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Southwest() {
    }

    /**
     * 
     * @param lng
     * @param lat
     */
    public Southwest(double lat, double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lat).append(lng).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Southwest) == false) {
            return false;
        }
        Southwest rhs = ((Southwest) other);
        return new EqualsBuilder().append(lat, rhs.lat).append(lng, rhs.lng).isEquals();
    }

}
