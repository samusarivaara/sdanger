/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * Container class for the venue specified by
 * https://developer.foursquare.com/docs/responses/venue
 * NOTE: Could be improved to use String[] for address instead of single String.
 */

public class Venue implements Parcelable {

	public static final int DISTANCE_UNKNOWN = -1;
	
	// name - The best known name for this venue.
	private String mName;
	// Interpret result from:
	// location - An object containing none, some, 
	// or all of address (street address), crossStreet, city, state,
	// postalCode, country, lat, lng, and distance.
	// All fields are strings, except for lat, lng, and distance. Distance is measured in meters.
	private String mAddress;
	private int mDistanceInMeters = DISTANCE_UNKNOWN;
	
	// Getters
	
	public String getName() {
		return mName;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public int getDistanceInMeters() {
		return mDistanceInMeters;
	}
	
	// Setters
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setAddress(String address) {
		mAddress = address;
	}
	
	public void setDistanceInMeters(int distanceMeters) {
		mDistanceInMeters = distanceMeters;
	}
	
	// ONLY for debugging purposes
	@SuppressLint("DefaultLocale")
	public String toString() {
		return String.format("%s %s Distance: %d meters", mName, mAddress, mDistanceInMeters);
	}
	
	// Parcelable interface related
	
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        
    	out.writeValue(mName); // allows null values
        out.writeValue(mAddress); // allow null values
        out.writeInt(mDistanceInMeters);
    }

    public static final Parcelable.Creator<Venue> CREATOR
            = new Parcelable.Creator<Venue>() {
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };
    
    private Venue(Parcel in) {
    	
        mName = (String) in.readValue(String.class.getClassLoader());
        mAddress = (String) in.readValue(String.class.getClassLoader());
        mDistanceInMeters = in.readInt();        
    }
    
    public Venue() {
    	
    }

    // Generated by eclipse
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mAddress == null) ? 0 : mAddress.hashCode());
		result = prime * result + mDistanceInMeters;
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		return result;
	}
    // Generated by eclipse
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Venue other = (Venue) obj;
		if (mAddress == null) {
			if (other.mAddress != null)
				return false;
		} else if (!mAddress.equals(other.mAddress))
			return false;
		if (mDistanceInMeters != other.mDistanceInMeters)
			return false;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
			return false;
		return true;
	}
}
