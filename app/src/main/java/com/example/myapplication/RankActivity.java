package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.Url;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//排行榜Activity
public class RankActivity extends AppCompatActivity {
TextView titleview;
RecyclerView rank_rec;
List<Music>  musicList =new ArrayList<>();

Handler handler=new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Bundle bundle=msg.getData();
        Gson gson=new Gson();
        try {
            JSONArray jsonArray=new JSONArray(bundle.get("musicinfo").toString());
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                Music music;
                music=gson.fromJson(jsonObject.toString(),Music.class);
                /*
                music.setSongname(jsonObject.getString("songname"));
                music.setPath(jsonObject.getString("path"));
                music.setSinger(jsonObject.getString("singer"));
                music.setMusictype(jsonObject.getString("musictype"));*/
                musicList.add(music);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(RankActivity.this);
            rank_rec.setLayoutManager(layoutManager);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            MusicAdapter MusicAdapter =new MusicAdapter(musicList,RankActivity.this);
            rank_rec.setAdapter(MusicAdapter);
            //   System.out.println(jsonArray.get(0));
        }catch (JSONException e){
            e.printStackTrace();
        }

        return false;
    }
});


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        titleview=findViewById(R.id.title);
        titleview.setText("排行榜");
        rank_rec=findViewById(R.id.rank_rec);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RankMusic();
    }
    public void RankMusic(){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().get().url(Url.url+"login/servlet/RankServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("musicinfo", response.body().string());
                Message message = new Message();
                message.what = 1;
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }
}
