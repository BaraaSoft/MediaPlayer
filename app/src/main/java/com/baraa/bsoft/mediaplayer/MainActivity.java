package com.baraa.bsoft.mediaplayer;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.Views.SurahAdapter;

import java.io.IOException;
import java.util.ArrayList;

import mbanje.kurt.fabbutton.FabButton;

public class MainActivity extends AppCompatActivity implements SurahAdapter.PlayListListener,View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ArrayList<Surah> surahs;
    private ListView lvClips;
    private MediaPlayer mediaPlayer;
    private FabButton lastPlayButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        ImageButton btnPlay = findViewById(R.id.btnPlay);
        ImageButton btnforward = findViewById(R.id.btnForward);
        ImageButton btnbackward = findViewById(R.id.btnBackward);
        btnPlay.setOnClickListener(this);
        btnforward.setOnClickListener(this);
        btnbackward.setOnClickListener(this);

        surahs = new ArrayList<Surah>();
        surahs = builSurahList();
        lvClips = (ListView)findViewById(R.id.lvClips);
        SurahAdapter surahAdapter = new SurahAdapter(this,R.layout.list_item,surahs);
        surahAdapter.setmPlayListListener(this);
        lvClips.setMinimumHeight(200);
        lvClips.setAdapter(surahAdapter);

        mediaPlayer = new MediaPlayer();
        for (Surah item:surahs
             ) {
            Log.d(TAG, "onCreate: \n"+item.getUrl());
        }


    }

    public ArrayList<Surah> builSurahList(){
        ArrayList<Surah> surahs = new ArrayList<>();
        //"https://quran.islamway.net/quran3/324/001.mp3";
        String baseUrl = getResources().getString(R.string.base_url);
        String[] surahArry = getResources().getStringArray(R.array.surahs);
        String decm = String.format("%03d",0);
        for (int i=0;i<surahArry.length;i++){
            StringBuilder strBuilder = new StringBuilder(baseUrl);
            String b = String.format("%03d",i+1);
            String x = strBuilder.append(b).append(".mp3").toString();
            Surah item = new Surah(surahArry[i],x,i);
            Log.d(TAG, "builSurahList:"+x);
            surahs.add(item);
        }
        return surahs;
    }

    @Override
    public void onItemListClicked(Surah surah, int index, final View view) {
        final int x = index;
        final ProgressHelper progressHelper = new ProgressHelper((FabButton) view,this);
        progressHelper.startIndeterminate();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer !=null){
                    mediaPlayer.stop();
                    if (lastPlayButton != null){
                        lastPlayButton.setIcon(R.drawable.ic_play_circle_outline_white_24dp,R.drawable.ic_play_circle_outline_white_24dp);
                    }
                }
                Log.d(TAG, "onItemListClicked: "+surahs.get(x).getUrl());
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(surahs.get(x).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    progressHelper.stopIndeterminate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastPlayButton = (FabButton)view;
                            ((FabButton)view).setIcon(R.drawable.ic_pause_circle_outline_white_24dp,R.drawable.ic_pause_circle_outline_white_24dp);
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
