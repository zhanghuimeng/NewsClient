package com.java.no_36;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.app.SearchManager;
import android.support.v7.widget.SearchView;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    /* add by lwt */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // auto-gen start
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = getSharedPreferences("config", MODE_PRIVATE);
            int themeId = getThemeId();
            if (themeId != 0) {
                setTheme(themeId);
            }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        initViewPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // auto-gen end

        // 设置CommonUtils的内容
        CommonUtils.setContext(getApplicationContext());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_bar, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.bar_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_search) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_collect) {
            Intent intent = new Intent(this, CollectPage.class);
            startActivity(intent);
        } else if (id == R.id.nav_night) {
            boolean isNightMode = getMode();
            isNightMode = !isNightMode;
            setMode(isNightMode);
            int themeId;
            if (isNightMode) {
                themeId = R.style.APPTheme_NightTheme;
            }else {
                themeId = R.style.APPTheme_DayTheme;
            }

            setThemeId(themeId);
            this.recreate();

        } else if (id == R.id.nav_text) {
            boolean isTextMode = CommonUtils.getTextMode();
            isTextMode = !isTextMode;
            CommonUtils.setTextMode(isTextMode);
        } else if (id == R.id.nav_shield) {

        } else if(id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryPage.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        List<String> titles = new ArrayList();
        List<Fragment> fragments = new ArrayList<>();

        titles.add("推荐");
        titles.add("分类");

        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        fragments.add(new BoxBaseFragment());

        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        fragments.add(new ClassifyFragment());


        FragmentAdapter mFragmentAdapteradapter =
                new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapteradapter);
        //将TabLayout和ViewPager关联起来。
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setThemeId(int themeId) {
        SharedPreferences.Editor editor = config.edit();
        editor.putInt("theme_id", themeId);
        editor.commit();
    }

    private int getThemeId() {
        return config.getInt("theme_id", 0);
    }
    private void setMode(boolean isNightMode) {
        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean("is_night_mode", isNightMode);
        editor.commit();
    }

    private boolean getMode() {
        return config.getBoolean("is_night_mode", false);
    }



}
