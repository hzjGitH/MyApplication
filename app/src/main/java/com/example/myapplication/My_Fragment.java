package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Util.Url;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.myapplication.MainActivity.bitmap;
import static com.example.myapplication.MainActivity.headurl;
import static com.example.myapplication.MainActivity.recentlyList;
import static com.example.myapplication.MainActivity.username;

public class My_Fragment extends Fragment implements ChangeView {

    private static MusicAdapter adapter = null;
    LinearLayout mylike;
    LinearLayout localmusic;
    LinearLayout downmanager;
    Handler handler;//更新用
   private CircleImageView headview;
   private TextView usertext;
boolean init=false;

    public static Handler myhandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 2 && adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    });

    My_Fragment(Handler handler) {
        this.handler = handler;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.my_fragment, container, false);
        RecyclerView rencently_rec = view.findViewById(R.id.lishi_rec);
        localmusic = view.findViewById(R.id.localmusic);
        mylike = view.findViewById(R.id.mylike);
        downmanager = view.findViewById(R.id.download);
        headview=view.findViewById(R.id.headview);
        usertext=view.findViewById(R.id.username);
        ImageView reset=view.findViewById(R.id.reset);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        rencently_rec.setLayoutManager(manager);
        adapter = new MusicAdapter(recentlyList, handler, view.getContext());
        rencently_rec.setAdapter(adapter);
        mylike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MyLoveActivity.class);
                startActivity(intent);
            }
        });
        localmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), LocalMusicActivity.class);
                startActivity(intent);
            }
        });
        downmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), DownloadManagerActivity.class);
                startActivity(intent);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentlyList.clear();
                adapter.notifyDataSetChanged();
                handler.sendEmptyMessage(6);
            }
        });
        init=true;
        return view;
    }

    @Override
    public void Change() {
        if (username!=null&&init){
            Glide.with(getActivity()).load(Url.url+headurl).into(headview);
            usertext.setText(username);
        }
    }

    @Override
    public void onResume() {
        if (username!=null){
            Glide.with(getActivity()).load(Url.url+headurl).into(headview);
            usertext.setText(username);
        }
        super.onResume();
    }
}
