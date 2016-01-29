package ru.anrad.p001;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.anrad.p001.core.Duty;
import ru.anrad.p001.core.DutyDataSource;
import ru.anrad.p001.core.DutyStateDialog;

public class ViewItemActivity extends AppCompatActivity
    implements DutyStateDialog.DutyStateDialogListener {

    private Duty duty;
    private TextView who;
    private TextView when;
    private TextView what;
    private TextView note;
    private TextView timestamp;
    private FloatingActionButton fabEdit;
    private Button bDutyStateDialog;
    private AppBarLayout appBarLayout;

    private boolean isUpdated = false;

    static private final int REQUEST_CODE_EDIT_ITEM = 1;

    public void onSetDuty(Duty duty) {
        DutyDataSource.getInstance(getApplicationContext()).updateItem(duty);
        setDuty();
    }
    public Duty onGetDuty() {
        return duty;
    }


    static class WhenDateFormat {
        static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM y");
        static public String toString (Date d) {
            if (d !=null) {
                return DATE_FORMAT.format(d);
            }
            else {
                return "";
            }
        }
    }

    private void setDuty() {
        if (duty.isActive()) { getSupportActionBar().setTitle("Активные"); }
        else if (duty.isArhive()) {getSupportActionBar().setTitle("Архив"); }
        else if (duty.isTrash()) {getSupportActionBar().setTitle("Корзина"); }

        what.setText(duty.getWhat());
        who.setText(duty.getWho());
        when.setText(WhenDateFormat.toString(duty.getWhen()));
        note.setText(duty.getNote());
        timestamp.setText(WhenDateFormat.toString(duty.getTimestamp()));

        if (duty.isActive()) {
            CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            p.anchorGravity = Gravity.BOTTOM | Gravity.END;
            p.setAnchorId(R.id.app_bar);
            fabEdit.setLayoutParams(p);
            fabEdit.setVisibility(View.VISIBLE);
        }
        else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabEdit.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fabEdit.setLayoutParams(p);
            fabEdit.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MyApp:ViewItemActivity", "onCreate");
        setContentView(R.layout.activity_view_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        String uuid = intent.getStringExtra("UUID");
        Log.v("ViewItemActivity", "onCreate: UUID= " + uuid);

        what = (TextView) findViewById(R.id.activity_view_item_what);
        who = (TextView) findViewById(R.id.activity_view_item_who);
        when = (TextView) findViewById(R.id.activity_view_item_when);
        note = (TextView) findViewById(R.id.activity_view_item_note);
        timestamp = (TextView) findViewById(R.id.activity_view_item_timestamp);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        fabEdit = (FloatingActionButton) findViewById(R.id.activity_view_item_edit_button);
        fabEdit.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "selected:" + adapter.getSelectedDuty().toString(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Intent intent = new Intent(view.getContext(), EditItemActivity.class);
                    intent.putExtra("UUID", duty.getUUID());
                    startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM);
                }
            }
        );


        duty = DutyDataSource.getInstance(getApplicationContext()).getItem(uuid);
        Log.v("ViewItemActivity", "OnCreate: Duty= " + duty.toString());
        setDuty();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (data == null) {return;}
        //String who = data.getStringExtra("who");
        //String what = data.getStringExtra("what");
        Log.v("MyApp:ViewItemActivity", "OnActivityResult" );

        if (requestCode == REQUEST_CODE_EDIT_ITEM && resultCode == EditItemActivity.RESULT_OK) {
            Snackbar.make(findViewById(R.id.activity_view_item_who), "Данные обновлены", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            Log.v("ViewItemActivity", "OnActivityResult Duty= " + duty.toString());
            setDuty();
            isUpdated = true;
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("UUID", duty.getUUID());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_view_item_toolbar_menu, menu);
        return super.onPrepareOptionsMenu(menu);
        //MenuItem item = menu.findItem(R.id.activ);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (duty.isActive()) {
            menu.findItem(R.id.activity_view_item_menu_to_active).setVisible(false);
            menu.findItem(R.id.activity_view_item_menu_to_arhive).setVisible(true);
            menu.findItem(R.id.activity_view_item_menu_to_trash).setVisible(true);
        }
        else if (duty.isArhive()) {
            menu.findItem(R.id.activity_view_item_menu_to_active).setVisible(true);
            menu.findItem(R.id.activity_view_item_menu_to_arhive).setVisible(false);
            menu.findItem(R.id.activity_view_item_menu_to_trash).setVisible(true);
        }
        else if (duty.isTrash()) {
            menu.findItem(R.id.activity_view_item_menu_to_active).setVisible(true);
            menu.findItem(R.id.activity_view_item_menu_to_arhive).setVisible(true);
            menu.findItem(R.id.activity_view_item_menu_to_trash).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isUpdated) {
                Log.v("MyApp:ViewItemActivity", "onOptionsItemSelected:android.R.id.home && isUpdated");
                setResult(RESULT_OK);
            }
            else {
                setResult(RESULT_CANCELED);
            }
            finish();
            return true;
        }
        if (id == R.id.activity_view_item_menu_to_active) {
            Log.v("MyApp:ViewItemActivity", "activity_view_item_menu_to_active");
            duty.setActive();
            DutyDataSource.getInstance(getApplicationContext()).updateItem(duty);
            setDuty();
            isUpdated = true;
            return true;
        }
        if (id == R.id.activity_view_item_menu_to_arhive) {
            Log.v("MyApp:ViewItemActivity", "activity_view_item_menu_to_arhive");
            duty.setArhive();
            DutyDataSource.getInstance(getApplicationContext()).updateItem(duty);
            setDuty();
            isUpdated = true;
            return true;
        }
        if (id == R.id.activity_view_item_menu_to_trash) {
            Log.v("MyApp:ViewItemActivity", "activity_view_item_menu_to_trash");
            duty.setTrash();
            DutyDataSource.getInstance(getApplicationContext()).updateItem(duty);
            setDuty();
            isUpdated = true;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
