package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Adapter.SkinAdapter;
import com.example.myapplication.Bean.DownloadBean;
import com.example.myapplication.Bean.Music;
import com.example.myapplication.Util.BaseUtil;
import com.example.myapplication.Util.CircleImageView;
import com.example.myapplication.Util.CustomDialog;
import com.example.myapplication.Util.Url;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public static String username =null;//用户名字
    public static String id = null;//用户id
    public static List<Music> recentlyList = new ArrayList<>();
    public static List<Music> likelist = new ArrayList<>();
    public static String reply = "";
    public static List<DownloadBean> downloadBeanList = new ArrayList<>();
    public static List<DownloadBean> finishdownloadlist = new ArrayList<>();
    public static Map<String, Integer> RecordMap = new HashMap<>();
   public static Bitmap bitmap;
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
    LinearLayout tabliner;
    TextView after_login;//成功登陆显示用户名字
    CircleImageView headview;
    //对应三个不同的Fragment
    private Fragment community_Fragment;
    private Fragment search_Fragment;
    private Fragment my_Fragment;
    LayoutInflater layoutInflater;
    static MusicServe musicServe = new MusicServe();//音乐服务
    MusicConnector conn;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static String info = "借";//歌曲名字
    public static String singer = "毛不易";//歌手名字
    public String playinfo;//歌曲+歌手名字
    static String path = "";//路径
    public static String color="";
    SharedPreferences sharedPreferences;
    SharedPreferences record_play;
    Timer timer;
    CustomDialog dialog1;
   public static String headurl;
private boolean userchanger=false;

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
                                headurl =  jsonObject.getString("headurl");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            bitmap = getBitmap(Url.url+headurl);
                                            handler.sendEmptyMessage(5);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            String musictype = jsonObject.getString("musictype");
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
                            Log.i("RecordMap", RecordMap.toString());
                            if (RecordMap.get(musictype) == 0)//第一次登陆赋值一个用户注册选择的音乐类型基值20用于推荐音乐
                                RecordMap.put(musictype, 20);

                           Search_Fragment.gettuijian=true;
                          search_Fragment.onResume();
                            dialog1.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
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
                        dialog1.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    songinfo.setText(info);
                    control_play.setImageResource(R.drawable.play);
                    break;
                case 4:
                    getWindow().setStatusBarColor(Color.parseColor(color));
                    navigationView.setBackgroundColor(Color.parseColor(color));
                    //navigationView.setItemBackgroundResource();
                    tabliner.setBackgroundColor(Color.parseColor(color));
                    break;
                case 5:
                    headview.setImageBitmap(bitmap);
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
        username=null;
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
            //去重
            Set<Music> set=new HashSet<>(recentlyList);
            recentlyList=new ArrayList<>(set);
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
                seekBar.setMax(toal_time);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

               Bundle bundle= musicServe.nextmusic();
               if (bundle!=null)
              songinfo.setText(bundle.getString("songname")+"-"+bundle.getString("singer"));
            }
        });
        Timer();//开启计时器

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
        headview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeHeadViewPopWin();
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
                     dialog1=new CustomDialog(MainActivity.this,"登陆中..");
                     dialog1.show();
                    login_username = user_text.getText().toString();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("username", login_username);
                    map.put("password", password_text.getText().toString());
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Gson gson = new Gson();
                    String data = gson.toJson(map);
                    data= BaseUtil.encryptPassword(data);
                    RequestBody requestBody = RequestBody.create(JSON, data);
                    Request request = new Request.Builder().post(requestBody)
                            .url(Url.url + "login/servlet/LoginServlet")
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            dialog1.dismiss();
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
                                dialog1=new CustomDialog(MainActivity.this,"注册中...");
                                dialog1.show();
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
        tabliner=findViewById(R.id.tabliner);
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
        headview=nav_head_view.findViewById(R.id.headview);
        after_login = nav_head_view.findViewById(R.id.after_login);
        login = nav_head_view.findViewById(R.id.login);
        register = nav_head_view.findViewById(R.id.register);
        Community.setOnClickListener(this);
        Myself.setOnClickListener(this);
        Search.setOnClickListener(this);
        setFragment(1);
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent=new Intent(MainActivity.this,AboutMyActivity.class);
                             startActivity(intent);
                        break;
                    case R.id.item2:
                        ChangeSkinColorPopWin();
                        break;
                    case R.id.item3:
                        if (username!=null){
                            Intent intent1=new Intent(MainActivity.this,MydynamicActivity.class);
                            startActivity(intent1);
                        }else {
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.item4:
                        Toast.makeText(MainActivity.this, "已退出应用", Toast.LENGTH_SHORT).show();
                        finish();
                        musicServe.onDestroy();
                        //timer.cancel();
                        break;
                    case R.id.item5:
                        ShowLoginDialog();
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    public void Timer() {
        //记时器刷新进度条
         timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (userchanger){
                    userchanger=false;
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    seekBar.setMax(mediaPlayer.getDuration() / 1000);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                }
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    public void closTimer() {
        timer.cancel();
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
                    search_Fragment = new Search_Fragment(handler,MainActivity.this);
                    fragmentTransaction.add(R.id.container, search_Fragment, "Search_Fragment");
                } else {
                    fragmentTransaction.show(search_Fragment);
                }
                break;
            case 2:
                Community.setTextSize(25);
                if (community_Fragment == null) {
                    community_Fragment = new Community_Fragment(MainActivity.this, this);
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
         String register_data = gson.toJson(map);
        register_data=BaseUtil.encryptPassword(register_data);
        RequestBody requestBody = RequestBody.create(JSON, register_data);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().post(requestBody)
                .url(Url.url+"login/servlet/RegistServlet")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dialog1.dismiss();
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
    public void  ChangeSkinColorPopWin(){
      final   SharedPreferences sharedPreferences=getSharedPreferences("colors",MODE_PRIVATE);
     final    SharedPreferences.Editor editor=sharedPreferences.edit();

        View popView = View.inflate(MainActivity.this, R.layout.changeskin, null);
        TextView cancel=popView.findViewById(R.id.cancel);
        TextView change=popView.findViewById(R.id.change);
        RecyclerView skin_rec=popView.findViewById(R.id.skin_rec);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        skin_rec.setLayoutManager(linearLayoutManager);
        skin_rec.setAdapter(new SkinAdapter(MainActivity.this,handler));
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels  / 4;
        final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        popupWindow.setAnimationStyle(R.style.popup_ani);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 10);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                editor.putString("newcolor",SkinAdapter.newcolor);
                editor.apply();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color= sharedPreferences.getString("newcolor","#fff");
                handler.sendEmptyMessage(4);
                popupWindow.dismiss();
            }
        });
    }
private void ChangeHeadViewPopWin(){
        View view=View.inflate(this,R.layout.head_pop,null);
        Button change_head=view.findViewById(R.id.change_head);
        Button cancel=view.findViewById(R.id.pop_cancel);
        int weight = getResources().getDisplayMetrics().widthPixels;
         int height = getResources().getDisplayMetrics().heightPixels / 5;
       final   PopupWindow popupWindow = new PopupWindow(view, weight, height);
        popupWindow.setAnimationStyle(R.style.popup_ani);
        popupWindow.setFocusable(true);
    //点击外部popueWindow消失
    popupWindow.setOutsideTouchable(true);
    //popupWindow消失屏幕变为不透明
    change_head.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1001);
            popupWindow.dismiss();
        }
    });
    cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
        }
    });
    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        }
    });
    //popupWindow出现屏幕变为半透明
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.alpha = 0.5f;
    getWindow().setAttributes(lp);
    popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 10);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK&&requestCode==1001&&data!=null){
            try {
                Uri uri=data.getData();
                Bitmap bitmap =getBitmapFormUri(uri);
                File file=new File(getFilesDir(),"touxiang.png");
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                headview.setImageBitmap(bitmap);
                if (username!=null){
                    String touxiang= BaseUtil.encodeImage(file.getPath());
                    UploadHead(touxiang);
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public Bitmap getBitmapFormUri(Uri uri) throws IOException {
        InputStream input =getContentResolver().openInputStream(uri);
        // InputStream input=getActivity().openFileInput(uri.toString());
        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;//不加载到内存
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以600x480为标准
        float hh = 600f;//这里设置高度为600f
        float ww = 600f;//这里设置宽度为600f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

         input =getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options <= 0)
                break;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    public void UploadHead(String touxiang){
        OkHttpClient okHttpClient=new OkHttpClient();
        Map<String,String> map=new HashMap<>();
        map.put("username",username);
        map.put("touxiang",touxiang);
        Gson gson=new Gson();
        RequestBody requestBody=RequestBody.create(JSON,gson.toJson(map));
        final Request request=new Request.Builder().post(requestBody).url(Url.url+"login/servlet/ChangeHeadViewServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("headURL----",response.body().string());
            }
        });
    }
    public static Bitmap getBitmap(String path) throws IOException{

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = conn.getInputStream();
        return BitmapFactory.decodeStream(inputStream);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Timer();
        playinfo=info+"-"+singer;
        songinfo.setText(playinfo);
        if (mediaPlayer.isPlaying()) {
            control_play.setImageResource(R.drawable.play);
        }
        else {
            control_play.setImageResource(R.drawable.pause);
        }
        SharedPreferences sharedPreferences=getSharedPreferences("colors",MODE_PRIVATE);
        String color= sharedPreferences.getString("newcolor","#ffffff");
        Log.i("colors",color);
        if (!color.equals("#ffffff")) {
            getWindow().setStatusBarColor(Color.parseColor(color));
            navigationView.setBackgroundColor(Color.parseColor(color));
            tabliner.setBackgroundColor(Color.parseColor(color));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recentlyList.size() != 0) {
            //去重
            Set<Music> set=new HashSet<>(recentlyList);
            recentlyList=new ArrayList<>(set);
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
        if (username != null) {
            Log.i("record_play", "执行存储");
            Log.i("username", username);
            SharedPreferences.Editor editor = record_play.edit();
            for (Map.Entry<String, Integer> map : RecordMap.entrySet()) {
                editor.putInt(map.getKey(), map.getValue());
            }
            editor.apply();
        }
        closTimer();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        Log.i("MainActivity","服务解绑");
    }

}
