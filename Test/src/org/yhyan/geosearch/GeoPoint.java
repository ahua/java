package org.yhyan.geosearch;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple bean to represent a single point on the earth.
 */
public class GeoPoint {

	private double longitude;
	private double latitude;

	public GeoPoint(double longitude, double latitude) {
		setLongitude(longitude);
		setLatitude(latitude);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getNormalizedLongitude() {
		return normalize(getLongitude(), 180);
	}

	public String getNormalizedLatitude() {
		return normalize(getLatitude(), 90);
	}

	private String normalize(double coord, int offset) {
		Double d = coord + offset;
		String s = String.valueOf(d);
		String[] parts = StringUtils.split(s, ".");
		if (parts[1].length() > 6) {
			parts[1] = parts[1].substring(0, 6);
		}
		return StringUtils.leftPad(parts[0], 3, "0") + StringUtils.rightPad(parts[1], 6, "0");
	}

	public double distanceFrom(GeoPoint anotherPoint) {
		double distX = Math.abs(anotherPoint.getLongitude() - this.getLongitude());
		double distY = Math.abs(anotherPoint.getLatitude() - this.getLatitude());
		return Math.sqrt((distX * distX) + (distY * distY));
	}

	public static void main(String[] args) {

		GeoPoint p = new GeoPoint(1.0, 1.0);
		System.out.println(p.getNormalizedLatitude());
		System.out.println(p.getNormalizedLongitude());
	}
}
