package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
//歌单对应的Activity
public class SongsheetActivity extends AppCompatActivity {
TextView titleview;
    TabLayout tabLayout;
    List<String> mtitle;
    List<Fragment> fragments;
ViewPager viewPager;
ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songsheet);
        titleview=findViewById(R.id.title);
        titleview.setText("歌单");
        tabLayout=findViewById(R.id.tablelayout);

        viewPager=findViewById(R.id.music_vp);
        mtitle=new ArrayList<>();
        mtitle.add("流行");
        mtitle.add("经典");
        mtitle.add("电子");
        mtitle.add("Rap");
        mtitle.add("摇滚");
        mtitle.add("民谣");
        mtitle.add("纯音乐");
        fragments=new ArrayList<>();
        fragments.add(new SongsheetFragment("Popular",SongsheetActivity.this));
        fragments.add(new SongsheetFragment("Classical",SongsheetActivity.this));
        fragments.add(new SongsheetFragment("Electronic",SongsheetActivity.this));
        fragments.add(new SongsheetFragment("Rap",SongsheetActivity.this));
        fragments.add(new SongsheetFragment("Rock",SongsheetActivity.this));
        fragments.add(new SongsheetFragment("Folk",SongsheetActivity.this));
        fragments.add(new SongsheetFragment("Absolute",SongsheetActivity.this));

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

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mtitle.get(position);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        /*tabLayout.addTab(tabLayout.newTab().setText("流行"));
        tabLayout.addTab(tabLayout.newTab().setText("经典"));
        tabLayout.addTab(tabLayout.newTab().setText("电子"));
        tabLayout.addTab(tabLayout.newTab().setText("摇滚"));
        tabLayout.addTab(tabLayout.newTab().setText("民谣"));
        tabLayout.addTab(tabLayout.newTab().setText("Rap"));
        tabLayout.addTab(tabLayout.newTab().setText("纯音乐"));*/

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
