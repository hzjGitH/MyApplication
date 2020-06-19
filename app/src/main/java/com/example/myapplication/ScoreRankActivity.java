package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.DataParser;
import com.example.myapplication.Util.Url;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.JSON;

public class ScoreRankActivity extends AppCompatActivity {
    RecyclerView score_rec;
    List<Music> musicList=new ArrayList<>();

    Handler handler=new Handler(new Handler.Callback() {


        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            musicList=  new DataParser().Parser(bundle);
            LinearLayoutManager manager=new LinearLayoutManager(ScoreRankActivity.this);
            manager.setOrientation(RecyclerView.VERTICAL);
            score_rec.setLayoutManager(manager);
            MusicAdapter adapter=new MusicAdapter(musicList,ScoreRankActivity.this);
            score_rec.setAdapter(adapter);

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_rank);
        TextView title=findViewById(R.id.title);
        title.setText("评分排行");
        ImageView back=findViewById(R.id.back);
        score_rec=findViewById(R.id.score_rec);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetScoreRank();
    }
    private void GetScoreRank(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get()
                .url(Url.url+"login/servlet/GetScoreRankServlet")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("musicinfo", response.body().string());
                Message message = new Message();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

}
