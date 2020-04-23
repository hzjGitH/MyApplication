package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Bean.DownloadBean;
import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.Url;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    public static String username = null;//用户名字
    public static String id = null;//用户id
    public static List<Music> recentlyList = new ArrayList<>();
    public static List<Music> likelist = new ArrayList<>();
    public static String reply = "";
    public static List<DownloadBean> downloadBeanList = new ArrayList<>();
    public static List<DownloadBean> finishdownloadlist = new ArrayList<>();
    public static Map<String, Integer> RecordMap = new HashMap<>();
    String login_username;
    ImageView menu;
    ImageView control_play;//播放开关
    ImageView music_log;
    ImageView search;//搜索图标
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    RelativeLayout relativeLayout;
    //三个Fragment索引
    TextView Community;
    TextView Myself;
    TextView Search;
    //登陆注册
    TextView login;
    TextView register;
    TextView songinfo;//播放信息
    View nav_head_view;
    SeekBar seekBar;//进度条
    Spinner spinner_user;//用户类型下拉框
    Spinner spinner_music;//music类型下拉框
    LinearLayout beforelayout;//未登陆之前的布局
    LinearLayout recommendlayout;
    TextView after_login;//成功登陆显示用户名字
    //对应三个不同的Fragment
    private Fragment community_Fragment;
    private Fragment search_Fragment;
    private Fragment my_Fragment;
    LayoutInflater layoutInflater;
    static MusicServe musicServe = new MusicServe();//音乐服务
    MusicConnector conn;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static String info = "";//歌曲名字
    public static String singer = "";//歌手名字
    static String path = "";//路径
    SharedPreferences sharedPreferences;
    SharedPreferences record_play;
  private   Timer timer;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    try {
                        JSONObject jsonObject = new JSONObject(bundle.get("loginbundle").toString());
                        String info = jsonObject.get("info").toString();

                        if (jsonObject.get("status").toString().equals("true")) {
                            id = jsonObject.getString("id");
                            String musictype=jsonObject.getString("musictype");
                            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                            username = login_username;
                            beforelayout.setVisibility(View.GONE);
                            after_login.setVisibility(View.VISIBLE);
                            after_login.setText(username);
                            record_play = getSharedPreferences(username, Context.MODE_PRIVATE);
                            RecordMap.put("流行音乐", record_play.getInt("流行音乐", 0));
                            RecordMap.put("摇滚音乐", record_play.getInt("摇滚音乐", 0));
                            RecordMap.put("纯音乐", record_play.getInt("纯音乐", 0));
                            RecordMap.put("经典音乐", record_play.getInt("经典音乐", 0));
                            RecordMap.put("说唱音乐", record_play.getInt("说唱音乐", 0));
                            RecordMap.put("民族音乐", record_play.getInt("民族音乐", 0));
                            RecordMap.put("电子音乐", record_play.getInt("电子音乐", 0));
                            Log.i("RecordMap",RecordMap.toString());
                            if(RecordMap.get(musictype)==0)//第一次登陆赋值一个用户注册选择的音乐类型基值20用于推荐音乐
                                RecordMap.put(musictype,20);
                        } else {
                            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Bundle bundle1 = msg.getData();
                    try {
                        JSONObject jsonObject = new JSONObject(bundle1.get("register_info").toString());
                        String info = jsonObject.getString("info");

                        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    songinfo.setText(info);
                    control_play.setImageResource(R.drawable.play);
                    break;
            }
            return false;
        }
    });

    private class MusicConnector implements ServiceConnection {
        //成功绑定时调用 即bindService（）执行成功同时返回非空Ibinder对象
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            musicServe = ((MusicServe.MyMusicBinder) iBinder).getService();
        }

        //不成功绑定时调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicServe = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.KILL_BACKGROUND_PROCESSES}, 200);
        }

        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("recentlyList", Context.MODE_PRIVATE);
        int size = sharedPreferences.getInt("recentlyListLength", 0);
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                String json = sharedPreferences.getString("recentlyList" + i, null);
                recentlyList.add(gson.fromJson(json, Music.class));
            }
        }
        //取出用户使用的历史记录（播放，下载，添加喜欢，搜索）

        init();//初始化控件
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MusicServe.class);
        conn = new MusicConnector();
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Url.url + "music/popularmusic/毛不易-借.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                int toal_time;
                toal_time = mp.getDuration();//毫秒换成秒
                String time = Integer.toString(toal_time / 1000);
                seekBar.setMax(toal_time);
                //  music_time.setText(time+"s");
            }
        });
//记时器刷新进度条
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                }
            }
        };
        timer.schedule(timerTask, 0,1000);

        layoutInflater = LayoutInflater.from(MainActivity.this);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        control_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    control_play.setImageResource(R.drawable.play);

                    RotateAnimation(music_log);
                } else {
                    mediaPlayer.pause();
                    control_play.setImageResource(R.drawable.pause);

                }

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLoginDialog();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRegisterDialog();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);

                startActivity(intent);
            }
        });

    }

    private void ShowLoginDialog() {
        final View login_view = layoutInflater.inflate(R.layout.login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.spinnerstyle);
        builder.setView(login_view).setCancelable(true).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText user_text = login_view.findViewById(R.id.username);
                EditText password_text = login_view.findViewById(R.id.password);
                if (user_text.getText().toString().isEmpty() && password_text.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "帐号和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    login_username = user_text.getText().toString();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("username", login_username);
                    map.put("password", password_text.getText().toString());
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Gson gson = new Gson();
                    String data = gson.toJson(map);
                    RequestBody requestBody = RequestBody.create(JSON, data);
                    Request request = new Request.Builder().post(requestBody)
                            .url(Url.url + "login/servlet/LoginServlet")
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            System.out.println("Response++");
                            Message message = new Message();
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("loginbundle", response.body().string());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(1000, 850);
    }

    private void ShowRegisterDialog() {
        final View register_view = layoutInflater.inflate(R.layout.register, null);
        spinner_user = register_view.findViewById(R.id.spinner_user);
        spinner_music = register_view.findViewById(R.id.spinner_music);
        final EditText register_user = register_view.findViewById(R.id.register_username);
        final EditText register_password = register_view.findViewById(R.id.register_password);
        final EditText repeat_password = register_view.findViewById(R.id.repeat_password);
        //用户类型下拉框添加适配器
        String[] user_list = getResources().getStringArray(R.array.user_array);
        ArrayAdapter<String> user_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, user_list);
        user_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_user.setAdapter(user_adapter);
        //音乐类型下拉框添加适配器
        String[] music_list = getResources().getStringArray(R.array.music_array);
        ArrayAdapter<String> music_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, music_list);
        music_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_music.setAdapter(music_adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.spinnerstyle)
                .setView(register_view).setCancelable(true).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String user = register_user.getText().toString();
                        String password = register_password.getText().toString();
                        String repeat = repeat_password.getText().toString();
                        String usertype = spinner_user.getSelectedItem().toString();
                        String musictype = spinner_music.getSelectedItem().toString();
                        if (user != null && password != null && repeat != null) {
                            if (!password.equals(repeat)) {
                                Toast.makeText(MainActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                            } else {
                                RegisterUser(user, password, usertype, musictype);//发送注册信息
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "注册信息不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(900, 1050);
    }

    private void init() {
        songinfo = findViewById(R.id.songinfo);
        search = findViewById(R.id.search);
        music_log = findViewById(R.id.log);
        control_play = findViewById(R.id.control_play);
        seekBar = findViewById(R.id.seekbar);
        menu = findViewById(R.id.menu);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawerlayout);
        Community = findViewById(R.id.community);
        relativeLayout = findViewById(R.id.relative);
        Myself = findViewById(R.id.my);
        Search = findViewById(R.id.find);
        recommendlayout = findViewById(R.id.recommend);
        nav_head_view = navigationView.getHeaderView(0);
        beforelayout = nav_head_view.findViewById(R.id.before);
        after_login = nav_head_view.findViewById(R.id.after_login);
        login = nav_head_view.findViewById(R.id.login);
        register = nav_head_view.findViewById(R.id.register);
        Community.setOnClickListener(this);
        Myself.setOnClickListener(this);
        Search.setOnClickListener(this);
        setFragment(1);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item2:
                        Toast.makeText(MainActivity.this, "主题", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item4:
                        Toast.makeText(MainActivity.this, "退出", Toast.LENGTH_SHORT).show();
                        finish();
                        musicServe.onDestroy();
                        timer.cancel();
                        break;

                }

                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.community:
                setFragment(2);
                break;
            case R.id.find:
                setFragment(1);
                break;
            case R.id.my:
                setFragment(0);
                break;
        }
    }

    public void setFragment(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            default:
                break;
            case 0:
                Myself.setTextSize(25);
                if (my_Fragment == null) {
                    my_Fragment = new My_Fragment(handler);
                    fragmentTransaction.add(R.id.container, my_Fragment, "My_Fragment");
                } else {
                    fragmentTransaction.show(my_Fragment);
                }
                break;
            case 1:
                Search.setTextSize(25);
                if (search_Fragment == null) {
                    search_Fragment = new Search_Fragment(handler);
                    fragmentTransaction.add(R.id.container, search_Fragment, "Search_Fragment");
                } else {
                    fragmentTransaction.show(search_Fragment);
                }
                break;
            case 2:
                Community.setTextSize(25);
                if (community_Fragment == null) {
                    community_Fragment = new Community_Fragment(MainActivity.this);
                    fragmentTransaction.add(R.id.container, community_Fragment, "Community_Fragment");
                } else {
                    fragmentTransaction.show(community_Fragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    public void hideFragments(FragmentTransaction fragmentTransaction) {
        if (community_Fragment != null) {
            //隐藏Fragment
            fragmentTransaction.hide(community_Fragment);
            //将对应菜单栏设置为默认状态
            //     Community.setTextColor(getResources().getColor(R.color.darkgrey));
            Community.setTextSize(20);
        }
        if (my_Fragment != null) {
            //隐藏Fragment
            fragmentTransaction.hide(my_Fragment);
            //将对应菜单栏设置为默认状态
            //   Myself.setTextColor(getResources().getColor(R.color.darkgrey));
            Myself.setTextSize(20);
        }
        if (search_Fragment != null) {
            //隐藏Fragment
            fragmentTransaction.hide(search_Fragment);
            //将对应菜单栏设置为默认状态
            //       Search.setTextColor(getResources().getColor(R.color.darkgrey));
            Search.setTextSize(20);
        }
    }

    private void RegisterUser(String username, String passwrod, String usertype, String musictype) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", passwrod);
        map.put("usertype", usertype);
        map.put("musictype", musictype);
        Gson gson = new Gson();
        final String register_data = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON, register_data);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().post(requestBody)
                .url("http://192.168.1.19:8080/myproject/login/servlet/RegistServlet")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("register_info", response.body().string());
                Message message = new Message();
                message.what = 2;
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    public void RotateAnimation(ImageView view) {
        ObjectAnimator discObjectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
        discObjectAnimator.setDuration(5000);
        //使ObjectAnimator动画匀速平滑旋转
        discObjectAnimator.setInterpolator(new LinearInterpolator());
        //无限循环旋转
        discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        discObjectAnimator.setRepeatMode(ValueAnimator.RESTART);

    }

    @Override
    protected void onResume() {
        super.onResume();
        songinfo.setText(info);
        if (mediaPlayer.isPlaying()) {
            control_play.setImageResource(R.drawable.play);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recentlyList.size() != 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.remove("recentlyList");
            editor.putInt("recentlyListLength", recentlyList.size());
            System.out.println("DESTROP" + recentlyList.size());
            for (int i = 0; i < recentlyList.size(); i++) {
                String data = gson.toJson(recentlyList.get(i));
                editor.putString("recentlyList" + i, data);
            }
            editor.apply();
        }
        if (username!=null)
        {
            Log.i("record_play","执行存储");
            SharedPreferences.Editor editor = record_play.edit();
            for (Map.Entry<String,Integer> map:RecordMap.entrySet()){
                editor.putInt(map.getKey(),map.getValue());
            }
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

}
