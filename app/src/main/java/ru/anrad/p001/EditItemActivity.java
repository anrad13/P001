package ru.anrad.p001;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.anrad.p001.core.Duty;
import ru.anrad.p001.core.DutyDataSource;

public class EditItemActivity
        extends AppCompatActivity
        implements DatePickerDialog.DatePickerDialogListener {

    private Duty duty;
    private EditText whatEditText;
    private EditText whoEditText;
    private TextView whenEditText;

    private DatePickerDialog dialog;

    public void onSetDate(@NonNull Date date) {
        duty.setWhen(date);
        whenEditText.setText(WhenDateFormat.toString(duty.getWhen()));
    }
    public Date onGetDate() {
        return duty.getWhen();
    }

    static class WhenDateFormat {
        static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM y");
        public static String toString (Date d) {
            if (d != null) {
                return DATE_FORMAT.format(d);
            }
            else {
                return "";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Правка");

        Intent intent = getIntent();
        String uuid = intent.getStringExtra("UUID");

        duty = DutyDataSource.getInstance(getApplicationContext()).getItem(uuid);
        Log.v("ViewItemActivity", "Input Duty: " + duty.toString());

        whatEditText = (EditText) findViewById(R.id.activity_edit_item_what);
        whoEditText = (EditText) findViewById(R.id.activity_edit_item_who);
        whenEditText = (TextView) findViewById(R.id.activity_edit_item_when);

        whatEditText.setText(duty.getWhat());
        whoEditText.setText(duty.getWho());
        whenEditText.setText(WhenDateFormat.toString(duty.getWhen()));

        dialog = new DatePickerDialog();

        whenEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show(getFragmentManager(), "dialog");
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_item_toolbar_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.v("MyApp:EditItemActivity", "onOptionsItemSelected:android.R.id.home");
            finish();
            return true;
        }

        if (id == R.id.activity_edit_item_menu_save) {
            if (whatEditText.getText().toString().trim().isEmpty()) {
                Snackbar.make(getCurrentFocus(), "Пустое значение действия. Не могу сохранить", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            }
            else {
                duty.setWhat(whatEditText.getText().toString().trim());
            }

            if (whoEditText.getText().toString().trim().isEmpty()) {
                duty.setWho(null);
            }
            else {
                duty.setWho(whoEditText.getText().toString().trim());
            }

            DutyDataSource ds = DutyDataSource.getInstance(getApplicationContext());
            ds.updateItem(duty);

            Snackbar.make(getCurrentFocus(), "Обновлено", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            setResult(RESULT_OK, null);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
