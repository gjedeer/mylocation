/*
  Author:
  Ansgar Esztermann (DG8EKG)
 */

package net.mypapit.mobile.myposition;

public final class MaidenheadLocator {
    public static final double LAT_OFFSET = 90;
    public static final double LON_OFFSET = 180;
    public static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWX";
    private final String LOC;


    public MaidenheadLocator(double latitude, double longitude) {
        double mLat = latitude + LAT_OFFSET;
        double mLon =  longitude + LON_OFFSET;

        char lon_field = CODE_ALPHABET.charAt((int) mLon/20);
        char lat_field = CODE_ALPHABET.charAt((int) mLat/10);
        StringBuilder sb = new StringBuilder();
        sb.append (lon_field);
        sb.append (lat_field);

        double lon_square = (mLon % 20.0)/2;
        double lat_square = (mLat % 10.0);
        sb.append((int)lon_square);
        sb.append((int)lat_square);

        double lon_sub_square = (lon_square % 1.0) * 24;
        double lat_sub_square = (lat_square % 1.0) * 24;
        sb.append(CODE_ALPHABET.charAt((int)lon_sub_square));
        sb.append(CODE_ALPHABET.charAt((int)lat_sub_square));

        sb.append(' ');

        double lon_ext_square = (lon_sub_square % 1.0) * 10;
        double lat_ext_square = (lat_sub_square % 1.0) * 10;
        sb.append((int)lon_ext_square);
        sb.append((int)lat_ext_square);

        LOC = sb.toString();
    }

    public String toString() {
        return getLOC();
    }

    public String getLOC() {
        return LOC;
    }
}