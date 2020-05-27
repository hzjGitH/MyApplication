package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

public class RecordFragment extends Fragment implements ToFragment {
    public View discView;
   public ImageView needleImage;
    public ObjectAnimator discObjectAnimator, neddleObjectAnimator;
    RecordFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.recordfragment,container,false);
        init(view);
        return view;
    }
 private void init(View view){
       //最外部的半透明边线
       OvalShape ovalShape0 = new OvalShape();
       ShapeDrawable drawable0 = new ShapeDrawable(ovalShape0);
       drawable0.getPaint().setColor(0x10000000);
       drawable0.getPaint().setStyle(Paint.Style.FILL);
       drawable0.getPaint().setAntiAlias(true);
       //黑色唱片边框
       RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.disc));
       drawable1.setCircular(true);
       drawable1.setAntiAlias(true);
       //内层黑色边线
       OvalShape ovalShape2 = new OvalShape();
       ShapeDrawable drawable2 = new ShapeDrawable(ovalShape2);
       drawable2.getPaint().setColor(Color.BLACK);
       drawable2.getPaint().setStyle(Paint.Style.FILL);
       drawable2.getPaint().setAntiAlias(true);
       //最里面的图像
       RoundedBitmapDrawable drawable3 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.touxiang));
       drawable3.setCircular(true);
       drawable3.setAntiAlias(true);
       Drawable[] layers = new Drawable[4];
       layers[0] = drawable0;
       layers[1] = drawable1;
       layers[2] = drawable2;
       layers[3] = drawable3;
       LayerDrawable layerDrawable = new LayerDrawable(layers);
       int width = 10;
       //针对每一个图层进行填充，使得各个圆环之间相互有间隔，否则就重合成一个了。
       layerDrawable.setLayerInset(0, width, width, width, width);
       layerDrawable.setLayerInset(1, width, width, width, width);
       layerDrawable.setLayerInset(2, width * 11, width * 11, width * 11, width * 11);
       layerDrawable.setLayerInset(3, width * 12, width * 12, width * 12, width * 12);
     discView = view.findViewById(R.id.myView);
     needleImage = view.findViewById(R.id.needle);
     discView.setBackground(layerDrawable);

     //圆盘唱片和指针的动画初始化
     discObjectAnimator = ObjectAnimator.ofFloat(discView, "rotation", 0, 360);
     discObjectAnimator.setDuration(20000);
     //使ObjectAnimator动画匀速平滑旋转
     discObjectAnimator.setInterpolator(new LinearInterpolator());
     //无限循环旋转
     discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
     discObjectAnimator.setRepeatMode(ValueAnimator.RESTART);

     neddleObjectAnimator = ObjectAnimator.ofFloat(needleImage, "rotation", 0, 25);
     needleImage.setPivotX(0);
     needleImage.setPivotY(0);
     neddleObjectAnimator.setDuration(500);
     neddleObjectAnimator.setInterpolator(new LinearInterpolator());

   }

    @Override
    public void ListenerChange() {
            discObjectAnimator.start();
            neddleObjectAnimator.start();
    }

    @Override
    public void ListenerStop() {
        discObjectAnimator.cancel();
        neddleObjectAnimator.reverse();
    }
}
