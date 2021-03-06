package com.baraa.bsoft.mediaplayer.Views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baraa.bsoft.mediaplayer.Activities.MainActivity;
import com.baraa.bsoft.mediaplayer.DataAccess.DAL;
import com.baraa.bsoft.mediaplayer.Model.DataStored;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;
import com.baraa.bsoft.mediaplayer.Services.Downloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by baraa on 01/01/2018.
 */

public class SurahAdapter extends ArrayAdapter implements Downloader.DownloadProgressListener {
    private static final String TAG = "SurahAdapter";
    private ArrayList<Surah> surahslst;
    private Context context;
    private int resource;
    private LayoutInflater layoutInflater;
    private PlayListListener mPlayListListener;
    private Map<String,Integer> mMapViewDownload;
    private ListView mListView;


    public interface PlayListListener{
        void onItemListClicked(Surah surah,int index,FabButton view);
        void onDownloadClicked(Surah surah,int index,FabButton view);
    }
    public void setmPlayListListener(PlayListListener playListListener){
        mPlayListListener = playListListener;
    }
    public SurahAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<Surah> mlist,ListView listView) {
        super(context, resource);
        surahslst = mlist;
        this.context = context;
        this.resource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        mMapViewDownload = new HashMap<>();
        mListView = listView;
    }

    @Override
    public int getCount() {
        return surahslst.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            convertView = layoutInflater.inflate(resource,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Surah surah = surahslst.get(position);
        //Log.d(TAG, "getView: "+surah.toString());
        viewHolder.getTvTitle().setText(surah.getTitle());
        viewHolder.getTvTitleArabic().setText(surah.getTitleArabic());
        viewHolder.getBtnPlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayListListener.onItemListClicked(surahslst.get(position),position,(FabButton)viewHolder.getBtnPlay());
            }
        });

        if(DAL.getInstance().setContext(getContext()).getDataStoredWithSurahKey(surah.getKey()) != null)
            setDownloadIcon(viewHolder.getBtnDownload(),2);
        else
            setDownloadIcon(viewHolder.getBtnDownload(),0);

        viewHolder.getBtnDownload().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.getBtnDownload().resetIcon();
                viewHolder.getBtnDownload().showShadow(false);
                viewHolder.getBtnDownload().showProgress(true);
                downloadInit(surah);
                mMapViewDownload.put(surah.getKey(),position);

            }
        });
        return  convertView;
    }

    /*** To prevent recycling ListView Item ***
     *
     *    (( disable view recycling!! )
     *
     ****/
    @Override
    public int getViewTypeCount() {

        return surahslst.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    /*************************************/

    public void updateData(ArrayList<Surah> lst){
        surahslst.clear();
        surahslst = lst;
        notifyDataSetChanged();
        Log.d(TAG, "updateData: !!!!!!");
    }


    void setDownloadIcon(FabButton fabButton,int status){
        switch (status){
            case 0:
                fabButton.setEnabled(true);
                fabButton.setProgress(0);
                fabButton.setVisibility(View.VISIBLE);
                fabButton.setIcon(ContextCompat.getDrawable(context,R.drawable.ic_file_download_white_24dp),
                        ContextCompat.getDrawable(context,R.drawable.ic_file_download_white_24dp));
                break;
            case 1:
                fabButton.setIcon(ContextCompat.getDrawable(context,R.drawable.download_button_arrow),
                        ContextCompat.getDrawable(context,R.drawable.download_button_arrow));
                fabButton.setEnabled(false);
                break;
            case 2:
                fabButton.setVisibility(View.GONE);
                break;
                default:
                    fabButton.setIcon(ContextCompat.getDrawable(context,R.drawable.ic_file_download_white_24dp),
                            ContextCompat.getDrawable(context,R.drawable.ic_file_download_white_24dp));
        }
    }
    private void updateViewDownloadProgress(final int pos,final double progress){
       // Log.d(TAG, "updateViewDownloadProgress: "+progress+"%");
        View view = getViewByPosition(pos,mListView);
        if (view == null) return;
        //Log.d(TAG, "run: >>"+progress);
        FabButton fabButton = (FabButton)view.findViewById(R.id.btnDownloadLst);
        fabButton.setProgress((float) progress);
        setDownloadIcon(fabButton,1);
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void downloadInit(Surah surah){
        if(!((MainActivity)getContext()).checkStoragePermissionBeforeAccess()){
            Toast.makeText(context,"Enable permission to download! ",Toast.LENGTH_LONG);
        }
        Downloader downloader = new Downloader(context,this,surah.getKey(),surah.getKey());
        downloader.execute(surah.getUrl());
    }

    @Override
    public void onDownloadFinnished(final String tokenId,String path,String skey) {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Surah surah = surahslst.get(mMapViewDownload.get(tokenId));
                DAL.getInstance().setContext(context).updateProgress(surah.getKey(),2);
                FabButton fabButton = (FabButton) getViewByPosition(mMapViewDownload.get(tokenId),mListView).findViewById(R.id.btnDownloadLst);
                setDownloadIcon(fabButton,2);

            }
        });
        DataStored dataStored = new DataStored(skey,path);
        DAL.getInstance().setContext(getContext()).insertOrUpdateDataStored(dataStored);

        Log.d(TAG, "onDownloadFinnished: THE STORED KEY :: ");
    }

    @Override
    public void onDownloadProgress(String tokenId, int progress,String skey) {
        if(progress == 0){
            return;
        }
        Surah surah = surahslst.get(mMapViewDownload.get(tokenId));
        if(progress <101 && surah.getKey().equals(skey))
            updateViewDownloadProgress(mMapViewDownload.get(tokenId),(progress));
    }

    @Override
    public void onDownloadStarted(final String tokenId) {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Surah surah = surahslst.get(mMapViewDownload.get(tokenId));
                DAL.getInstance().setContext(context).updateProgress(surah.getKey(),1);
                FabButton fabButton = (FabButton) getViewByPosition(mMapViewDownload.get(tokenId),mListView).findViewById(R.id.btnDownloadLst);
                setDownloadIcon(fabButton,1);
            }
        });

    }

    public class ViewHolder{
        public TextView tvTitle;
        public FabButton btnPlay;
        private FabButton btnDownload;
        private CardView cvMain;
        private TextView tvTitleArabic;
        private View view;

        public View getView() {
            return view;
        }

        public ViewHolder(View view) {
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.btnPlay = (FabButton) view.findViewById(R.id.btnPlayLst);
            this.cvMain = (CardView)view.findViewById(R.id.cvMain);
            this.btnDownload = (FabButton)view.findViewById(R.id.btnDownloadLst);
            this.tvTitleArabic = (TextView)view.findViewById(R.id.tvTitleAr);
            this.view = view;
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public FabButton getBtnPlay() {
            return btnPlay;
        }

        public CardView getCvMain() {
            return cvMain;
        }

        public FabButton getBtnDownload() {
            return btnDownload;
        }

        public TextView getTvTitleArabic() {return tvTitleArabic;}
    }
}
