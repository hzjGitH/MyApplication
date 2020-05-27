package com.example.myapplication.Adapter;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.ViewHolder> {

  private   List<String> colors=new ArrayList<>();
  private List<Integer> themeList=new ArrayList<>();
  private int choose;
  private Context context;
  private Handler handler;
  public static String newcolor="";
  public static String oldcolor="";
  public SkinAdapter(Context context, Handler handler){
      this.context=context;
      this.handler=handler;
      colors.add("#FCFCFC");
      colors.add("#4F97E9");
      colors.add("#008080");
      colors.add("#0878E8");
      colors.add("#3dfc7a");
      colors.add("#fcf02d");
      colors.add("#e080ff");
      colors.add("#FF4500");
      colors.add("#ffb294");

  }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View skinview;

       public ViewHolder(View view){
            super(view);

           skinview=view.findViewById(R.id.skinview);
        }

    }

    @NonNull
    @Override
    public SkinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.skin,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkinAdapter.ViewHolder holder, final int position) {
      //  holder.skinview.setBackgroundColor(Color.parseColor(colors.get(position)));
        holder.skinview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            choose=position;
            notifyDataSetChanged();
            MainActivity.color=colors.get(position);
            handler.sendEmptyMessage(4);

            }
        });
        //根据是否被选中来添加不同的背景
        if(choose==position){
            holder.skinview.setBackground(ContextCompat.getDrawable(context,R.drawable.color_select));
            newcolor=colors.get(position);

        }else{
            holder.skinview.setBackground(ContextCompat.getDrawable(context,R.drawable.color_unselect));

        }
        GradientDrawable myGrad = (GradientDrawable)holder.skinview.getBackground();
        myGrad.setColor(Color.parseColor(colors.get(position)));

    }

    @Override
    public int getItemCount() {
        return colors.size();
    }


}

