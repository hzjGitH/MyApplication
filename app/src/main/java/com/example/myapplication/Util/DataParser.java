package com.example.myapplication.Util;

import android.os.Bundle;

import com.example.myapplication.Bean.Music;
import com.example.myapplication.comments.PublicBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

public class DataParser {

  private   List<Music> musicList=new ArrayList<>();
    private  List<PublicBean> PublicList=new ArrayList<>();
    public List<Music> Parser(Bundle bundle) {
        Gson gson=new Gson();
        if (bundle.get("musicinfo").toString()!=null)
        try {
            JSONArray jsonArray = new JSONArray(bundle.get("musicinfo").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                Music music =gson.fromJson(jsonObject.toString(),Music.class);
                musicList.add(music);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return musicList;
    }
    //用来解析推荐的数据
    public List<Music> Parser2(Bundle bundle) {
        Gson gson=new Gson();
        if (bundle.get("musicinfo").toString()!=null)
            try {
                JSONArray jsonArray = new JSONArray(bundle.get("musicinfo").toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray jsonArray1 = new JSONArray(jsonArray.get(i).toString());
                    for (int j=0; j <jsonArray1.length();j++)
                    {
                        JSONObject jsonObject = new JSONObject(jsonArray1.get(j).toString());
                    Music music =gson.fromJson(jsonObject.toString(),Music.class);
                    musicList.add(music);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        return musicList;
    }

    public List<PublicBean> PublicParser(Bundle bundle) {
        if (bundle.get("getpublicinfo").toString()!=null)
            try {
                JSONArray jsonArray = new JSONArray(bundle.get("getpublicinfo").toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    PublicBean publicBean=new PublicBean();
                    publicBean.setId(jsonObject.getString("id"));
                    publicBean.setComment(jsonObject.getString("comment"));
                    publicBean.setContent(jsonObject.getString("content"));
                    publicBean.setCount(jsonObject.getInt("count"));
                    publicBean.setTime(jsonObject.getString("time"));
                    publicBean.setUsername(jsonObject.getString("username"));
                    publicBean.setPhoto(jsonObject.getString("photo"));
                    PublicList.add(publicBean);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        return PublicList;
    }
}
