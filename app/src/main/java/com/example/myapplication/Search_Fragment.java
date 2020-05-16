package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import com.example.myapplication.Adapter.ViewPagerAdapter;
import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.DataParser;
import com.example.myapplication.Util.Url;
import com.google.gson.Gson;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.UltraViewPagerAdapter;
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
import static com.example.myapplication.MainActivity.username;

public class Search_Fragment extends Fragment {
   private LinearLayout recomandlayout;//每日推荐
    private LinearLayout ranklayout;//排行榜
    private LinearLayout songlayou;//歌单
    private LinearLayout newestlayout;//最新推荐
    private  UltraViewPager ultraViewPager;
    private LinearLayout  dotHorizontal;
    RecyclerView tuijian_rec;
    Handler handler;
    Context context;
    private List<Music> musicList=new ArrayList<>();
  public Handler searHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            Log.i("tuijian Data___>",bundle.getString("musicinfo"));
            musicList=  new DataParser().Parser2(bundle);
            LinearLayoutManager manager=new LinearLayoutManager(context);
            manager.setOrientation(RecyclerView.VERTICAL);
            tuijian_rec.setLayoutManager(manager);
            MusicAdapter adapter=new MusicAdapter(musicList,handler,context);
            tuijian_rec.setAdapter(adapter);
            return false;
        }
    });

     Search_Fragment(Handler handler,Context context){
         this.handler=handler;
         this.context=context;
     }

    @Override
    public void onResume() {
        super.onResume();
        if (username!=null&&musicList!=null)
            GetTuiJian();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.search_fragment, container, false);
        recomandlayout=view.findViewById(R.id.recommend);
        songlayou=view.findViewById(R.id.song_sheet);
        ranklayout=view.findViewById(R.id.rank_list);
        newestlayout=view.findViewById(R.id.newestlayout);
        ultraViewPager=view.findViewById(R.id.viewpage);
        tuijian_rec=view.findViewById(R.id.tuijian_rec);
        dotHorizontal=view.findViewById(R.id.dot_horizontal);
        ViewPagerAdapter viewPagerAdapter =new ViewPagerAdapter(view.getContext());
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        PagerAdapter adapter=new UltraViewPagerAdapter(viewPagerAdapter);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.initIndicator();
      ultraViewPager.getIndicator()
                .setFocusColor(R.color.brown)
                .setNormalColor(Color.WHITE)
                .setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().build();
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAutoScroll(2000);

        recomandlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), RecommendActivity.class);
                startActivity(intent);
            }
        });
        songlayou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),SongsheetActivity.class);
                startActivity(intent);
            }
        });
        ranklayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),RankActivity.class);
                startActivity(intent);
            }
        });
        newestlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),NewestActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    public  void GetTuiJian(){
        Gson gson=new Gson();
        HashMap<String,Integer> map=new HashMap<>();
        map.put("Absolute",RecordMap.get("纯音乐"));
        map.put("Rap",RecordMap.get("说唱音乐"));
        map.put("Popular",RecordMap.get("流行音乐"));
        map.put("Electronic",RecordMap.get("电子音乐"));
        map.put("Rock",RecordMap.get("摇滚音乐"));
        map.put("Folk",RecordMap.get("民族音乐"));
        map.put("Classical",RecordMap.get("经典音乐"));
        String data= gson.toJson(map);
        OkHttpClient okHttpClient=new OkHttpClient();
        RequestBody requestBody=RequestBody.create(JSON,data);
        Request request=new Request.Builder().post(requestBody).url(Url.url+"login/servlet/MaybeLikeServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle=new Bundle();
                bundle.putString("musicinfo",response.body().string());
                Message message=new Message();
                message.setData(bundle);
               searHandler.sendMessage(message);
            }
        });
    }
}
