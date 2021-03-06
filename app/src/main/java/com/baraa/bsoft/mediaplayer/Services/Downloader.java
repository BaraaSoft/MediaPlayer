package com.baraa.bsoft.mediaplayer.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by baraa on 20/03/2018.
 */

public class Downloader extends AsyncTask<String,Integer,String> {
    private static final String TAG = "Downloader";
    private Context mContext;
    private PowerManager.WakeLock mWakeLock;
    private DownloadProgressListener mProgressListener;
    private String mToken;
    private int progress = 0;
    private String mSurahKey;

    private String mFilePath;

    public interface DownloadProgressListener{
        void onDownloadFinnished(String tokenId,String path,String sKey);
        void onDownloadProgress(String tokenId,int progress,String sKey);
        void onDownloadStarted(String tokenId);
    }


    public void setProgressListener(DownloadProgressListener progressListener){
        mProgressListener = progressListener;
    }

    public Downloader(Context context,DownloadProgressListener downloadProgressListener,String token,String surahKey) {
        this.mContext = context;
        this.mToken = token;
        this.mProgressListener = downloadProgressListener;
        this.mSurahKey = surahKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

    }

    @Override
    protected String doInBackground(String... sUrl) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
           // mFilePath = getPublicAlbumStorageDir("Quran").getAbsolutePath()+mToken+".mp3";

            mFilePath = getStorageDir("Quran").getAbsoluteFile()+File.separator+mToken+".mp3";

            input = connection.getInputStream();
            output = new FileOutputStream(mFilePath);
            mProgressListener.onDownloadStarted(mToken);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(values == null){
            return;
        }
        //Log.d(TAG, "onProgressUpdate: >>> "+values[0]);

        if(values[0]%2 == 0 && values[0] != progress){
            mProgressListener.onDownloadProgress(mToken,values[0],mSurahKey);
        }

        progress = values[0];

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mWakeLock.release();
        if (result != null){
            Toast.makeText(mContext,"Download error: "+result, Toast.LENGTH_LONG).show();
            Log.e(TAG, "onPostExecute: >>>"+result);
        }
        else{
            mProgressListener.onDownloadFinnished(mToken,mFilePath,mSurahKey);
        }
    }

    private File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), albumName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
        }catch (Exception e){
            Log.e(TAG, "getPublicAlbumStorageDir: ",e );
        }

        return file;
    }


    private File getStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"system app"+File.separator+albumName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
        }catch (Exception e){
            Log.e(TAG, "getPublicAlbumStorageDir: ",e );
        }

        return file;
    }
}
