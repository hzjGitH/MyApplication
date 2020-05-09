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

import static com.example.myapplication.MainActivity.likelist;

public class MyLoveActivity extends AppCompatActivity {
    RecyclerView music_rec;
    ImageView backview;
    LinearLayout titlelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_love);
        music_rec = findViewById(R.id.music_rec);
        backview = findViewById(R.id.back);
        titlelayout=findViewById(R.id.titlelayout);
        backview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        music_rec.setLayoutManager(manager);
        MusicAdapter adapter = new MusicAdapter(likelist, MyLoveActivity.this);
        music_rec.setAdapter(adapter);
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
