package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.AndroidDownloadManger;
import com.example.myapplication.Util.Url;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.example.myapplication.MainActivity.JSON;
import static com.example.myapplication.MainActivity.RecordMap;
import static com.example.myapplication.MainActivity.id;
import static com.example.myapplication.MainActivity.info;
import static com.example.myapplication.MainActivity.likelist;
import static com.example.myapplication.MainActivity.mediaPlayer;
import static com.example.myapplication.MainActivity.musicServe;
import static com.example.myapplication.MainActivity.recentlyList;
import static com.example.myapplication.MainActivity.singer;
import static com.example.myapplication.MainActivity.username;
import static com.example.myapplication.My_Fragment.myhandle;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<Music> musicList;
    Handler handler;
    Context context;
    private int midcount;

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout music_item;
        TextView songname;
        TextView singer;
        TextView position;
        ImageView more_item;
        ImageView add_mylike;
        ImageView download;
        ImageView comments;
        LinearLayout more_items;

        public ViewHolder(View view) {
            super(view);
            music_item = view.findViewById(R.id.music_item);
            songname = view.findViewById(R.id.songname);
            singer = view.findViewById(R.id.singer);
            position = view.findViewById(R.id.position);
            more_item = view.findViewById(R.id.more);
            add_mylike = view.findViewById(R.id.add_mylike);
            download = view.findViewById(R.id.download);
            comments = view.findViewById(R.id.comments);
            more_items = view.findViewById(R.id.more_items);

        }
    }

    public MusicAdapter(List<Music> musicList, Context context) {
        this.musicList = musicList;
        this.context = context;
    }

    public MusicAdapter(List<Music> musicList, Handler handler, Context context) {
        this.musicList = musicList;
        this.handler = handler;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull final MusicAdapter.ViewHolder holder, int position) {
        final Music music = musicList.get(position);
        holder.songname.setText(music.getSongname());
        holder.singer.setText(music.getSinger());
        holder.position.setText(String.valueOf(position));
        holder.more_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.more_items.getVisibility()==View.GONE)
                holder.more_items.setVisibility(View.VISIBLE);
                else
                    holder.more_items.setVisibility(View.GONE);
            }
        });
        //跳转到音乐评论Activity
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicCommentsActivity.class);
                intent.putExtra("songname", music.getSongname());
                intent.putExtra("singer", music.getSinger());
                intent.putExtra("comments", music.getComments());
                intent.putExtra("musictype",music.getMusictype());
                System.out.println("22222" + music.getComments());
                context.startActivity(intent);
            }
        });
        holder.add_mylike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size= likelist.size();
                likelist.add(music);
                //去重
                Set<Music> set=new HashSet<>(likelist);
                likelist=new ArrayList<>(set);
                if (size!=likelist.size()&&username!=null)//成功添加喜欢,基数加2
                    RecordMap.put(music.getMusictype(),RecordMap.get(music.getMusictype())+2);
                else
                    Toast.makeText(context,"已存在",Toast.LENGTH_SHORT).show();
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "正在下载", Toast.LENGTH_SHORT).show();
                AndroidDownloadManger androidDownloadManger = new AndroidDownloadManger(context, Url.url + music.getPath());
                androidDownloadManger.download();
                if(username!=null)
                RecordMap.put(music.getMusictype(),RecordMap.get(music.getMusictype())+1);

            }
        });

    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.music_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(Url.url + musicList.get(holder.getPosition()).getPath());
                    info = musicList.get(holder.getPosition()).getSongname() + "-" + musicList.get(holder.getPosition()).getSinger();
                    singer = musicList.get(holder.getPosition()).getSinger();
                    if (username!=null) {
                        midcount = MainActivity.RecordMap.get(musicList.get(holder.getPosition()).getMusictype());
                        midcount=midcount+1;
                        MainActivity.RecordMap.put(musicList.get(holder.getPosition()).getMusictype(), midcount);
                        Log.i("RecordMap", RecordMap.toString());
                    }
                    if (handler != null)
                        handler.sendEmptyMessage(3);//更新播放信息
                    if (!recentlyList.contains(musicList.get(holder.getPosition()))) {
                        recentlyList.add(musicList.get(holder.getPosition()));
                        //去重
                        Set<Music> set=new HashSet<>(recentlyList);
                        recentlyList=new ArrayList<>(set);
                        Message message = new Message();
                        message.what = 2;
                        myhandle.sendMessage(message);//通知更新历史播放数据
                    }
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Toast.makeText(view.getContext(), "正在播放:" + musicList.get(holder.getPosition()).getSongname(), Toast.LENGTH_SHORT).show();
                        musicServe.play(mp, musicList, holder.getPosition());
                        if (username != null)
                            updateMusicData(musicList.get(holder.getPosition()).getSongname(), musicList.get(holder.getPosition()).getSinger(), username);
                    }
                });
            }
        });

        return holder;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
//更新播放次数
    private void updateMusicData(String songname, String singer, String username) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Map<String, String> map = new ArrayMap<>();
        map.put("update", "MusicInfo");
        map.put("songname", songname);
        map.put("singer", singer);
        map.put("username", username);
        map.put("id", id);
        Gson gson = new Gson();
        String data = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON, data);
        Request request = new Request.Builder().post(requestBody).url(Url.url + "community/UpdataServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
