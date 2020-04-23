package com.example.myapplication.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.DownloadBean;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Util.AndroidDownloadManger;
import com.example.myapplication.Util.AutoMarqueeTextView;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AddDownloadAdapter extends RecyclerView.Adapter<AddDownloadAdapter.ViewHolder> {
  public static Timer downloadtimer;
    Context context;
    List<DownloadBean> downloadBeanList;
    TextView downloadpercent;
    long downloadid;
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1)
            downloadpercent.setText(Integer.toString(AndroidDownloadManger.getDownloadPercent(downloadid))+"%");
            return true;
        }
    });
   public AddDownloadAdapter(Context context,List<DownloadBean> downloadBeanList){
        this.context=context;
        this.downloadBeanList=downloadBeanList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        AutoMarqueeTextView downloadname;
        SeekBar downloadbar;
        LinearLayout downloadlinear;
        public ViewHolder(View view) {
            super(view);
            downloadname=view.findViewById(R.id.downloadname);
            downloadbar=view.findViewById(R.id.downloadbar);
            downloadpercent=view.findViewById(R.id.downloadpercent);
            downloadlinear=view.findViewById(R.id.downloadlinear);
        }

    }

    @NonNull
    @Override
    public AddDownloadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_order,parent,false);
       ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AddDownloadAdapter.ViewHolder holder, int position) {
        final DownloadBean downloadBean=downloadBeanList.get(position);
        holder.downloadname.setText(downloadBean.getDownloadname());
        holder.downloadbar.setEnabled(false);
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                holder.downloadbar.setProgress(AndroidDownloadManger.getDownloadPercent(downloadBean.getDownloadid()));
                downloadid=downloadBean.getDownloadid();
                handler.sendEmptyMessage(1);
            }
        };
        downloadtimer=new Timer();
        downloadtimer.schedule(timerTask,10,10);
      /*  holder.downloadlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        holder.downloadlinear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("确定删除该下载吗？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AndroidDownloadManger.downloadManager.remove(downloadBean.getDownloadid());
                        MainActivity.downloadBeanList.remove(downloadBean);
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
       // timer.cancel();关闭计时器执行
    }

    @Override
        public int getItemCount() {
            return downloadBeanList.size();
        }
    }
