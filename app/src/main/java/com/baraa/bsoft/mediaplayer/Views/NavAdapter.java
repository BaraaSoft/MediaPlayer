package com.baraa.bsoft.mediaplayer.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baraa.bsoft.mediaplayer.Model.Artist;
import com.baraa.bsoft.mediaplayer.R;

import java.util.ArrayList;

/**
 * Created by baraa on 21/03/2018.
 */

public class NavAdapter extends ArrayAdapter {
    private Context mContext;
    private ArrayList<Artist> mArtists;
    private NavListener mNavListener;

    public interface NavListener{
        void onNavClicked(Artist artist);
    }
    public NavAdapter(@NonNull Context context, int resource,ArrayList<Artist> lstArtists,NavListener navListener) {
        super(context, resource);
        this.mContext = context;
        this.mArtists = lstArtists;
        if(mNavListener instanceof NavListener){
            this.mNavListener = navListener;
        }else {
            throw new ClassCastException("must implement NavListener!");
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nav_item,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final Artist artist = mArtists.get(position);
        viewHolder.getTvShk().setText(artist.getName());
        viewHolder.getImgShk().setImageDrawable(mContext.getDrawable(artist.getImageResourceId()));
        viewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavListener.onNavClicked(artist);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return mArtists.size();
    }


    class ViewHolder{
        private View view;
        private ImageView imgShk;
        private TextView tvShk;

        public ViewHolder(View view) {
            this.view = view;
            this.imgShk = (ImageView)view.findViewById(R.id.imgShk);
            this.tvShk = (TextView)view.findViewById(R.id.tvShk);
        }

        public ImageView getImgShk() {
            return imgShk;
        }

        public TextView getTvShk() {
            return tvShk;
        }

        public View getView() {
            return view;
        }
    }
}
