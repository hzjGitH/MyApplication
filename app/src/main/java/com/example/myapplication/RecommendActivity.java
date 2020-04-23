package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Bean.Music;
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
//每日推荐Activity
public class RecommendActivity extends AppCompatActivity {
List<Music> musicList =new ArrayList<>();
RecyclerView recommend_rec;

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what){
            case 1:  Bundle bundle=msg.getData();
                    musicList=new DataParser().Parser(bundle);
                   LinearLayoutManager layoutManager = new LinearLayoutManager(RecommendActivity.this);
                   recommend_rec.setLayoutManager(layoutManager);
                   layoutManager.setOrientation(RecyclerView.VERTICAL);
                   MusicAdapter MusicAdapter =new MusicAdapter(musicList,RecommendActivity.this);
                   recommend_rec.setAdapter(MusicAdapter);
                break;
            case 2:
                break;
        }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        recommend_rec=findViewById(R.id.recommend_rec);
        ImageView back=findViewById(R.id.back);
            GetRecommendMusic();
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
    }

    public void GetRecommendMusic(){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().get().url(Url.url+"login/servlet/DailyServlet").build();
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
