package com.example.myapplication.Adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.Picture;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
   private List<Picture> pictureList;

  public   PictureAdapter( List<Picture> pictureList){
        this.pictureList=pictureList;
    }

   public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView picture;
      public ViewHolder(View view){
           super(view);
           picture=view.findViewById(R.id.picture);
       }
   }

    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PictureAdapter.ViewHolder holder, int position) {
            holder.picture.setImageBitmap(pictureList.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }
}
