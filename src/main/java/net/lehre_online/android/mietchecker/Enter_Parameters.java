package net.lehre_online.android.mietchecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class represents an activity to enter parameters in text fields to filter
 * the map accordingly.
 *
 * @author Michael Kaleve, Eric Walter
 * @version 1.1, 2019-07-07
 */
public class Enter_Parameters extends AppCompatActivity {

    String zip_code, max_square, min_square, max_rent, min_rent;

    /**
     * This method initializes the Enter_Parameters activity
     *
     * @param savedInstanceState The Bundle to save & recover state information for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_parameters);

        Button back_button = findViewById(R.id.bt_back);

        back_button.setOnClickListener(v -> finish());

    }

    /**
     * This method overrides the finish() method to save the input from the text fields
     * into variables and put the value into the extra of an intent to deliver to data
     * back to the main activity
     */
    @Override
    public void finish() {

        // Pass data back
        //Intent intent = getIntent();
        Intent intent = new Intent();

        // Extract data from UI
        EditText editZip = findViewById(R.id.zip_code);
        zip_code = editZip.getText().toString();
        EditText editMxSqm = findViewById(R.id.max_square);
        max_square = editMxSqm.getText().toString();
        EditText editMiSqm = findViewById(R.id.min_square);
        min_square = editMiSqm.getText().toString();
        EditText editMxRe = findViewById(R.id.max_rent);
        max_rent = editMxRe.getText().toString();
        EditText editMiRe = findViewById(R.id.min_rent);
        min_rent = editMiRe.getText().toString();

        // load intent with data
        intent.putExtra("zipCode", zip_code);
        intent.putExtra("max_square", max_square);
        intent.putExtra("min_square", min_square);
        intent.putExtra("max_rent", max_rent);
        intent.putExtra("min_rent", min_rent);
        // setResult(Activity.RESULT_OK, intent);
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }
}


