package com.example.myapplication.Adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.lzy.ninegrid.ImageInfo;

import java.util.List;

public class ImagviewAdapter extends BaseAdapter {
    private Context context;
    private List<ImageInfo> list;
  public   ImagviewAdapter(Context context,List<ImageInfo> list){
        this.list=list;
        this.context=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView==null){
          convertView=View.inflate(context, R.layout.imageview,null);
          holder=new ViewHolder();
          holder.imageView=convertView.findViewById(R.id.picture);
          convertView.setTag(holder);
      }else {
          holder=(ViewHolder) convertView.getTag();
      }
        Glide.with(context).load(list.get(position).getThumbnailUrl()).placeholder(R.drawable.ic_default_image)
                .error(R.drawable.loader_error).into(holder.imageView);
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return list.size();
    }
    public class  ViewHolder{
      ImageView imageView;
    }
}
