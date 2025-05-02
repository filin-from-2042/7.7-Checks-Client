package com.tradeassistant1c77;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.Entites.User;
import com.tradeassistant1c77.dbconnection.ConnectionClass;

import java.sql.Connection;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // сохранение натсроек даты для дипазонов да в приложении
        String startDateKey = getString(R.string.dh_key_start_date);
        String endDateKey = getString(R.string.dh_key_end_date);
        Date startDate = (DataHolder.getData(startDateKey)!=null) ? (Date)DataHolder.getData(startDateKey) : null;
        Date endDate = (DataHolder.getData(endDateKey)!=null) ? (Date)DataHolder.getData(endDateKey) : null;
        if(startDate==null || endDate==null)
        {
            Date currDate = new Date();
            DataHolder.setData(startDateKey,currDate);
            DataHolder.setData(endDateKey,currDate);
        }

        super.onCreate(savedInstanceState);
        // если объект с данными пользователя потерялся отправляемся на перелогининвание
        if(DataHolder.getData("LoggedUser")==null){
            Connection connection = ConnectionClass.getConnection();
            if(!Helpers.reinitUserBySavedData(connection)){
                try{
                    connection.close();
                }catch (Exception ex){
                    Crashlytics.logException(ex);
                    ex.printStackTrace();
                }
                Helpers.redirectToLogin(this);
                return ;
            }
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView usernameTxt = (TextView) navigationView.getHeaderView(0).findViewById(R.id.usernameTxt);
        TextView stockNameTxt = (TextView) navigationView.getHeaderView(0).findViewById(R.id.stockNameTxt);
        if(DataHolder.getData("LoggedUser")!=null) {
            User usr = (User) DataHolder.getData("LoggedUser");
            usernameTxt.setText(usr.getUserName());
            stockNameTxt.setText(usr.getStockName());
        }
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        // если в стеке фаргментов уже есть очередь(что случается при повороте), выводим оттуда последний фрагмент, иначе список чеков
        List<Fragment> fmList = fragmentManager.getFragments();
        int titleID = 0;
        if(fmList!=null && fmList.size()>0){
            fragment = fmList.get(fmList.size()-1);
            if(fragment instanceof  ChecksListFragment)
                titleID = R.string.nm_check_list;
            else if(fragment instanceof  DbSystemFragment)
                titleID = R.string.nm_db;
        }
        else {
            Class fragmentClass = ChecksListFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                // Выводим выбранный пункт в заголовке
                titleID = R.string.nm_check_list;
                navigationView.getMenu().getItem(0).setChecked(true);
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        // Вставляем фрагмент, заменяя текущий фрагмент
        fragmentManager.beginTransaction().replace(R.id.content_container, fragment).commit();
        setTitle(titleID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
            fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount());
            List<Fragment> fmList = fragmentManager.getFragments();
            Fragment fragment = fmList.get(fragmentManager.getBackStackEntryCount());
            int titleID = 0;
            if(fragment instanceof  ChecksListFragment) {
                titleID = R.string.nm_check_list;
                navigationView.getMenu().getItem(0).setChecked(true);
            }
            else if(fragment instanceof  DbSystemFragment) {
                titleID = R.string.nm_db;
                navigationView.getMenu().getItem(1).setChecked(true);
            }
            setTitle(titleID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, MyPreferenceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_check_list) {
            fragmentClass = ChecksListFragment.class;
        }
        if (id == R.id.nav_db_system) {
            fragmentClass = DbSystemFragment.class;
        }
        if (id == R.id.nav_privacy_policy){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1pMQAx_Q1xbIBB5bLywsb7hBGj8zvs2bxaqEPnRL9nD4/edit?usp=sharing"));
            startActivity(browserIntent);
            return true;
        }
        if (id == R.id.nav_exit) {
            // выход из учетной записи
            DataHolder.setData("LoggedUser",null);
            SharedPreferences sharedPref = getSharedPreferences("Auth", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            Helpers.redirectToLogin(this);
            return true;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        // Вставляем фрагмент, заменяя текущий фрагмент
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        // Выделяем выбранный пункт меню в шторке
        item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
