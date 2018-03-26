package com.baraa.bsoft.mediaplayer.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baraa.bsoft.mediaplayer.DataAccess.DAL;
import com.baraa.bsoft.mediaplayer.DataAccess.DataBuilder;
import com.baraa.bsoft.mediaplayer.Model.Artist;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;
import com.baraa.bsoft.mediaplayer.Services.PlayService;
import com.baraa.bsoft.mediaplayer.Views.NavAdapter;
import com.baraa.bsoft.mediaplayer.Views.ProgressHelper;
import com.baraa.bsoft.mediaplayer.Views.SurahAdapter;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mbanje.kurt.fabbutton.FabButton;


/*
* unregister the LocalBroadcast dynamically:
* MyApplication.getInstance().getApplicationContext().registerReceiver(sentReceiver, new IntentFilter(SENT));
* MyApplication.getInstance().getApplicationContext().unregisterReceiver(this);
*
**/


public class MainActivity extends AppCompatActivity implements SurahAdapter.PlayListListener,View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,NavAdapter.NavListener{
    private static final String TAG = "MainActivity";
    private ArrayList<Surah> mSurahs;
    private ListView lvClips;
    private MediaPlayer mediaPlayer;
    private FabButton lastPlayButton;

    private ArrayList<Artist> mArtists;

    private Context mContext=MainActivity.this;
    private static final int REQUEST = 101;
    private SurahAdapter mSurahAdapter;
    private DataBuilder mDataBuilder;

    private PlayService mBoundService;
    private boolean isServiceBound;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayService.PlayerBinder playerBinder = (PlayService.PlayerBinder) iBinder;
            mBoundService = playerBinder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Realm Database:
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();
        Realm.setDefaultConfiguration(config);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDataBuilder = new DataBuilder(this);
        mArtists = mDataBuilder.getAllArtists();
        ListView lvNav = (ListView) findViewById(R.id.lvNav);
        NavAdapter adapter = new NavAdapter(this,R.layout.nav_item,mArtists,this);
        lvNav.setAdapter(adapter);









        //
        //setContentView(R.layout.activity_main);
        ImageButton btnPlay = findViewById(R.id.btnPlay);
        ImageButton btnforward = findViewById(R.id.btnForward);
        ImageButton btnbackward = findViewById(R.id.btnBackward);
        btnPlay.setOnClickListener(this);
        btnforward.setOnClickListener(this);
        btnbackward.setOnClickListener(this);

        builSurahList("1");
        Object[] arry =  DAL.getInstance().setContext(this).getAllSurah().toArray();
        mSurahs = mDataBuilder.builSurahList(mArtists.get(0)); //toArraySurah(arry); //(ArrayList<Surah>) Arrays.asList(arry); //builSurahList("1");
        lvClips = (ListView)findViewById(R.id.lvClips);
        mSurahAdapter = new SurahAdapter(this,R.layout.list_item, mSurahs,lvClips);
        mSurahAdapter.setmPlayListListener(this);
        lvClips.setMinimumHeight(200);
        lvClips.setAdapter(mSurahAdapter);

        mediaPlayer = new MediaPlayer();
        for (Surah item:DAL.getAllSurah()) {
            Log.d(TAG, "onCreate: Data In Realm \n"+item.getUrl());
        }
    }


    public ArrayList<Surah> toArraySurah(Object[] objects){
        ArrayList<Surah> surahs = new ArrayList<>();
        for (Object object: objects){
            surahs.add((Surah) object);
        }
        return surahs;
    }


    public ArrayList<Surah> builSurahList(String artistKey){
        ArrayList<Surah> surahs = new ArrayList<>();
        //"https://quran.islamway.net/quran3/324/001.mp3";
        String baseUrl = getResources().getString(R.string.base_url);
        String[] surahArry = getResources().getStringArray(R.array.surahs);
        String decm = String.format("%03d",0);
        for (int i=0;i<surahArry.length;i++){
            StringBuilder strBuilder = new StringBuilder(baseUrl);
            String b = String.format("%03d",i+1);
            String x = strBuilder.append(b).append(".mp3").toString();
            Surah item = new Surah(surahArry[i],x,i,artistKey);
            surahs.add(item);
            if(mSurahAdapter != null){
                mSurahAdapter.notifyDataSetChanged();
            }
        }
        DAL.getInstance().setContext(this).insertListToDB(surahs);
        return surahs;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registeringReceiver();
    }



    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiverIsPlaying);
    }

    BroadcastReceiver mReceiverIsPlaying = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PlayService.ACTION_IS_PLAYING)){
                boolean isPlaying = intent.getExtras().getBoolean(PlayService.DATA_IS_PLAYING);
            }

        }
    };
    private void registeringReceiver(){
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiverIsPlaying,new IntentFilter(PlayService.ACTION_IS_PLAYING));
    }


    private void startMediaPlayerService(String url){
        Intent intent = new Intent(this,PlayService.class);
        intent.putExtra(PlayService.DATA_URL,url);
        intent.setAction(PlayService.ACTION_PLAY);
        bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isServiceBound){
            unbindService(mServiceConnection);
            isServiceBound = false;
        }
    }

    @Override
    public void onItemListClicked(Surah surah, int index, final FabButton view) {
        final int x = index;
        final ProgressHelper progressHelper = new ProgressHelper((FabButton) view,this,surah.getKey());
        progressHelper.startIndeterminate();
        final String surahUrl = mSurahs.get(x).getUrl();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer !=null){
                    mediaPlayer.stop();
                    if (lastPlayButton != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lastPlayButton.setIcon(R.drawable.ic_play_circle_outline_white_24dp,R.drawable.ic_play_circle_outline_white_24dp);
                            }
                        });
                    }
                }
                //Log.d(TAG, "onItemListClicked: "+mSurahs.get(x).getUrl());
                try {
                    mediaPlayer = new MediaPlayer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastPlayButton = (FabButton)view;
                            ((FabButton)view).setIcon(R.drawable.ic_pause_circle_outline_white_24dp,R.drawable.ic_pause_circle_outline_white_24dp);
                        }
                    });
                    mediaPlayer.setDataSource(surahUrl);// changed
                    //mediaPlayer.setDataSource("https://quran.islamway.net/quran3/101/15276/32/077.mp3");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    progressHelper.stopIndeterminate();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if(lastPlayButton != null)
                                lastPlayButton.setIcon(R.drawable.ic_play_circle_outline_white_24dp,R.drawable.ic_play_circle_outline_white_24dp);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDownloadClicked(Surah surah, int index, FabButton view) {
        Log.d(TAG, "onDownloadClicked: >> "+surah.getTitle()+":"+surah.getKey());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlay:
                break;
            case R.id.btnForward:
                break;
            case R.id.btnBackward:
                break;
        }

    }




    public boolean checkStoragePermissionBeforeAccess(){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                return false;
            } else {
                //do here
                return true;

            }
        } else {
            //do here
            return true;
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
        mSurahAdapter.clear();
        mSurahs = mDataBuilder.builSurahList(artist);
        mSurahAdapter.updateData(mSurahs);


        ImageView imgSeletedShk = findViewById(R.id.imgSelectedShk);
        TextView tvSelectedShk = findViewById(R.id.tvSelectedShk);
        imgSeletedShk.setImageDrawable(getDrawable(artist.getImageResourceId()));
        tvSelectedShk.setText(artist.getName());
    }
}
