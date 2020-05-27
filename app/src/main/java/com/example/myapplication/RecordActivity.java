package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.AndroidDownloadManger;
import com.example.myapplication.Util.AutoMarqueeTextView;
import com.example.myapplication.Util.Url;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.myapplication.MainActivity.RecordMap;
import static com.example.myapplication.MainActivity.info;
import static com.example.myapplication.MainActivity.likelist;
import static com.example.myapplication.MainActivity.mediaPlayer;
import static com.example.myapplication.MainActivity.musicServe;
import static com.example.myapplication.MainActivity.username;

public class RecordActivity extends AppCompatActivity {

    private ImageView control;
    private ImageView back;
    private AutoMarqueeTextView songname;
    private AutoMarqueeTextView singer;
    private SeekBar seekBar;
    private TextView curr_time;
    private TextView total_time;
    private ImageView pre;
    private ImageView next;
    private ImageView addlike;
    private ImageView download;
    private ImageView commentit;
    private ViewPager viewPager;
    private Music curr_music;
    ToFragment toLyricFragment;
    ToFragment toRecordFragment;
    RecordFragment recordFragment=new RecordFragment();
    LyricFragment lyricFragment=new LyricFragment();
    List<Fragment> fragments=new ArrayList<>();
    String curr;
    String total;
    boolean userchanger;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                curr_time.setText(curr);
                total_time.setText(total);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()&&mediaPlayer!=null) {
                    mediaPlayer.start();
                    toRecordFragment.ListenerChange();
                    control.setImageResource(R.drawable.stop);
                } else {
                    toRecordFragment.ListenerStop();
                    musicServe.pause(mediaPlayer);
                    control.setImageResource(R.drawable.start);
                }
            }
        });
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = musicServe.premusic();
                songname.setText(bundle.getString("songname"));
                singer.setText(bundle.getString("singer"));
                Toast.makeText(RecordActivity.this, "上一首", Toast.LENGTH_SHORT).show();
                toLyricFragment.ListenerChange();
                toRecordFragment.ListenerChange();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = musicServe.nextmusic();
                if (bundle==null){
                    Toast.makeText(RecordActivity.this,"无资源",Toast.LENGTH_SHORT).show();
                    return;
                }
                toRecordFragment.ListenerChange();
                toLyricFragment.ListenerChange();
                songname.setText(bundle.getString("songname"));
                singer.setText(bundle.getString("singer"));
                Toast.makeText(RecordActivity.this, "下一首", Toast.LENGTH_SHORT).show();

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Bundle bundle = musicServe.nextmusic();
                if (bundle==null){
                    Toast.makeText(RecordActivity.this,"无资源",Toast.LENGTH_SHORT).show();
                    return;
                }
                toRecordFragment.ListenerChange();
                toLyricFragment.ListenerChange();
                songname.setText(bundle.getString("songname"));
                singer.setText(bundle.getString("singer"));
                Toast.makeText(RecordActivity.this, "下一首", Toast.LENGTH_SHORT).show();
            }
        });

        addlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size= likelist.size();
                likelist.add(musicServe.CurrentMusic());
                //去重
                Set<Music> set=new HashSet<>(likelist);
                likelist=new ArrayList<>(set);
                if (size!=likelist.size())//成功添加喜欢,基数加2
                {
                    Toast.makeText(RecordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                    if (username!=null){
                        RecordMap.put(musicServe.CurrentMusic().getMusictype(),RecordMap.get(musicServe.CurrentMusic().getMusictype())+2);
                    }
                } else {
                    Toast.makeText(RecordActivity.this, "已存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music music = musicServe.CurrentMusic();
                if (music != null) {
                    AndroidDownloadManger androidDownloadManger = new AndroidDownloadManger(RecordActivity.this, Url.url + music.getPath());
                    androidDownloadManger.download();
                    Toast.makeText(RecordActivity.this, "正在下载", Toast.LENGTH_SHORT).show();
                    if (username!=null)
                    RecordMap.put(music.getMusictype(),RecordMap.get(music.getMusictype())+1);
                } else {
                    Toast.makeText(v.getContext(), "该音乐已经下载", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MusicServe.localmusic) {
                    Music music = musicServe.CurrentMusic();
                    Intent intent = new Intent(RecordActivity.this, MusicCommentsActivity.class);
                    intent.putExtra("songname", music.getSongname());
                    intent.putExtra("singer", music.getSinger());
                    intent.putExtra("comments", music.getComments());
                    intent.putExtra("musictype", music.getMusictype());
                    startActivity(intent);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser&&mediaPlayer!=null){
                    Log.i("Progress-----",Integer.toString(progress*1000));
                    userchanger=fromUser;
                    mediaPlayer.seekTo(progress*1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (userchanger){
                    userchanger=false;
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    int totaltime = mediaPlayer.getDuration() / 1000;
                    int currtime = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setMax(totaltime);
                    seekBar.setProgress(currtime);
                    curr = currtime / 60 + ":" + currtime % 60;
                    total = totaltime / 60 + ":" + totaltime % 60;
                    handler.sendEmptyMessage(1);
                }
            }
        };
        timer.schedule(task, 0,1000);

    }

    private void init() {
        viewPager=findViewById(R.id.Record_viewpage);
        control = findViewById(R.id.control_play);
        songname = findViewById(R.id.songname);
        singer = findViewById(R.id.singer);
        back = findViewById(R.id.back);
        addlike = findViewById(R.id.likeit);
        download = findViewById(R.id.downloadit);
        commentit=findViewById(R.id.commentit);
        songname.setText(info);
        singer.setText(MainActivity.singer);
        seekBar = findViewById(R.id.seekbar);
        curr_time = findViewById(R.id.curr_time);
        total_time = findViewById(R.id.total_time);
        pre = findViewById(R.id.pre);
        next = findViewById(R.id.next);
        toRecordFragment=recordFragment;
        toLyricFragment=lyricFragment;
        fragments.add(recordFragment);
        fragments.add(lyricFragment);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer.isPlaying()) {
            control.setImageResource(R.drawable.stop);
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    toRecordFragment.ListenerChange();
                }
            });
        }
    }
}
