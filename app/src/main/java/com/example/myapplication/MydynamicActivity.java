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

import com.example.myapplication.Util.CustomDialog;
import com.example.myapplication.Util.DataParser;
import com.example.myapplication.Util.Url;
import com.example.myapplication.comments.PublicBean;
import com.google.gson.Gson;

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
import static com.example.myapplication.MainActivity.username;

public class MydynamicActivity extends AppCompatActivity {

ImageView back;
TextView title;
RecyclerView mydynamic;
List<PublicBean> publicBeans=new ArrayList<>();
CustomDialog dialog;
Handler handler=new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Bundle bundle1 = msg.getData();
        publicBeans = new DataParser().PublicParser(bundle1);
        Log.i("My________",publicBeans.toString());
        CommunityAdapter  adapter = new CommunityAdapter(publicBeans, MydynamicActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(MydynamicActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        mydynamic.setLayoutManager(manager);
        mydynamic.setAdapter(adapter);
        dialog.dismiss();
        return false;
    }
});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydynamic);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title);
        mydynamic=findViewById(R.id.my_dynamic);
        title.setText("我的动态");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetPersonDynamic();
        dialog=new CustomDialog(MydynamicActivity.this,"加载中");
        dialog.show();
    }
  void  GetPersonDynamic(){
      OkHttpClient okHttpClient=new OkHttpClient();
      Map<String,String> map=new HashMap<>();
      map.put("username",username);
      Gson gson=new Gson();
      RequestBody requestBody=RequestBody.create(JSON,gson.toJson(map));
      Request request=new Request.Builder().post(requestBody).url(Url.url+"community/PersonPublicServlet").build();
      okHttpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              dialog.dismiss();
              e.printStackTrace();
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
              Bundle bundle = new Bundle();
              bundle.putString("getpublicinfo", response.body().string());
              Message message = new Message();
              message.setData(bundle);
              handler.sendMessage(message);
          }
      });
    }
}
