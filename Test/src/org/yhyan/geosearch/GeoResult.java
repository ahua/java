package org.yhyan.geosearch;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Bean to represent results of a GeoSearch.
 */
public class GeoResult {

	private String name;
	private String address;
	private String phone;
	private String category;
	private double distanceKmFromOrigin;
	private double latitude;
	private double longitude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getDistanceKmFromOrigin() {
		return distanceKmFromOrigin;
	}

	public void setDistanceKmFromOrigin(double distanceKmFromOrigin) {
		this.distanceKmFromOrigin = distanceKmFromOrigin;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
	}
}