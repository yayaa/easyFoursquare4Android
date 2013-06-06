package br.com.condesales.criterias;

import android.location.Location;
import android.location.LocationManager;

public class TipsCriteria {

	private String mQuery = "";
	private int mQuantity = 10;
	private int mOffset = 0;
	private Location mLocation = new Location(LocationManager.GPS_PROVIDER);

	public String getQuery() {
		return mQuery;
	}

	public void setQuery(String mQuery) {
		this.mQuery = mQuery;
	}

	public int getQuantity() {
		return mQuantity;
	}

	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}

	public Location getLocation() {
		return mLocation;
	}

	public void setLocation(Location mLocation) {
		this.mLocation = mLocation;
	}

	public int getOffset() {
		return mOffset;
	}

	public void setOffset(int mOffset) {
		this.mOffset = mOffset;
	}

}
