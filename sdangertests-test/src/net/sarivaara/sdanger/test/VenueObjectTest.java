package net.sarivaara.sdanger.test;
import net.sarivaara.sdanger.model.Venue;
import android.os.Parcel;
import android.test.AndroidTestCase;


public class VenueObjectTest extends AndroidTestCase {

	
	public void testParcelable() {
		
		checkParceling("name", "address", 432);
		checkParceling("abba\n abba baa\na", null, 0);
		checkParceling(null, null, 0);		
	}

	private void checkParceling(String name, String address, int distance) {
	
		Venue venue = new Venue();
		venue.setName(name);
		venue.setAddress(address);
		venue.setDistanceInMeters(distance);
		
		// To Parcel
	    Parcel parcel = Parcel.obtain();
	    venue.writeToParcel(parcel, 0);
	    parcel.setDataPosition(0);

	    // and back
	    Venue createdFromParcel = Venue.CREATOR.createFromParcel(parcel);
	    assertEquals(venue, createdFromParcel);
	}
	

}
