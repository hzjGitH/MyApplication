package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutMyActivity extends AppCompatActivity {
TextView textView;
TextView user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_my);
        textView=findViewById(R.id.textview);
        user=findViewById(R.id.user);
        if (MainActivity.username!=null){
           String info="  对您统计的音乐类型数据为：流行音乐 "+MainActivity.RecordMap.get("流行音乐")+"  "+" 摇滚音乐 "+MainActivity.RecordMap.get("摇滚音乐")+"  "
                   +" 纯音乐 "+MainActivity.RecordMap.get("纯音乐")+"  " +" 经典音乐 "+MainActivity.RecordMap.get("经典音乐")+"  "
                   +" 说唱音乐 "+MainActivity.RecordMap.get("说唱音乐")+"  " +" 民族音乐 "+MainActivity.RecordMap.get("民族音乐")+"  "
                   +" 电子音乐 "+MainActivity.RecordMap.get("电子音乐");
           textView.setText(info);
            user.setText(MainActivity.username);
        }
    }
}
