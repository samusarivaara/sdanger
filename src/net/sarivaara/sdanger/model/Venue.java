/*
 * 
 *     Author Samu Sarivaara
 *            samusarivaara@gmail.com
 *        
 *     All rights reserved.
 */
package net.sarivaara.sdanger.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Container class for Venue specified by
 * https://developer.foursquare.com/docs/responses/venue
 */

public class Venue implements Parcelable {

	private static final int DISTANCE_UNKNOWN = -1;
	
	// name - The best known name for this venue.
	private String mName;
	// Interpret result from:
	// location - An object containing none, some, 
	// or all of address (street address), crossStreet, city, state,
	// postalCode, country, lat, lng, and distance.
	// All fields are strings, except for lat, lng, and distance. Distance is measured in meters.
	private String mAddress; // TODO: better to be String[] 
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
	
	public String toString() {
		// TODO: format in view layer/localize
		return String.format("%s %sDistance: %d meters", mName, mAddress, mDistanceInMeters);
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
}
