package com.example.myapplication;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.comments.Circlemovementmethod;
import com.example.myapplication.comments.Commentsbean;

import java.util.List;

import static com.example.myapplication.MainActivity.reply;
import static com.example.myapplication.MainActivity.username;

public class Commentsview extends LinearLayout {
    private Context mcontext;
    private List<Commentsbean> mdatas;
    private onitemclicklistener listener;

    public Commentsview(Context context) {
        this(context, null);
    }

    public Commentsview(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Commentsview(Context context, @Nullable AttributeSet attrs, int defstyleattr) {
        super(context, attrs, defstyleattr);
        setOrientation(VERTICAL);
        this.mcontext = context;
    }

    /**
     * 设置评论列表信息
     *
     * @param list
     */
    public void setlist(List<Commentsbean> list) {
        mdatas = list;
    }

    public void setonitemclicklistener(onitemclicklistener listener) {
        this.listener = listener;
    }

    public void notifydatasetchanged() {
        removeAllViews();
        if (mdatas == null || mdatas.size() <= 0) {
            return;
        }
        LayoutParams layoutparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutparams.setMargins(10, 10, 0, 10);
        for (int i = 0; i < mdatas.size(); i++) {
            View view = getview(i);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getview()...");
            }
            addView(view, i, layoutparams);
        }
    }

    private View getview(final int position) {
        final Commentsbean item = mdatas.get(position);
        String replyuser = item.getReplyuser();
        boolean hasreply = false; // 是否有回复
        if (!replyuser.equals("")) {
            hasreply = true;
        }
        TextView textview = new TextView(mcontext);
        textview.setTextSize(15);
        textview.setTextColor(0xff686868);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        String name = item.getCommentsuser();

        if (hasreply) {
            builder.append(setclickablespan(name, item.getCommentsuser()));
            builder.append(" 回复 ");
            builder.append(setclickablespan(replyuser, item.getReplyuser()));

        } else {
            builder.append(setclickablespan(name, item.getCommentsuser()));
        }
        builder.append(" : ");
        builder.append(setclickablespancontent(item.getContent(), position));
        textview.setText(builder);
        // 设置点击背景色
        textview.setHighlightColor(getResources().getColor(android.R.color.transparent));

        textview.setMovementMethod(new Circlemovementmethod(0xffcccccc, 0xffcccccc));

        textview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onitemclick(position, item);
                }
            }
        });


        return textview;
    }

    /**
     * 设置评论内容点击事件
     */
    public SpannableString setclickablespancontent(final String item, final int position) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // todo: 2017/9/3 评论内容点击事件
                //     Toast.makeText(mcontext, "position: " + position + " , content: " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的内容文本颜色
                ds.setColor(0xff686868);
                ds.setUnderlineText(false);
            }
        };
        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 设置评论用户名字点击事件
     *
     * @param item
     * @return
     */
    public SpannableString setclickablespan(final String item, final String name) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // todo: 2017/9/3 评论用户名字点击事件
                if (!name.equals(username)) {
                    reply = name;
                    Toast.makeText(mcontext, "选中回复人:" + reply, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的用户名文本颜色
                ds.setColor(0xff387dcc);
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 定义一个用于回调的接口
     */
    public interface onitemclicklistener {
        void onitemclick(int position, Commentsbean bean);
    }
}
