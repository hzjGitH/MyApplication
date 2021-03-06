package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.ImagviewAdapter;
import com.example.myapplication.Util.Url;
import com.example.myapplication.View.NineGridTestLayout;
import com.example.myapplication.comments.PublicBean;
import com.example.myapplication.comments.Commentsbean;
import com.example.myapplication.comments.Updata;
import com.google.gson.Gson;
import com.lzy.ninegrid.ImageInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.JSON;
import static com.example.myapplication.MainActivity.reply;
import static com.example.myapplication.MainActivity.username;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
private List<PublicBean> publicBeanList ;
private boolean [] first;
private Animation animation;
private Context context;
   private JSONArray photopath;
   private List<ImageInfo> infolist ;
   private List<String>  list;

   Handler handler=new Handler(new Handler.Callback() {
       @Override
       public boolean handleMessage(@NonNull Message msg) {
           notifyDataSetChanged();
           return false;
       }
   });



    public CommunityAdapter(List<PublicBean> publicBeanList,Context context){
        this.publicBeanList=publicBeanList;
        first=new boolean[publicBeanList.size()];
        this.context=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView time;//时间
        TextView content1;
        TextView uname;
        TextView count;
        TextView comment_cout;
        ImageView zan;
        ImageView delete;
        Commentsview commentsview;
        EditText get_comment;
        ImageView fabiao;
        GridView gridView;
        NineGridTestLayout layout;
        CircleImageView head_view;

        public ViewHolder(View view){
            super(view);
            time=view.findViewById(R.id.time);
            uname=view.findViewById(R.id.uname);
            zan=view.findViewById(R.id.zan);
            content1=view.findViewById(R.id.content);
            count=view.findViewById(R.id.Praise_points);
            commentsview=view.findViewById(R.id.comment_view);
            get_comment=view.findViewById(R.id.get_comment);
            fabiao=view.findViewById(R.id.fabiao);
            layout=view.findViewById(R.id.grid_layout);
            head_view=view.findViewById(R.id.user_view);
            comment_cout=view.findViewById(R.id.comment_count);
            delete=view.findViewById(R.id.delete);
            animation= AnimationUtils.loadAnimation(view.getContext(),R.anim.scale);
        }
    }
    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
         ViewHolder holder = new ViewHolder(view);

        return holder;
    }
    @Override
   public void onBindViewHolder(@NonNull final CommunityAdapter.ViewHolder holder, final int position){
        final  int index=position;
        final  ViewHolder viewHolder=holder;
        List<Commentsbean> commentlist =new ArrayList<>();
       final PublicBean publicBean=publicBeanList.get(publicBeanList.size() - position-1);
        holder.time.setText(publicBean.getTime());
        holder.count.setText(Integer.toString(publicBean.getCount()));
        holder.content1.setText(publicBean.getContent());
        holder.uname.setText(publicBean.getUsername());
        Glide.with(context).load(Url.url+publicBean.getHeadurl()).into(holder.head_view);
        try {
            list =new ArrayList<>();
            if (!publicBean.getPhoto().equals("[]")){
                System.out.println("->"+holder.getPosition()+publicBean.getPhoto());
                infolist =new ArrayList<>();
                photopath=new JSONArray(publicBean.getPhoto());
                for (int i=0;i<photopath.length();i++){
                   /* ImageInfo imageInfo=new ImageInfo();
                    imageInfo.setBigImageUrl(Url.url+photopath.get(i).toString());
                    imageInfo.setThumbnailUrl(Url.url+photopath.get(i).toString());
                    infolist.add(imageInfo);*/
                   list.add(Url.url+photopath.get(i).toString());
                }
               /* System.out.println(infolist.toString());
                if (infolist.size()>3&&infolist.size()<6){

                    LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) holder.gridView.getLayoutParams();
                    params.height=600;
                 //   params.width=holder.gridView.getWidth();
                    System.out.println("Gridview Height:"+holder.gridView.getHeight());
                    holder.gridView.setLayoutParams(params);
                }
                if (infolist.size()>6){
                    LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) holder.gridView.getLayoutParams();
                    params.height=900;
                  //  params.width=holder.gridView.getWidth();
                    holder.gridView.setLayoutParams(params);
                }*/

               // ImagviewAdapter adapter=new ImagviewAdapter(context,infolist);
               // holder.gridView.setAdapter(adapter);
            }
            holder.layout.setUrlList(list);

        }catch (JSONException e){
            e.printStackTrace();
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicBean.getUsername().equals(username)){
                    final AlertDialog.Builder alter=new AlertDialog.Builder(context);
                    alter.setTitle("确定删除该动态").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeletePublic(publicBean.getId(),position);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }
            }
        });
//发送对内容点赞和取消的信息给后端
        holder.zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!first[index]){
                    HashMap<String,String> map=new HashMap<>();
                    map.put("id",publicBean.getId());
                    System.out.println(publicBean.getId());
                    map.put("update","addpoint");
                    Updata updata=new Updata(map);
                    viewHolder.count.setText(Integer.toString(publicBean.getCount()+1));
                     first[index]=true;
                    viewHolder.zan.setAnimation(animation);
                    viewHolder.zan.startAnimation(animation);

                } else {
                    HashMap<String,String> map=new HashMap<>();
                    map.put("id",publicBean.getId());
                    map.put("update","redusepoin");
                    Updata updata=new Updata(map);
                    viewHolder.count.setText(Integer.toString(publicBean.getCount()));
                    first[index]=false;
                }

            }
        });
        //发表评论
        holder.fabiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.get_comment.getText().toString().equals("")&&username!=null) {
                    JSONObject jsonObject;
                    HashMap<String, String> commentmap = new HashMap<>();

                    jsonObject =new JSONObject(commentmap);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", publicBean.getId());
                        map.put("update", "addcomment");
                        map.put("comment",jsonObject.toString());
                        map.put("commentsuser",username);
                        map.put("content",viewHolder.get_comment.getText().toString());
                        map.put("replyuser",reply);
                        System.out.println(map);
                        new Updata(map);
                    viewHolder.get_comment.setText("");
                    Toast.makeText(v.getContext(),"评论成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(v.getContext(),"登陆且输入信息不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!publicBean.getComment().equals("")){
            try {
                Gson gson=new Gson();
                JSONArray jsonArray=new JSONArray(publicBean.getComment());
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                    Commentsbean commentsbean;
                    commentsbean=gson.fromJson(jsonObject.toString(),Commentsbean.class);
                    commentlist.add(commentsbean);
                }
                System.out.println("有多少条评论"+commentlist.size());
            }catch (JSONException e){
                e.printStackTrace();
            }
            holder.commentsview.setlist(commentlist);
            holder.comment_cout.setText(Integer.toString(commentlist.size()));
            holder.commentsview.setonitemclicklistener(new Commentsview.onitemclicklistener() {
                @Override
                public void onitemclick(int position, Commentsbean bean) {

                }
            });
            holder.commentsview.notifydatasetchanged();
        }
    }
    void DeletePublic(String id, final int position){
        OkHttpClient okHttpClient=new OkHttpClient();
        Map<String,String> map=new HashMap<>();
        map.put("id",id);
        map.put("username",username);
        Gson gson=new Gson();
        RequestBody requestBody=RequestBody.create(JSON,gson.toJson(map));
        Request request=new Request.Builder().post(requestBody).url(Url.url+"community/DeletePublicServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Looper.prepare();
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                   String result= jsonObject.getString("msg");

                    if (result.equals("success")) {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        publicBeanList.remove(position);
                        handler.sendEmptyMessage(1);
                    }
                    else
                        Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();

                }catch (JSONException e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        });
    }

    @Override
    public int getItemCount(){
        return publicBeanList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
}
