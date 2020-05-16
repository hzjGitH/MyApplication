package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.myapplication.MainActivity.RecordMap;
import static com.example.myapplication.MainActivity.info;
import static com.example.myapplication.MainActivity.likelist;
import static com.example.myapplication.MainActivity.mediaPlayer;
import static com.example.myapplication.MainActivity.musicServe;

public class RecordActivity extends AppCompatActivity {
    private ObjectAnimator discObjectAnimator, neddleObjectAnimator;
    private ImageView needleImage;
    private ImageView control;
    private View discView;
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
    private Music curr_music;
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
                    discObjectAnimator.start();
                    neddleObjectAnimator.start();
                    control.setImageResource(R.drawable.stop);
                } else {
                    musicServe.pause();
                    discObjectAnimator.cancel();
                    neddleObjectAnimator.reverse();
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
                    RecordMap.put(musicServe.CurrentMusic().getMusictype(),RecordMap.get(musicServe.CurrentMusic().getMusictype())+2);
                else
                    Toast.makeText(RecordActivity.this,"已存在",Toast.LENGTH_SHORT).show();
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
        //最外部的半透明边线
        OvalShape ovalShape0 = new OvalShape();
        ShapeDrawable drawable0 = new ShapeDrawable(ovalShape0);
        drawable0.getPaint().setColor(0x10000000);
        drawable0.getPaint().setStyle(Paint.Style.FILL);
        drawable0.getPaint().setAntiAlias(true);
        //黑色唱片边框
        RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.disc));
        drawable1.setCircular(true);
        drawable1.setAntiAlias(true);
        //内层黑色边线
        OvalShape ovalShape2 = new OvalShape();
        ShapeDrawable drawable2 = new ShapeDrawable(ovalShape2);
        drawable2.getPaint().setColor(Color.BLACK);
        drawable2.getPaint().setStyle(Paint.Style.FILL);
        drawable2.getPaint().setAntiAlias(true);
        //最里面的图像
        RoundedBitmapDrawable drawable3 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.touxiang));
        drawable3.setCircular(true);
        drawable3.setAntiAlias(true);
        Drawable[] layers = new Drawable[4];
        layers[0] = drawable0;
        layers[1] = drawable1;
        layers[2] = drawable2;
        layers[3] = drawable3;
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        int width = 10;
        //针对每一个图层进行填充，使得各个圆环之间相互有间隔，否则就重合成一个了。
        layerDrawable.setLayerInset(0, width, width, width, width);
        layerDrawable.setLayerInset(1, width, width, width, width);
        layerDrawable.setLayerInset(2, width * 11, width * 11, width * 11, width * 11);
        layerDrawable.setLayerInset(3, width * 12, width * 12, width * 12, width * 12);

        discView = findViewById(R.id.myView);
        discView.setBackground(layerDrawable);
        needleImage = findViewById(R.id.needle);
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

        //圆盘唱片和指针的动画初始化
        discObjectAnimator = ObjectAnimator.ofFloat(discView, "rotation", 0, 360);
        discObjectAnimator.setDuration(20000);
        //使ObjectAnimator动画匀速平滑旋转
        discObjectAnimator.setInterpolator(new LinearInterpolator());
        //无限循环旋转
        discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        discObjectAnimator.setRepeatMode(ValueAnimator.RESTART);

        neddleObjectAnimator = ObjectAnimator.ofFloat(needleImage, "rotation", 0, 25);
        needleImage.setPivotX(0);
        needleImage.setPivotY(0);
        neddleObjectAnimator.setDuration(500);
        neddleObjectAnimator.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer.isPlaying()) {
            discObjectAnimator.start();
            neddleObjectAnimator.start();
            control.setImageResource(R.drawable.stop);
        }
    }
}
