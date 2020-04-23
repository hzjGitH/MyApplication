package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.DataParser;
import com.example.myapplication.Util.Url;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.JSON;
import static com.example.myapplication.MainActivity.RecordMap;

//搜索Activity
public class SearchActivity extends AppCompatActivity {
  static   List<String> historical_data=new ArrayList<>();//存放搜索数据

  List<Music> searchinfo=new ArrayList<>();//搜索的结果数据
    ListView historical_listview;//搜索的历史记录
    RecyclerView searchinfo_rec;
    SearchView searchView;
    ArrayAdapter<String> adapter;


    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            searchinfo=new DataParser().Parser(bundle);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(SearchActivity.this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            searchinfo_rec.setLayoutManager(linearLayoutManager);
            MusicAdapter adapter=new MusicAdapter(searchinfo,SearchActivity.this);
            searchinfo_rec.setAdapter(adapter);
          /*  if(searchinfo.size()<=10){
                for (Music music:searchinfo){
                    String key=music.getMusictype();
                    RecordMap.put(key,RecordMap.get(key)+2);
                }
            }*/
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView=findViewById(R.id.search_view);
        historical_listview=findViewById(R.id.his_lv);
        searchinfo_rec=findViewById(R.id.search_info);
       adapter =new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,historical_data);
        historical_listview.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击软键盘中的搜索
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query!=null){
                    SearchResult(query);
                    historical_data.add(query);
                    searchView.clearFocus();
                    adapter.notifyDataSetChanged();
                    historical_listview.setVisibility(View.GONE);
                    searchinfo_rec.setVisibility(View.VISIBLE);

                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                historical_listview.setVisibility(View.VISIBLE);
                searchinfo_rec.setVisibility(View.GONE);
                //输入框内容改变监听
                return false;
            }
        });
    }
    public void SearchResult(String info){
        OkHttpClient okHttpClient=new OkHttpClient();
        HashMap<String,String> map=new HashMap<>();
        map.put("searchinfo",info);
        Gson gson=new Gson();
        String data=gson.toJson(map);
        RequestBody requestBody=RequestBody.create(JSON,data);
        Request request =new Request.Builder().post(requestBody).url(Url.url+"login/servlet/SearchServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.what=1;
                Bundle bundle=new Bundle();
                bundle.putString("musicinfo",response.body().string());
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }
}
