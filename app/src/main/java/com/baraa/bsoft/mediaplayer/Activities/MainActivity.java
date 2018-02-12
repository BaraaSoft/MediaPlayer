package com.baraa.bsoft.mediaplayer.Activities;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.baraa.bsoft.mediaplayer.DataAccess.DAL;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;
import com.baraa.bsoft.mediaplayer.Views.ProgressHelper;
import com.baraa.bsoft.mediaplayer.Views.SurahAdapter;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import mbanje.kurt.fabbutton.FabButton;

public class MainActivity extends AppCompatActivity implements SurahAdapter.PlayListListener,View.OnClickListener {
    private static final String TAG = "MainActivity";
    private RealmResults<Surah> surahs;
    private ListView lvClips;
    private MediaPlayer mediaPlayer;
    private FabButton lastPlayButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Realm Database:
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();
        Realm.setDefaultConfiguration(config);



        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        ImageButton btnPlay = findViewById(R.id.btnPlay);
        ImageButton btnforward = findViewById(R.id.btnForward);
        ImageButton btnbackward = findViewById(R.id.btnBackward);
        btnPlay.setOnClickListener(this);
        btnforward.setOnClickListener(this);
        btnbackward.setOnClickListener(this);

        builSurahList("1");
        surahs = DAL.getInstance().setContext(this).getAllSurah(); //builSurahList("1");
        lvClips = (ListView)findViewById(R.id.lvClips);
        SurahAdapter surahAdapter = new SurahAdapter(this,R.layout.list_item,surahs);
        surahAdapter.setmPlayListListener(this);
        lvClips.setMinimumHeight(200);
        lvClips.setAdapter(surahAdapter);

        mediaPlayer = new MediaPlayer();
        for (Surah item:DAL.getAllSurah()) {
            Log.d(TAG, "onCreate: Data In Realm \n"+item.getUrl());
        }


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
        }
        DAL.getInstance().setContext(this).InsertListToDB(surahs);
        return surahs;
    }

    @Override
    public void onItemListClicked(Surah surah, int index, final View view) {
        final int x = index;
        final ProgressHelper progressHelper = new ProgressHelper((FabButton) view,this);
        progressHelper.startIndeterminate();
        final String surahUrl = surahs.get(x).getUrl();
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
                //Log.d(TAG, "onItemListClicked: "+surahs.get(x).getUrl());
                try {
                    mediaPlayer = new MediaPlayer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastPlayButton = (FabButton)view;
                            ((FabButton)view).setIcon(R.drawable.ic_pause_circle_outline_white_24dp,R.drawable.ic_pause_circle_outline_white_24dp);
                        }
                    });
                    mediaPlayer.setDataSource(surahUrl);
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
}
