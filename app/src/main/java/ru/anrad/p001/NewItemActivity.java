package ru.anrad.p001;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.anrad.p001.core.Duty;
import ru.anrad.p001.core.DutyDataSource;

public class NewItemActivity
        extends AppCompatActivity
        implements DatePickerDialog.DatePickerDialogListener {

    EditText whatEditText;
    EditText whoEditText;
    EditText whenEditText;

    Date when;
    DatePickerDialog dialog;

    public void onSetDate(Date d) {
        when = d;
        whenEditText.setText(WhenDateFormat.toString(when));
    }
    public Date onGetDate() {
        return when;
    }

    static class WhenDateFormat {
        static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM y");
        public static String toString (Date d) {
            if (d !=null) {
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
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Новое");

        whatEditText = (EditText) findViewById(R.id.activity_new_item_what);
        whoEditText = (EditText) findViewById(R.id.activity_new_item_who);
        whenEditText = (EditText) findViewById(R.id.activity_new_item_when);

        dialog = new DatePickerDialog();
        when = null;

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
        getMenuInflater().inflate(R.menu.activity_new_item_toolbar_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.v("MyApp:NewItemActivity", "onOptionsItemSelected:android.R.id.home");
        }
        if (id == R.id.new_item_save) {
            String what =  whatEditText.getText().toString().trim();
            if (what.isEmpty()) {
                Snackbar.make(whatEditText, "Пустое значение действия. Не могу сохранить", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            }
            Duty duty = new Duty(what);

            String who = whoEditText.getText().toString().trim();
            if (who.isEmpty()) {
                who = null;
            }
            duty.setWho(who);
            duty.setWhen(when);

            DutyDataSource ds = DutyDataSource.getInstance(getApplicationContext());
            ds.putItem(duty);

            //Intent intent = new Intent();
            //intent.putExtra("what", whatEditText.getText().toString());
            //intent.putExtra("who", whoEditText.getText().toString());
            //intent.put
            Snackbar.make(whatEditText, "Действие добавлено", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            setResult(RESULT_OK, null);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
