package com.baraa.bsoft.mediaplayer.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

public class PlayService extends Service  implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{
    public static final String ACTION_IS_PLAYING = "action.isPlaying";
    public static final String DATA_IS_PLAYING = "isPlaying";
    public static final String ACTION_PLAY  = "action.play";
    public static final String DATA_URL = "url";
    private MediaPlayer mMediaPlayer;
    public PlayService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {
            String url = intent.getExtras().getString(DATA_URL);
            playStream(url);
        }



        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }

    private void toggleUiPlayPauseIcon(boolean isPlaying){
        Intent intent = new Intent(ACTION_IS_PLAYING);
        intent.putExtra(DATA_IS_PLAYING,isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
        super.onDestroy();
    }


    public void playStream(String url){
        if (mMediaPlayer !=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        if(mMediaPlayer != null){
            mMediaPlayer.start();
        }

    }
    public void pause(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }
    public void stop(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void toggle(){
        if(mMediaPlayer.isPlaying()){
            pause();
        }else {
            play();
        }
    }




    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
    @Override
    public void onPrepared(MediaPlayer player) {
        //player.start();
        play();
    }

}
