package ru.anrad.p001;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ru.anrad.p001.core.Duty;
import ru.anrad.p001.core.DutyDataSource;
import ru.anrad.p001.core.RVAdapter;


public class MainActivity
        extends AppCompatActivity
        implements RVAdapter.ItemListener,
                    NavigationView.OnNavigationItemSelectedListener {

    public void onItemClick(Duty duty) {
        Log.v("MainActivity", "On Click Item Duty: " + duty.toString());
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra("UUID", duty.getUUID());
        startActivityForResult(intent, REQUEST_CODE_VIEWITEM);
    }

    final int REQUEST_CODE_NEWITEM = 1;
    final int REQUEST_CODE_VIEWITEM = 2;

    private Toolbar toolbar;
    private RVAdapter adapter;
    private DutyDataSource ds;
    private int listName = DutyDataSource.ACTIVE_AGENDA;
    private String listCaption = "Повестка";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.v("MyApp:MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Snackbar.make(view, "selected:" + adapter.getSelectedDuty().toString(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        Intent intent = new Intent(view.getContext(), NewItemActivity.class);
                        //intent.putExtra("ID", adapter.get)
                        startActivityForResult(intent, REQUEST_CODE_NEWITEM);
                    }
                }
        );
        //-------------------------------
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Dynamic content ----------------------------------
        //toolbar.setTitle(listCaption);

        DutyDataSource.getInstance(this);
        ds = DutyDataSource.getInstance(getApplicationContext());
        ds.clearTrash();
        createItemsList(DutyDataSource.ACTIVE_AGENDA);
        //adapter = new RVAdapter(ds.getItemsList(listName), this);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {
            setItemsList(DutyDataSource.ACTIVE_AGENDA);
        } else if (id == R.id.nav_inbox) {
            setItemsList(DutyDataSource.ACTIVE_INBOX);
        } else if (id == R.id.nav_assigned) {
            setItemsList(DutyDataSource.ACTIVE_ASSIGNED);
        } else if (id == R.id.nav_schedule) {
            setItemsList(DutyDataSource.ACTIVE_SHEDULE);
        } else if (id == R.id.nav_arhive) {
            setItemsList(DutyDataSource.ARHIVE);
        } else if (id == R.id.nav_trash) {
            setItemsList(DutyDataSource.TRASH);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (data == null) {return;}
        //String who = data.getStringExtra("who");
        //String what = data.getStringExtra("what");
        Log.v("MyApp:MainActivity", "onActivityResult");
        if (requestCode == REQUEST_CODE_NEWITEM && resultCode == RESULT_OK) {
            setItemsList(DutyDataSource.ACTIVE_AGENDA);
        }
        if (requestCode == REQUEST_CODE_VIEWITEM && resultCode == RESULT_OK) {
            updateItemsList();
        }

    }

    private void createItemsList(int _listName) {
        listName = _listName;
        listCaption = getListCaption(listName);
        toolbar.setTitle(listCaption);
        adapter = new RVAdapter(ds.getItemsList(listName), this);
        if (adapter.getItemCount() == 0) {
            Snackbar.make(findViewById(R.id.recyclerView), "Список пуст", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void setItemsList(int _listName) {
        listName = _listName;
        listCaption = getListCaption(listName);
        updateItemsList();
    }

    private void updateItemsList() {
        toolbar.setTitle(listCaption);
        adapter.updateItems(ds.getItemsList(listName));
        if (adapter.getItemCount() == 0) {
            Snackbar.make(findViewById(R.id.recyclerView), "Список пуст", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(findViewById(R.id.recyclerView), "Список обновлен", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    private String getListCaption(int _listName) {
        switch (_listName) {
            case DutyDataSource.ACTIVE_AGENDA:
                return "Повестка";
            case DutyDataSource.ACTIVE_SHEDULE:
                return "Расписание";
             case DutyDataSource.ACTIVE_ASSIGNED:
                return "Назначено";
            case DutyDataSource.ACTIVE_INBOX:
                return "Входящее";
            case DutyDataSource.TRASH:
                return "Корзина";
            case DutyDataSource.ARHIVE:
                return "Архив";
            default:
                return "Error";
        }
    }
}


