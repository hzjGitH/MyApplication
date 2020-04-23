package com.example.myapplication;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.FinishdownloadAdapter;
import com.example.myapplication.Bean.DownloadBean;

import java.util.List;


public class FinishDownloadFragment extends Fragment {
 RecyclerView finish_rec;
Context context;
public static FinishdownloadAdapter finishdownloadAdapter;

    public FinishDownloadFragment(Context context) {
        this.context=context;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_down_manager, container, false);
        finish_rec=view.findViewById(R.id.download_order);
        LinearLayoutManager manager =new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        finish_rec.setLayoutManager(manager);
        finishdownloadAdapter=new FinishdownloadAdapter(context,MainActivity.finishdownloadlist);
        finish_rec.setAdapter(finishdownloadAdapter);

        return  view;
    }

}
