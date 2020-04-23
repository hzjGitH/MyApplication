package com.example.myapplication.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.DownloadBean;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.io.IOException;
import java.util.List;

import static com.example.myapplication.MainActivity.mediaPlayer;


public class FinishdownloadAdapter extends RecyclerView.Adapter<FinishdownloadAdapter.ViewHolder> {

    Context context;
    List<DownloadBean> finishdownloadlist;
   public FinishdownloadAdapter(Context context, List<DownloadBean> downloadBeanList){
        this.context=context;
        this.finishdownloadlist=downloadBeanList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView songname;
        LinearLayout linear_finish;
        public ViewHolder(View view) {
            super(view);
        songname=view.findViewById(R.id.text);
        linear_finish=view.findViewById(R.id.linear_finish);
        }

    }

    @NonNull
    @Override
    public FinishdownloadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item,parent,false);
       ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FinishdownloadAdapter.ViewHolder holder, int position) {
      final DownloadBean downloadBean=finishdownloadlist.get(position);
        holder.songname.setText(downloadBean.getDownloadname());
        holder.linear_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(downloadBean.getPath());
                    mediaPlayer.prepareAsync();
                    MainActivity.info=downloadBean.getDownloadname();
                }catch (IOException e){
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        Toast.makeText(context,"正在播放:"+downloadBean.getDownloadname(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return finishdownloadlist.size();
    }

}
