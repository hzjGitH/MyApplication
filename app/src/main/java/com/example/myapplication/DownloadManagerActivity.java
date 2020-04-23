package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DownloadManagerActivity extends AppCompatActivity {

    TextView titleview;
    TabLayout tabLayout;
    List<String> mtitle;
    List<Fragment> fragments = new ArrayList<>();
    ViewPager viewPager;
    ImageView back;
    String title = "下载管理";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        titleview = findViewById(R.id.title);
        viewPager = findViewById(R.id.vp_manager);
        back = findViewById(R.id.back);
        tabLayout = findViewById(R.id.tab_layout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleview.setText(title);
        mtitle = new ArrayList<>();
        mtitle.add("正在下载");
        mtitle.add("下载完成");
        fragments.add(new DownManagerFragment(this));
        fragments.add(new FinishDownloadFragment(this));
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
    }
}
