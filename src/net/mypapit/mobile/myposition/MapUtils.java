package net.mypapit.mobile.myposition;

/* 
 * This file is (c) OsmAnd developers, GPLv3 
 * https://github.com/osmandapp/Osmand/blob/9bb03894a57cc80c2f9ad935ba007d2c406abd2c/OsmAnd-java/src/net/osmand/util/MapUtils.java
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This utility class includes : 
 * 1. distance algorithms
 * 2. finding center for array of nodes
 * 3. tile evaluation algorithms
 *   
 *
 */
public class MapUtils {
	
    // TODO change the hostname back to osm.org once HTTPS works for it
    // https://github.com/openstreetmap/operations/issues/2
    private static final String BASE_SHORT_OSM_URL = "https://openstreetmap.org/go/";
	
	/**
     * This array is a lookup table that translates 6-bit positive integer
     * index values into their "Base64 Alphabet" equivalents as specified
     * in Table 1 of RFC 2045.
     */
    private static final char intToBase64[] = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_', '~'
    };

	
	public static double checkLongitude(double longitude) {
		if(longitude > -180 && longitude <= 180) {
			return longitude;
		}
		while (longitude < -180 || longitude > 180) {
			if (longitude < 0) {
				longitude += 360;
			} else {
				longitude -= 360;
			}
		}
		return longitude;
	}
	
	public static double checkLatitude(double latitude) {
		if(latitude > -80 && latitude <= 80) {
			return latitude;
		}
		while (latitude < -90 || latitude > 90) {
			if (latitude < 0) {
				latitude += 180;
			} else {
				latitude -= 180;
			}
		}
		if(latitude < -85.0511) {
			return -85.0511;
 		} else if(latitude > 85.0511){
 			return 85.0511;
 		}
		return latitude;
	}
	
	
	public static String buildGeoUrl(double latitude, double longitude, int zoom) {
        return "geo:" + ((float) latitude) + "," + ((float)longitude) + "?z=" + zoom;
	}
	
	// Examples
//	System.out.println(buildShortOsmUrl(51.51829d, 0.07347d, 16)); // http://osm.org/go/0EEQsyfu
//	System.out.println(buildShortOsmUrl(52.30103d, 4.862927d, 18)); // http://osm.org/go/0E4_JiVhs
//	System.out.println(buildShortOsmUrl(40.59d, -115.213d, 9)); // http://osm.org/go/TelHTB--
	public static String buildShortOsmUrl(double latitude, double longitude, int zoom){
        return BASE_SHORT_OSM_URL + createShortLinkString(latitude, longitude, zoom) + "?m";
	}

	public static String createShortLinkString(double latitude, double longitude, int zoom) {
		long lat = (long) (((latitude + 90d)/180d)*(1L << 32));
		long lon = (long) (((longitude + 180d)/360d)*(1L << 32));
		long code = interleaveBits(lon, lat);
		String str = "";
	    // add eight to the zoom level, which approximates an accuracy of one pixel in a tile.
		for (int i = 0; i < Math.ceil((zoom + 8) / 3d); i++) {
		    str += intToBase64[(int) ((code >> (58 - 6 * i)) & 0x3f)];
		}
		// append characters onto the end of the string to represent
		// partial zoom levels (characters themselves have a granularity of 3 zoom levels).
		for (int j = 0; j < (zoom + 8) % 3; j++) {
			str += '-';
		}
		return str;
	}
	
	
	/**	
	 * interleaves the bits of two 32-bit numbers. the result is known as a Morton code.	   
	 */
	private static long interleaveBits(long x, long y){
		long c = 0;
		for(byte b = 31; b>=0; b--){
			c = (c << 1) | ((x >> b) & 1);
			c = (c << 1) | ((y >> b) & 1);
		}
		return c;
	}

}


