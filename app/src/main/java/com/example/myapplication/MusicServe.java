package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.myapplication.Bean.Localmusic;
import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.Url;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.example.myapplication.MainActivity.info;
import static com.example.myapplication.MainActivity.path;
import static com.example.myapplication.MainActivity.singer;

public class MusicServe extends Service {
    IBinder binder;
    MediaPlayer mediaPlayer = new MediaPlayer();
    List<Music> musicList;
    List<Localmusic> list;
    int index;

   public static boolean localmusic = false;

    public MusicServe() {

    }

    class MyMusicBinder extends Binder {
        //返回Service对象
        MusicServe getService() {
            return MusicServe.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    //封装播放网络部分
    public void play(MediaPlayer mediaPlayer, List<Music> musicList, int index) {
        this.list = null;
        this.mediaPlayer = mediaPlayer;
        this.musicList = musicList;
        this.index = index;
        localmusic = false;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }



    //本地音乐的播放
    public void play(MediaPlayer mediaPlayer, List<Localmusic> List, int index, boolean t) {
        this.musicList = null;
        this.mediaPlayer = mediaPlayer;
        this.list = List;
        this.index = index;
        localmusic = true;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //封装暂停
    public void pause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    //返回MediaPlayer对象
    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayer;
    }

    //封装停止播放
    public void stop() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();//释放资源
    }

    //网络音乐调用
    public Bundle nextmusic() {
        Bundle bundle = new Bundle();
        if (localmusic) {
            bundle = nextmusic(1);
        } else {
            if (index != musicList.size() - 1) {
                index++;
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(Url.url + musicList.get(index).getPath());
                    info = musicList.get(index).getSongname();
                    singer = musicList.get(index).getSinger();
                    bundle.putString("songname", info);
                    bundle.putString("singer", singer);
                    System.out.println("下一首：" + musicList.get(index).getPath());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });

            }
        }

        return bundle;

    }

    //本地音乐调用
    public Bundle nextmusic(int i) {
        Bundle bundle = new Bundle();
        if (index != list.size() - 1) {
            index++;
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(list.get(index).getPath());
                info = list.get(index).getSongname();
                singer = list.get(index).getSinger();
                bundle.putString("songname", info);
                bundle.putString("singer", singer);
                System.out.println("下一首：" + list.get(index).getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        }
        return bundle;
    }

    //网络部分调用
    public Bundle premusic() {
        Bundle bundle = new Bundle();
        if (localmusic) {
            bundle = premusic(1);
        } else {
            if (index != 0) {
                index--;
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(Url.url + musicList.get(index).getPath());
                    info = musicList.get(index).getSongname();
                    singer = musicList.get(index).getSinger();
                    path = musicList.get(index).getPath();
                    bundle.putString("songname", info);
                    bundle.putString("singer", singer);
                    bundle.putString("path", path);
                    System.out.println("下一首：" + musicList.get(index).getPath());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });

            }
        }
        return bundle;
    }

    //本地音乐部分调用，随便传个整形i
    public Bundle premusic(int i) {
        Bundle bundle = new Bundle();
        if (index != 0) {
            index--;
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(list.get(index).getPath());
                info = list.get(index).getSongname();
                singer = list.get(index).getSinger();

                bundle.putString("songname", info);
                bundle.putString("singer", singer);
                bundle.putString("path", path);
                System.out.println("下一首：" + list.get(index).getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        }
        return bundle;
    }


    //返回当前播放的歌曲信息
    public Music CurrentMusic() {
        if (list != null) {
            return null;
        }
        return musicList.get(index);
    }
}
