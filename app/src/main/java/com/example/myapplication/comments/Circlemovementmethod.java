package com.example.myapplication.comments;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class Circlemovementmethod extends BaseMovementMethod {
    private final static int default_color_id = android.R.color.transparent;
    /**
     * 整个textview的背景色
     */
    private int textviewbgcolor;
    /**
     * 点击部分文字时部分文字的背景色
     */
    private int clickablespanbgclor;

    private BackgroundColorSpan mbgspan;
    private ClickableSpan[] mclicklinks;


    /**
     * @param clickablespanbgclor 点击选中部分时的背景色
     */
    public Circlemovementmethod(int clickablespanbgclor) {
        this.clickablespanbgclor = clickablespanbgclor;
    }

    /**
     * @param clickablespanbgclor 点击选中部分时的背景色
     * @param textviewbgcolor  整个textview点击时的背景色
     */
    public Circlemovementmethod(int clickablespanbgclor, int textviewbgcolor) {
        this.textviewbgcolor = textviewbgcolor;
        this.clickablespanbgclor = clickablespanbgclor;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line =  layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            mclicklinks = buffer.getSpans(off, off, ClickableSpan.class);
            if (mclicklinks.length > 0) {
                // 点击的是span区域，不要把点击事件传递
                Selection.setSelection(buffer,
                        buffer.getSpanStart(mclicklinks[0]),
                        buffer.getSpanEnd(mclicklinks[0]));
                //设置点击区域的背景色
                mbgspan = new BackgroundColorSpan(clickablespanbgclor);
                buffer.setSpan(mbgspan,
                        buffer.getSpanStart(mclicklinks[0]),
                        buffer.getSpanEnd(mclicklinks[0]),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                // textview选中效果
//    widget.setbackgroundcolor(textviewbgcolor);
                widget.setBackgroundResource(default_color_id);
            }

        } else if (action == MotionEvent.ACTION_UP) {
            if (mclicklinks.length > 0) {
                mclicklinks[0].onClick(widget);
                if (mbgspan != null) {//移除点击时设置的背景span
                    buffer.removeSpan(mbgspan);
                }
            } else {

            }
            Selection.removeSelection(buffer);
            widget.setBackgroundResource(default_color_id);
        } else if (action == MotionEvent.ACTION_MOVE) {
            //这种情况不用做处理
        } else {
            if (mbgspan != null) {//移除点击时设置的背景span
                buffer.removeSpan(mbgspan);
            }
            widget.setBackgroundResource(default_color_id);
        }
        return Touch.onTouchEvent(widget, buffer, event);
    }
}
