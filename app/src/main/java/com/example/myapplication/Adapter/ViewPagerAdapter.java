package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
//主页Viewpager适配器
public class ViewPagerAdapter extends PagerAdapter {
        private String[] resIds = {
                "http://192.168.0.101:8080/myproject/viewpage/1.jpg",
                "http://192.168.0.101:8080/myproject/viewpage/2.jpg",
                "http://192.168.0.101:8080/myproject/viewpage/3.jpg",
                "http://192.168.0.101:8080/myproject/viewpage/4.jpg",
                "http://192.168.0.101:8080/myproject/viewpage/5.jpg",
        };

        private Context context ;
        public   ViewPagerAdapter(Context context) {
            this.context = context ;
        }

        @Override
        public int getCount() {
            return resIds.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.vp_item, null);
            ImageView imageView =  view.findViewById(R.id.imageview);
            Glide.with(context)
                    .load(resIds[position])
                    .into(imageView);
//        imageView.setImageResource(resIds[position]);
            //添加到容器中
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

    }

