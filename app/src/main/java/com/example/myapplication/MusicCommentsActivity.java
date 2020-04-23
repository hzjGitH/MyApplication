package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Util.Url;
import com.example.myapplication.comments.Commentsbean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.JSON;
import static com.example.myapplication.MainActivity.id;
import static com.example.myapplication.MainActivity.reply;
import static com.example.myapplication.MainActivity.username;

public class MusicCommentsActivity extends AppCompatActivity {
    TextView songname;
    TextView singer;
    ImageView emptyview;
    EditText content;
    ImageView publish;
    Commentsview commentsview;
    String song_name;
    String sing;
    String comments;
    List<Commentsbean> music_comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_comments);
        init();
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null && !content.getText().toString().equals("")) {
                    PublishMusicComment();
                    content.setText("");
                    Toast.makeText(MusicCommentsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MusicCommentsActivity.this, "没登陆或者发表信息不能为空哦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        Intent intent = getIntent();
        song_name = intent.getStringExtra("songname");
        sing = intent.getStringExtra("singer");
        comments = intent.getStringExtra("comments");
        System.out.println("comments" + comments);
        songname = findViewById(R.id.songname);
        singer = findViewById(R.id.singer);
        emptyview = findViewById(R.id.Empty);
        commentsview = findViewById(R.id.music_comments);
        content = findViewById(R.id.contet_text);
        publish = findViewById(R.id.publish);
        songname.setText(song_name);
        singer.setText(sing);
        if (comments == null) {
            emptyview.setVisibility(View.VISIBLE);
            commentsview.setVisibility(View.GONE);
        } else {

            emptyview.setVisibility(View.GONE);
            commentsview.setVisibility(View.VISIBLE);
            try {
                JSONArray jsonArray = new JSONArray(comments);
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Commentsbean commentsbean = gson.fromJson(jsonArray.get(i).toString(), Commentsbean.class);
                    music_comments.add(commentsbean);
                }
                commentsview.setlist(music_comments);
                commentsview.notifydatasetchanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void PublishMusicComment() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("update", "MusicComments");
        map.put("singer", sing);
        map.put("songname", song_name);
        map.put("commentsuser", username);
        map.put("replyuser", reply);
        map.put("content", content.getText().toString());
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, gson.toJson(map));
        Request request = new Request.Builder().post(requestBody).url(Url.url + "community/UpdataServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }


}
