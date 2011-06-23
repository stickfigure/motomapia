/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 * 
 * This code has been subsequently rewritten by Jeff Schnitzer (jeff@infohazard.org) to be
 * more comprehensible and less broken.
 */

package com.motomapia.util;

import com.google.appengine.api.datastore.GeoPt;

/**
 * Encoder for polylines, an efficient way of compressing line data into a string.  We use this
 * instead of sending thousands of x,y pairs in JSON across the wire because it would take too
 * long.
 * 
 * @author jeff
 */
public class Polylines
{
	/** Encode the track as a polyline */
	public static String encode(GeoPt[] track)
	{
		StringBuilder encodedPoints = new StringBuilder();

		int plat = 0;
		int plng = 0;
		int counter = 0;

		int listSize = track.length;

		GeoPt trackpoint;

		for (int i = 0; i < listSize; i++)
		{
			counter++;
			trackpoint = track[i];

			int late5 = floor1e5(trackpoint.getLatitude());
			int lnge5 = floor1e5(trackpoint.getLongitude());

			int dlat = late5 - plat;
			int dlng = lnge5 - plng;

			plat = late5;
			plng = lnge5;

			encodedPoints.append(encodeSignedNumber(dlat)).append(encodeSignedNumber(dlng));
		}

		return encodedPoints.toString();
	}
	
	/** */
	private static int floor1e5(double coordinate)
	{
		return (int) Math.floor(coordinate * 1e5);
	}

	/** */
	private static String encodeSignedNumber(int num)
	{
		int sgn_num = num << 1;
		if (num < 0)
		{
			sgn_num = ~(sgn_num);
		}
		return (encodeNumber(sgn_num));
	}

	/** */
	private static String encodeNumber(int num)
	{
		StringBuffer encodeString = new StringBuffer();

		while (num >= 0x20)
		{
			int nextValue = (0x20 | (num & 0x1f)) + 63;
			encodeString.append((char) (nextValue));
			num >>= 5;
		}

		num += 63;
		encodeString.append((char) (num));

		return encodeString.toString();
	}

// This code is what you would use in GWT to decode these.  Since we're using coffeescript instead, this
// code can be found in motomapia.coffee
//
//	/**
//	 * Spits out a format useful to javascript.
//	 * This code is copied from http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/decode.js 
//	 */
//	public static native JsArray<LatLng> decode(String encoded)
//	/*-{
//		var len = encoded.length;
//		var index = 0;
//		var array = [];
//		var lat = 0;
//		var lng = 0;
//
//		while (index < len) {
//			var b;
//			var shift = 0;
//			var result = 0;
//			do {
//			  b = encoded.charCodeAt(index++) - 63;
//			  result |= (b & 0x1f) << shift;
//			  shift += 5;
//			} while (b >= 0x20);
//			var dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
//			lat += dlat;
//	
//			shift = 0;
//			result = 0;
//			do {
//			  b = encoded.charCodeAt(index++) - 63;
//			  result |= (b & 0x1f) << shift;
//			  shift += 5;
//			} while (b >= 0x20);
//			var dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
//			lng += dlng;
//	
//			array.push(new $wnd.google.maps.LatLng(lat * 1e-5, lng * 1e-5));
//		}
//
//		return array;
//	}-*/;
		
}
