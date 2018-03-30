package com.baraa.bsoft.mediaplayer.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.baraa.bsoft.mediaplayer.Activities.MainActivity;

import java.io.IOException;

public class PlayService extends Service  implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{
    private static final String TAG = "PlayService";
    public static final String ACTION_IS_PLAYING = "action.isPlaying";
    public static final String DATA_IS_PLAYING = "isPlaying";
    public static final String ACTION_PLAY  = "action.play";
    public static final String DATA_URL = "url";
    private MediaPlayer mMediaPlayer;

    // notification vars
    private String mSubTitle = "",mTitle = "";
    int mImgRes = -1;


    private final IBinder mBinder = new PlayerBinder();

    public class PlayerBinder extends Binder {
        public PlayService getService(){
            return PlayService.this;
        }
    }


    public PlayService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //if (intent.getAction().equals(ACTION_PLAY)) {




        if (intent.getAction().equals(Constants.ACTION.ACTION_START_FOREGROUND)){
            if (intent != null){
                String url = intent.getExtras().getString(DATA_URL);
                mSubTitle = intent.getExtras().getString(Constants.NOTIFICATION_ID.SUD_TEXT);
                mTitle = intent.getExtras().getString(Constants.NOTIFICATION_ID.TITLE);
                mImgRes = intent.getExtras().getInt(Constants.NOTIFICATION_ID.IMG);
                playStream(url);
            }

            showNotification(mImgRes,mTitle,mSubTitle);
            Log.d(TAG, "onStartCommand: < Start foreground Service > ");
        }
        else if(intent.getAction().equals(Constants.ACTION.ACTION_PLAY)){
            toggle();
            toggleUiPlayPauseIcon(isPlaying());
            Log.d(TAG, "onStartCommand: < Action Play >");
        }
        else if(intent.getAction().equals(Constants.ACTION.ACTION_NEXT)){
            Log.d(TAG, "onStartCommand: < Action Next >");
        }
        else if(intent.getAction().equals(Constants.ACTION.ACTION_PREV)){
            Log.d(TAG, "onStartCommand: < Action Previous >");
        }
        else if(intent.getAction().equals(Constants.ACTION.ACTION_STOP_FOREGROUND)){
            Log.d(TAG, "onStartCommand: < Stop foreground Service >");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNotification(int resImg,String appTitle,String surahTitle) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.ACTION_MAIN);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Intent prevIntent = new Intent(this, PlayService.class);
        prevIntent.setAction(Constants.ACTION.ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getService(this,0,prevIntent,0);

        Intent playIntent = new Intent(this, PlayService.class);
        playIntent.setAction(Constants.ACTION.ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getService(this,0,playIntent,0);

        Intent nextIntent = new Intent(this, PlayService.class);
        nextIntent.setAction(Constants.ACTION.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this,0,nextIntent,0);

        // refresh play pause icon/text
        String strPlayPause = "Play";
        int iconPlayPause = android.R.drawable.ic_media_play;
        if( mMediaPlayer != null && isPlaying()){
            strPlayPause = "Pause";
            iconPlayPause = android.R.drawable.ic_media_pause;
        }



        Bitmap notificationImg = BitmapFactory.decodeResource(getResources(),resImg);
        Notification notification = new NotificationCompat.Builder(this,"101")
                .setContentText(appTitle)
                .setTicker("Listening to Quran")
                .setContentText(surahTitle)
                .setSmallIcon(resImg)
                .setLargeIcon(Bitmap.createScaledBitmap(notificationImg,100,100,false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous,"previous",prevPendingIntent)
                .addAction(iconPlayPause,strPlayPause, playPendingIntent)
                .addAction(android.R.drawable.ic_media_next,"next",nextPendingIntent)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return mBinder;
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
            toggleUiPlayPauseIcon(false);
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
        showNotification(mImgRes,mTitle,mSubTitle);
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
    @Override
    public void onPrepared(MediaPlayer player) {
        //player.start();
        toggleUiPlayPauseIcon(true);
        play();
    }


}
