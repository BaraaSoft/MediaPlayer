package com.baraa.bsoft.mediaplayer.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.baraa.bsoft.mediaplayer.Activities.MainActivity;
import com.baraa.bsoft.mediaplayer.DataAccess.DAL;
import com.baraa.bsoft.mediaplayer.Model.CurrentMedia;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;

import java.io.IOException;

public class PlayService extends Service  implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{
    private static final String TAG = "PlayService";
    public static final String ACTION_IS_PLAYING = "action.isPlaying";
    public static final String DATA_IS_PLAYING = "isPlaying";
    public static final String ACTION_PLAY  = "action.play";
    public static final String DATA_URL = "url";
    private MediaPlayer mMediaPlayer;
    private int mSoundVolume = 0;
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
        
        if( intent == null ||intent.getAction() == null){
            Log.d(TAG, "onStartCommand: NO ACTION FOR INTENT");
            return START_STICKY;
        }

        if (intent.getAction().equals(Constants.ACTION.ACTION_START_FOREGROUND)){
            if (intent != null){
                String url = intent.getExtras().getString(DATA_URL);
                mSubTitle = intent.getExtras().getString(Constants.NOTIFICATION_ID.SUD_TEXT);
                mTitle = intent.getExtras().getString(Constants.NOTIFICATION_ID.TITLE);
                mImgRes = intent.getExtras().getInt(Constants.NOTIFICATION_ID.IMG);
                playStream(url,false);
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
            CurrentMedia media = DAL.getInstance().setContext(getApplicationContext()).getCurrentMedia();
            int currentIndex = media.getIndex();
            Log.d(TAG, "onStartCommand:"+currentIndex);
            if(currentIndex +1 < 51){
                String nextMediakey = (media.getIndex()+1)+media.getArtistKey();
                Surah surah = DAL.getInstance().setContext(this).getSurah(nextMediakey);
                DAL.getInstance().setContext(this).updateCurrentMedia(new CurrentMedia(surah.getKey(),surah.getArtistKey()
                        ,media.getIndex()+1,0));
                mSubTitle = surah.getTitle();
                Log.d(TAG, "onStartCommand: < Action Next > ::"+media.getIndex());
                playStream(surah.getUrl(),false);
                showNotification(mImgRes,getResources().getString(R.string.notificationPlayTitle),surah.getTitle());
            }
        }
        else if(intent.getAction().equals(Constants.ACTION.ACTION_PREV)){
            CurrentMedia media = DAL.getInstance().setContext(getApplicationContext()).getCurrentMedia();
            int currentIndex = media.getIndex();
            Log.d(TAG, "onStartCommand:"+currentIndex);
            if(currentIndex -1 > -1 ){
                String nextMediakey = (media.getIndex()-1)+media.getArtistKey();
                Surah surah = DAL.getInstance().setContext(this).getSurah(nextMediakey);
                DAL.getInstance().setContext(this).updateCurrentMedia(new CurrentMedia(surah.getKey(),surah.getArtistKey()
                        ,media.getIndex()-1,0));
                mSubTitle = surah.getTitle();
                Log.d(TAG, "onStartCommand: < Action Next > ::"+media.getIndex());
                playStream(surah.getUrl(),false);
                showNotification(mImgRes,getResources().getString(R.string.notificationPlayTitle),surah.getTitle());
            }
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
        notificationIntent.setClass(this, PlayService.class);
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
        String strPlayPause = "pause";
        int iconPlayPause = android.R.drawable.ic_media_pause;
        if(!isPlaying()){
            strPlayPause = "play";
            iconPlayPause = android.R.drawable.ic_media_play;
        }

        Notification notification = null;
        Bitmap notificationImg = BitmapFactory.decodeResource(getResources(),resImg);
        NotificationManager mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O){
            createChannel(mNotifyManager);
            NotificationCompat.Action actionPlayPause = new NotificationCompat.Action.Builder(iconPlayPause,strPlayPause, playPendingIntent).build();
            NotificationCompat.Action actionNext = new NotificationCompat.Action.Builder(android.R.drawable.ic_media_next,"next",nextPendingIntent).build();
            NotificationCompat.Action actionPrev = new NotificationCompat.Action.Builder(android.R.drawable.ic_media_previous,"previous",prevPendingIntent).build();

             notification = new NotificationCompat.Builder(this, "101")
                    .setContentText(appTitle)
                    .setTicker("Listening to Quran")
                    .setContentText(surahTitle)
                    .setSmallIcon(resImg)
                    .setLargeIcon(Bitmap.createScaledBitmap(notificationImg,100,100,false))
                    .setContentIntent(pendingIntent)
                     .addAction(actionPrev)
                     .addAction(actionPlayPause)
                     .addAction(actionNext)
                     .setChannelId("101")
                     .setOngoing(false)
                     .build();
            Log.d(TAG, "showNotification: Android O");
            mNotifyManager.notify(101, notification);

        }else {
             notification = new NotificationCompat.Builder(this,"Quran")
                    .setContentText(appTitle)
                    .setTicker("Listening to Quran")
                    .setContentText(surahTitle)
                    .setSmallIcon(resImg)
                    .setLargeIcon(Bitmap.createScaledBitmap(notificationImg,100,100,false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .addAction(android.R.drawable.ic_media_previous,"previous",prevPendingIntent)
                    .addAction(iconPlayPause,strPlayPause, playPendingIntent)
                    .addAction(android.R.drawable.ic_media_next,"next",nextPendingIntent)
                    .build();
            Log.d(TAG, "showNotification: Android Pre o");
            mNotifyManager.notify(101, notification);
            //startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(NotificationManager notificationManager) {
        String name = "Quran";
        String description = "Notifications for Listening to Quran";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .build();

       
        NotificationChannel mChannel = new NotificationChannel("101", name, importance);
        mChannel.setDescription(description);
//        mChannel.enableLights(true);
//        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
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
        unregisterReceiver(mAudioBecomeNoisyReceiver);
        if (mMediaPlayer !=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }


    public void playStream(String url, boolean toggleUiPlayPause){
        if (mMediaPlayer !=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            toggleUiPlayPauseIcon(toggleUiPlayPause);
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
            //mMediaPlayer.start();
            if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.O) {
                requestAudioFocus_preO();
            }else {
                requestAudioFocus_postO();
            }
            // to refresh notification icons & action
            showNotification(mImgRes,mTitle,mSubTitle);
            registerReceiver(mAudioBecomeNoisyReceiver, mIntentFilterNoisy);
        }

    }
    public void pause(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
            // to refresh notification icons & action
            showNotification(mImgRes,mTitle,mSubTitle);
        }
    }
    public void stop(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            unregisterReceiver(mAudioBecomeNoisyReceiver);
        }
    }

    public void toggle(){
        if(mMediaPlayer.isPlaying()){
            pause();
        }else {
            play();
        }

    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        mAudioManager.abandonAudioFocus(afChangeListener);
    }
    @Override
    public void onPrepared(MediaPlayer player) {
        //player.start();
        toggleUiPlayPauseIcon(true);
        play();
    }


    // requesting audio focus

    private boolean mPlayingBeforeInterruptions = false;
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if(mPlayingBeforeInterruptions){
                        play();
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSoundVolume, 0);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    toggle();
                    mAudioManager.abandonAudioFocus(afChangeListener);
                    unregisterReceiver(mAudioBecomeNoisyReceiver);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(isPlaying()){
                        mPlayingBeforeInterruptions = true;
                    }else {
                        mPlayingBeforeInterruptions = false;
                    }
                    pause();
                    unregisterReceiver(mAudioBecomeNoisyReceiver);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // ... pausing or ducking depends on your app
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1, 0);
                    break;
            }
        }
    };

    private void requestAudioFocus_preO(){
        mAudioManager = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        int resultCode = mAudioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        mSoundVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (resultCode == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback
            mMediaPlayer.start();
            showNotification(mImgRes,mTitle,mSubTitle);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void requestAudioFocus_postO(){
        mAudioManager = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        AudioFocusRequest mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mPlaybackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(afChangeListener)
                .build();
        mSoundVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int res = mAudioManager.requestAudioFocus(mFocusRequest);
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer.start();
            showNotification(mImgRes,mTitle,mSubTitle);
        }
    }


    // handling noise when headphone unplugged
    private class AudioBecomeNoisyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
                pause();
            }
        }
    }

    private IntentFilter mIntentFilterNoisy = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private AudioBecomeNoisyReceiver  mAudioBecomeNoisyReceiver = new AudioBecomeNoisyReceiver();

}
