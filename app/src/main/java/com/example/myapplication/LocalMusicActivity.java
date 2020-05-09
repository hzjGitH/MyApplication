package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.myapplication.Bean.Localmusic;
import com.example.myapplication.Util.LoaclMusicUtil;

import java.util.List;

public class LocalMusicActivity extends AppCompatActivity {
    List<Localmusic> localmusics;
    ImageView back;
    LinearLayout titlelayout;
    RecyclerView music_rec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        localmusics= LoaclMusicUtil.getmusic(LocalMusicActivity.this);
        back=findViewById(R.id.back);
        music_rec=findViewById(R.id.music_rec);
        titlelayout=findViewById(R.id.titlelayout);
        System.out.println(localmusics);
        LocalMusicAdapter adapter=new LocalMusicAdapter(localmusics,this);
        LinearLayoutManager manager=new LinearLayoutManager(LocalMusicActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        music_rec.setLayoutManager(manager);
        music_rec.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences=getSharedPreferences("colors",MODE_PRIVATE);
        String color= sharedPreferences.getString("newcolor","#fff");
        getWindow().setStatusBarColor(Color.parseColor(color));
       titlelayout.setBackgroundColor(Color.parseColor(color));
    }
}
