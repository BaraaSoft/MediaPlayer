package com.baraa.bsoft.mediaplayer.Activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.baraa.bsoft.mediaplayer.DataAccess.DataBuilder;
import com.baraa.bsoft.mediaplayer.Model.Artist;
import com.baraa.bsoft.mediaplayer.R;
import com.baraa.bsoft.mediaplayer.Views.NavAdapter;

import java.util.ArrayList;

public class MainActivityDisplay extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,NavAdapter.NavListener {


    private ArrayList<Artist> mArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DataBuilder dataBuilder = new DataBuilder(this);
        mArtists = dataBuilder.getAllArtists();
        ListView lvNav = (ListView) findViewById(R.id.lvNav);
        NavAdapter adapter = new NavAdapter(this,R.layout.nav_item,mArtists,this);
        lvNav.setAdapter(adapter);

    }



    private ArrayList<Artist> createArtistsList(){
        ArrayList<Artist> artists = new ArrayList<>();
        artists.add(new Artist("1",R.drawable.shk_muhammad_abdulkareem_bezzy,getString(R.string.shk_muhammad_abdulkareem)));
        artists.add(new Artist("2",R.drawable.shk_mishary_rashed_alafasy,getString(R.string.shk_alafasy)));
        return artists;
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
        getMenuInflater().inflate(R.menu.main_activity_display, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shk_muhammad_abdulkareem) {
            // Handle the camera action
        } else if (id == R.id.nav_shk_alafasy) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNavClicked(Artist artist) {

    }
}
