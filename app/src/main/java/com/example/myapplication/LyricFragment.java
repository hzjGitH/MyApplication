package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.Bean.Music;
import com.example.myapplication.LyricView.Lyric;
import com.example.myapplication.LyricView.LyricParser;
import com.example.myapplication.LyricView.LyricView;
import com.example.myapplication.Util.Url;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.myapplication.MainActivity.mediaPlayer;
import static com.example.myapplication.MainActivity.musicServe;

public class LyricFragment extends Fragment implements ToFragment{
    LyricView lyricView;
    LyricFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.lyricfragment,container,false);
         lyricView=view.findViewById(R.id.lyricview);
        /* File file=new File("/storage/emulated/0/netease/cloudmusic/Music/喜欢你 - 李嘉石.lrc");
             List<Lyric> lrc = LyricParser.loadFile(file);
             lyricView.setLrc(lrc);*/
         SearchLrc();

        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()){
                    lyricView.update(mediaPlayer.getCurrentPosition(),mediaPlayer.getDuration());
                }
            }
        };
        timer.schedule(timerTask,0,100);

        return view;
    }
    public void loadLyric(){
      Music music= musicServe.CurrentMusic();

      if (music==null)
            return;
        Log.i("music",music.getLyric());
        if (music.getLyric()==null)
            return;
      String lrcname=music.getSinger()+"-"+music.getSongname()+".lrc";
        try {
            URL url=new URL(Url.url+music.getLyric());
            URLConnection connection=url.openConnection();
            BufferedReader in =new BufferedReader(new InputStreamReader(connection.getInputStream(),"GBK"));
            String urlString = "";
            String current;
            while ((current = in.readLine()) != null) {
                urlString = urlString + current+"\r";
            }
            //写到本地
            File fp = new File("/storage/emulated/0/netease/cloudmusic/Music",lrcname);
            if (fp.exists()){
                fp.delete();
            }
            OutputStream os = new FileOutputStream(fp);
            os.write(urlString.getBytes());
            os.close();
            List<Lyric> lrc = LyricParser.loadFile(fp);
            lyricView.setLrc(lrc);

        }catch (IOException e){

        }

    }
    public void newThread(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                SearchLrc();
            }
        });
        thread.start();
    }

  private void SearchLrc(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Music music=musicServe.CurrentMusic();
                if (music==null)
                    return;
                if (music.getSinger().contains("/")){
                    String s[]=music.getSinger().split("/");
                    music.setSinger(s[0]+"."+s[1]);
                }
                Log.i("music--------------",music.getSongname());
                String lrcname=music.getSinger()+"-"+music.getSongname()+".lrc";
                File fp = new File("/storage/emulated/0/netease/cloudmusic/Music",lrcname);
                if (fp.exists()){
                    List<Lyric> lrc = LyricParser.loadFile(fp);
                    lyricView.setLrc(lrc);
                    return;
                }

            try{

                String urlStr = "http://music.163.com/api/search/get/web?csrf_token=hlpretag=&hlposttag=&s="+music.getSinger()+"-"+music.getSongname()+"&type=1&offset=0&total=true&limit=1";
                URL url=new URL(urlStr);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
               BufferedReader br=new BufferedReader(
                        new InputStreamReader(input));
                String line;
               StringBuilder sb=new StringBuilder();
                while ((line=br.readLine())!=null)
                {
                    sb.append(line);
                }
                input.close();
                Log.i("lrctest------------",sb.toString());
                JSONObject jsonObject=new JSONObject(sb.toString());
                JSONObject jsonObject1=new JSONObject(jsonObject.getString("result"));
                JSONArray jsonArray=new JSONArray(jsonObject1.getString("songs"));
               String id= new JSONObject(jsonArray.getString(0)).getString("id");
               String lrcurl="https://music.163.com/api/song/lyric?id="+id+"&lv=1&kv=1&tv=-1";
                URL Lrcurl=new URL(lrcurl);
                HttpURLConnection connection1=(HttpURLConnection) Lrcurl.openConnection();
                connection1.connect();
                InputStream input1 = connection1.getInputStream();
                BufferedReader br1=new BufferedReader(
                        new InputStreamReader(input1));
                String line1;
                StringBuilder sb1=new StringBuilder();
                while ((line1=br1.readLine())!=null)
                {
                    sb1.append(line1);
                }
                JSONObject jsonObject3=new JSONObject(sb1.toString());
                JSONObject jsonObject4=new JSONObject(jsonObject3.getString("lrc"));
                String lyr=jsonObject4.getString("lyric");
                Log.i("Lyric-------",lyr);

                OutputStream os = new FileOutputStream(fp);
                os.write(lyr.getBytes());
                os.close();
                List<Lyric> lrc = LyricParser.loadFile(fp);
                lyricView.setLrc(lrc);


            }catch (Exception e){
            e.printStackTrace();
            }
            }
        }).start();
   }

    @Override
    public void ListenerChange() {
        SearchLrc();
    }

    @Override
    public void ListenerStop() {

    }
}
