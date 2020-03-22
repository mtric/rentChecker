package net.lehre_online.android.mietchecker;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * This class performs an SQL operation as an asynchronous task to insert data
 * into the database
 *
 * @author Michael Kaleve, Eric Walter
 * @version 1.1, 2019-07-07
 */
public class SqlInsert extends AsyncTask<Void, Void, Void> {

    private static final String TAG = ".SqlInsert";
    private static final boolean DBG = true;
    private String o_rent, o_tel, o_qm, o_rooms, o_address, o_zip, o_houseNr;
    private double o_lat, o_long;
    private java.sql.Date sqlDate;

    /**
     * This method is a constructor to initialize the variables in this class
     *
     * @param o_lat     The Latitude
     * @param o_long    The Longitude
     * @param o_rooms   The number of rooms
     * @param o_rent    The amount of rent
     * @param o_qm      The size in square meters
     * @param o_tel     The phone number
     * @param sqlDate   The availability date
     * @param o_address The street name
     * @param o_houseNr The house number
     * @param o_zip     The zip code
     */
    SqlInsert(Double o_lat, Double o_long, String o_rooms, String o_rent, String o_qm, String o_tel,
              java.sql.Date sqlDate, String o_address, String o_houseNr, String o_zip) {
        this.o_lat = o_lat;
        this.o_long = o_long;
        this.o_rooms = o_rooms;
        this.o_qm = o_qm;
        this.o_tel = o_tel;
        this.sqlDate = sqlDate;
        this.o_rent = o_rent;
        this.o_address = o_address;
        this.o_houseNr = o_houseNr;
        this.o_zip = o_zip;
    }

    /**
     * This method performs a background operation to establish a database connection
     * and insert given values to the database
     * @param params No parameter (void)
     * @return Returns null
     */
    @Override
    protected Void doInBackground(Void... params) {

        if (DBG) Log.i(TAG, "...insertData opened");
        final String USER = "USER356603_root";
        final String PW = "jF69gD5vs8qCark";
        final String INSTANCE = "db_356603_3";
        final String URL = "jdbc:mysql://kaleve.lima-db.de:3306";
        final String DRIVER = "com.mysql.jdbc.Driver";

        Connection con;
        String TAG = "SqlConnection";
        Log.i(TAG, "Try to establish connection to MySQL...");

        try {
            // JDBC-Treiber laden:
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL, USER, PW);

            if (con.isValid(10)) {
                Log.i(TAG, URL + "...Successfully connected!");
            }
            String sQl = "INSERT INTO " + INSTANCE + ".object( "
                    + "o_long, o_lat, o_qm, o_numberofrooms, o_adress, o_hnumber, " +
                    "o_plz, o_rent, o_free, o_tel ) "
                    + "VALUES ( ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

            String sQl1 = "INSERT INTO " + INSTANCE + ".object( "
                    + "o_long, o_lat, o_qm, o_numberofrooms, " +
                    "o_rent, o_free, o_tel, o_plz, o_adress, o_hnumber ) "
                    + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sQl1);

            // Das wird in die "?" bei VALUES eingesetzt
            ps.setDouble(1, o_long);
            ps.setDouble(2, o_lat);
            ps.setDouble(3, Double.parseDouble(o_qm));
            ps.setDouble(4, Double.parseDouble(o_rooms));
            ps.setDouble(5, Double.parseDouble(o_rent));
            ps.setDate(6, sqlDate);
            ps.setString(7, o_tel);
            ps.setDouble(8, Double.parseDouble(o_zip));
            ps.setString(9, o_address);
            ps.setString(10, o_houseNr);

            int n = ps.executeUpdate();
            if (n == 1) {
                Log.i(TAG, "O.K., Datensatz eingefuegt.");
            }
            ps.close();
            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "Connection kam nicht zustande");
        }
        return null;
    }
}
