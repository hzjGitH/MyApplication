package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.Localmusic;
import com.example.myapplication.Util.Url;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.example.myapplication.MainActivity.info;
import static com.example.myapplication.MainActivity.mediaPlayer;
import static com.example.myapplication.MainActivity.musicServe;

import static com.example.myapplication.MainActivity.singer;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {
    Context context;
    List<Localmusic> list;
  public   LocalMusicAdapter(List<Localmusic> list,Context context){
        this.list=list;
        this.context=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       RelativeLayout music_item;
       TextView songname;
       TextView singer;
       TextView position;
        public ViewHolder(View view){
           super(view);
           music_item=view.findViewById(R.id.music_item);
           songname=view.findViewById(R.id.songname);
           singer=view.findViewById(R.id.singer);
           position=view.findViewById(R.id.position);
       }
   }

    @NonNull
    @Override
    public LocalMusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.localmusic_item,parent,false);
       ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicAdapter.ViewHolder holder,  int position) {
   final    Localmusic localmusic=list.get(position);
   final int index=position;
      holder.singer.setText(localmusic.getSinger());
      holder.songname.setText(localmusic.getSongname());
      holder.position.setText(String.valueOf(position));
        holder.music_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    mediaPlayer.reset();
                  String card= Environment.getExternalStorageDirectory().getPath();
                  System.out.println("card"+card);
                  File file=new File(localmusic.getPath());
                  file.canRead();
                    mediaPlayer.setDataSource(file.getPath());
                    info = localmusic.getSongname();
                    singer = localmusic.getSinger();
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Toast.makeText(v.getContext(), "正在播放:" + localmusic.getSongname(), Toast.LENGTH_SHORT).show();
                        System.out.println(localmusic.getPath());
                        musicServe.play(mp, list, index, true);

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
