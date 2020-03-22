package net.lehre_online.android.mietchecker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import static net.lehre_online.android.mietchecker.GeoCoderHelper.getLocationFromAddress;

/**
 * This class represents an activity to enter information into text fields to add
 * a new flat to the database.
 *
 * @author Michael Kaleve, Eric Walter
 * @version 1.1, 2019-07-07
 */
public class AddFlat extends AppCompatActivity {

    private static final String TAG = ".AddFlat";
    private static final boolean DBG = true;
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText editFreeDate;
    private String cityName, freeFrom,
            o_rent, o_address, o_houseNr, o_tel, o_qm, o_rooms, o_zip, sAddress;
    private double o_lat, o_long;
    private java.sql.Date sqlDate;
    private LatLng position;

    /**
     * This method updates the text of an input field with the date picked in a calender
     */
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        editFreeDate.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * This method initializes the the AddFlat activity
     *
     * @param savedInstanceState The Bundle to save & recover state information for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flat);

        Button back_button = findViewById(R.id.bt_back_flat);
        Button submit_button = findViewById(R.id.bt_submit_flat);
        editFreeDate = findViewById(R.id.freeDate);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        editFreeDate.setOnClickListener(v -> {
            new DatePickerDialog(AddFlat.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        submit_button.setOnClickListener(v -> {
            // Extract data from UI
            EditText editStreet = findViewById(R.id.streetName);
            o_address = editStreet.getText().toString();
            EditText editNr = findViewById(R.id.houseNr);
            o_houseNr = editNr.getText().toString();
            EditText editCity = findViewById(R.id.cityName);
            cityName = editCity.getText().toString();
            EditText editZip = findViewById(R.id.plzNr);
            o_zip = editZip.getText().toString();
            EditText editSize = findViewById(R.id.flatSize);
            o_qm = editSize.getText().toString();
            EditText editRent = findViewById(R.id.rentPrice);
            o_rent = editRent.getText().toString();
            EditText editRooms = findViewById(R.id.flatRooms);
            o_rooms = editRooms.getText().toString();
            EditText editTel = findViewById(R.id.telNr);
            o_tel = editTel.getText().toString();
            freeFrom = editFreeDate.getText().toString();
            sqlDate = java.sql.Date.valueOf(freeFrom);
            sAddress = o_address + " " + o_houseNr + ", " + o_zip + " " + cityName;

            position = getLocationFromAddress(this, sAddress);
            o_lat = position != null ? position.longitude : 0;
            o_long = position != null ? position.latitude : 0;

            // call inserData to insert into database
            AsyncTask asyncTask = new SqlInsert(o_lat, o_long, o_rooms, o_rent, o_qm,
                    o_tel, sqlDate, o_address, o_houseNr, o_zip).execute();
            Intent intent = new Intent();
            intent.putExtra("zipCode", o_zip);
            intent.putExtra("city", cityName);
            setResult(Activity.RESULT_OK, intent);

            Toast.makeText(getBaseContext(), "Data was successfully transmitted!", Toast.LENGTH_LONG).show();
            finish();
        });
        back_button.setOnClickListener(v -> finish());
    }
}
