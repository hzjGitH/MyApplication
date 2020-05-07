package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.CustomDialog;
import com.example.myapplication.Util.DataParser;
import com.example.myapplication.Util.Url;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SongsheetFragment extends Fragment {
 String music_type;
RecyclerView music_rec;
List<Music> musicList=new ArrayList<>();
Context context;
CustomDialog dialog;

Handler handler=new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Bundle bundle=msg.getData();
        musicList=new DataParser().Parser(bundle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        music_rec.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        MusicAdapter adapter=new MusicAdapter(musicList,context);
        music_rec.setAdapter(adapter);
        dialog.dismiss();
        return false;
    }
});


    public SongsheetFragment(String musictype,Context context) {
        this.music_type=musictype;
        this.context=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_songsheet, container, false);
        music_rec=view.findViewById(R.id.music_rec);
        dialog=new CustomDialog(context,"数据加载中...");
        dialog.show();
        getmusic(music_type);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getmusic(String music_type){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().get().url(Url.url+"login/servlet/"+music_type+"Servlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle=new Bundle();
                bundle.putString("musicinfo",response.body().string());
                Message message=new Message();
                message.what=1;
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }


}
