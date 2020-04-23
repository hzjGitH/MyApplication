package com.example.myapplication.comments;

import com.example.myapplication.Util.Url;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.JSON;

public class Updata {
   public Updata(HashMap<String,String> map){
       OkHttpClient okHttpClient=new OkHttpClient();
       Gson gson=new Gson();
       String Data=gson.toJson(map);
       RequestBody requestBody=RequestBody.create(JSON,Data);
       Request request=new Request.Builder().post(requestBody).url(Url.url+"community/UpdataServlet").build();
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
