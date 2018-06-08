package com.alan.mydemo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class FlowLayout extends ViewGroup {


    private TextView tv;

    //存储所有的View，按行记录
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    //记录每一行的最大高度
    private List<Integer> mLineHeight = new ArrayList<Integer>();



    public FlowLayout(Context context) {
        super(context);

    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    public void setData(List<String> data, Context context, int textSize, int pl, int pt, int pr, int pb, int ml, int mt, int mr, int mb) {
        createChild(data, context, textSize, pl, pt, pr, pb, ml, mt, mr, mb);
    }

    private MarkClickListener markClickListener;

    public void setMarkClickListener(MarkClickListener markClickListener) {
        this.markClickListener = markClickListener;
    }

    public interface MarkClickListener {
        void clickMark(int position);
    }


    private void createChild(List<String> data, final Context context, int textSize, int pl, int pt, int pr, int pb, int ml, int mt, int mr, int mb) {
        int size = data.size();
        for (int i = 0; i < size; i++) {
            String text = data.get(i);

            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setText(text);
            tv.setTextSize(textSize);
            tv.setClickable(true);
            tv.setPadding(dip2px(context, pl), dip2px(context, pt), dip2px(context, pr), dip2px(context, pb));
            MarginLayoutParams params = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
            params.setMargins(ml, mt, mr, mb);
            tv.setLayoutParams(params);
            tv.setBackgroundColor(Color.parseColor("#CCCCCC"));

            final int finalI = i;
            //给每个view添加点击事件
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    markClickListener.clickMark(finalI);
                }
            });
            this.addView(tv);
        }
    }


    private int dip2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }




    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth();
        int childCount = getChildCount();
        int lineWidth = 0;
        int lineHeight = 0;
        ArrayList<View> lineList = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int measuredWidth = child.getMeasuredWidth();
            int measuredHeight = child.getMeasuredHeight();
            if (measuredWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                mAllViews.add(lineList);
                mLineHeight.add(lineHeight);
                lineWidth = 0;
                lineList = new ArrayList<View>();
            }

            lineWidth += measuredWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, measuredHeight + lp.topMargin + lp.bottomMargin);
            lineList.add(child);
        }

        mAllViews.add(lineList);
        mLineHeight.add(lineHeight);

        int leftDimen = 0;
        int topDimen = 0;

        for (int i = 0; i < mAllViews.size(); i++) {
            List<View> views = mAllViews.get(i);

            lineHeight = (i + 1) * mLineHeight.get(i);
            for (int j = 0; j < views.size(); j++) {
                View view = views.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
                int l = lp.leftMargin + leftDimen;
                int t = lp.topMargin + topDimen;
                int r = l + view.getMeasuredWidth();
                int b = t + view.getMeasuredHeight();

                view.layout(l, t, r, b);
                leftDimen += view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            leftDimen = 0;
            topDimen = lineHeight;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        int width = 0;
        int height = 0;
        int lineWidth = 0;
        int lineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (childWidth + lineWidth > widthSize) {
                width = Math.max(lineWidth, childWidth);
                height += lineHeight;
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(childHeight, lineHeight);
            }

            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize : width, (heightMode == MeasureSpec.EXACTLY) ? heightSize : height);
    }


}
