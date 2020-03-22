package net.lehre_online.android.mietchecker;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * This class performs an async task to run a database connection in the background
 * and returns an object list back to the main thread through an interface
 *
 * @author Michael Kaleve, Eric Walter
 * @version 1.1, 2019-06-01
 */
public class SqlConnectorWithDelegate extends AsyncTask<Void, Void, ArrayList> {

    /**
     * Interface to handle objects between threads
     */
    public interface AsyncResponse {
        /**
         * The method sends the object back to the main thread
         *
         * @param objectList The object array list
         */
        void processFinish(ArrayList objectList);
    }

    private AsyncResponse delegate = null;

    /**
     * This method is a constructor to initialize the AsyncResponse
     *
     * @param delegate The AsyncResponse
     */
    SqlConnectorWithDelegate(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    /**
     * This method is a constructor to print information in the console
     */
    SqlConnectorWithDelegate() {
        String CLASSNAME = getClass().getName();
        System.out.println("Constructor " + CLASSNAME + " at " + new Date() + "...");
    }

    /**
     * Method to connect with the database in the background.
     * Login information should be removed in the future.
     */
    @Override
    public ArrayList doInBackground(Void... params) {

        final String USER = "USER356603_root";
        final String PW = "jF69gD5vs8qCark";
        final String INSTANCE = "db_356603_3";
        final String URL = "jdbc:mysql://kaleve.lima-db.de:3306";
        final String DRIVER = "com.mysql.jdbc.Driver";
        ArrayList objectList = new ArrayList();
        String sQuery = "select * from " + INSTANCE + ".object";
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

            Statement stm = con.createStatement();

            // load DB result sets here
            stm.executeQuery(sQuery);
            ResultSet rs;
            rs = stm.getResultSet();

            if (rs != null) {
                while (rs.next()) {
                    Log.i(TAG, "Object: " + rs.getInt(1) + ", " + rs.getInt(2) + ", " + rs.getDouble(3) + ", "
                            + rs.getDouble(4) + ", " + rs.getDouble(5));
                    objectList.add(rs.getInt(1));
                    objectList.add(rs.getDouble(2));
                    objectList.add(rs.getDouble(3));
                    objectList.add(rs.getDouble(4));
                    objectList.add(rs.getDouble(5));
                    objectList.add(rs.getString(6));
                    objectList.add(rs.getString(7));
                    objectList.add(rs.getInt(8));
                    objectList.add(rs.getDouble(9));
                    objectList.add(rs.getDate(10));
                    objectList.add(rs.getString(11));
                }

                // Anzahl Datensätze ausgeben:
                rs.last();
                Log.i(TAG, "\nDie Tabelle enthält " + rs.getRow() + " Datensätze.");
                // print out the float arrays in the arrayList
                Log.i(TAG, Arrays.deepToString(objectList.toArray()));
            }

            if (rs != null) rs.close();
            stm.close();
            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "Connection kam nicht zustande");
        }
        return objectList;
    }

    /**
     * This method sends the object list to the interface
     * @param objectList The array list with object information
     */
    @Override
    protected void onPostExecute(ArrayList objectList) {
        delegate.processFinish(objectList);

    }

}
