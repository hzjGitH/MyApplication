package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.AddDownloadAdapter;


public class DownManagerFragment extends Fragment {
private RecyclerView download_rec;
private Context context;
  public  static   AddDownloadAdapter addDownloadAdapter;

    public DownManagerFragment(Context context) {
    this.context=context;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addDownloadAdapter=new AddDownloadAdapter(context,MainActivity.downloadBeanList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_down_manager, container, false);
        download_rec=view.findViewById(R.id.download_order);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        download_rec.setLayoutManager(manager);
        download_rec.setAdapter(addDownloadAdapter);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

}
