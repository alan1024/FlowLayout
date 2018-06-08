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
            tv.setBackgroundColor(Color.WHITE);
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


    //存储所有的View，按行记录
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    //记录每一行的最大高度
    private List<Integer> mLineHeight = new ArrayList<Integer>();


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


//        mAllViews.clear();  //清空子控件列表
//        mLineHeight.clear();  //清空高度记录列表
//        int width = getWidth();//得到当前控件的宽度（在onmeasure方法中已经测量出来了）
//        int childCount = getChildCount();
//        // 存储每一行所有的childView
//        List<View> lineViews = new ArrayList<View>();
//        int lineWidth = 0;  //行宽
//        int lineHeight = 0; //总行高
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();//得到属性参数
//            int childWidth = child.getMeasuredWidth();
//            int childHeight = child.getMeasuredHeight();
//            // 如果已经需要换行
//
//            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width)  //大于父布局的宽度
//            {
//                // 记录这一行所有的View以及最大高度
//                mLineHeight.add(lineHeight);
//                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
//                mAllViews.add(lineViews);
//                lineWidth = 0;// 重置行宽
//                lineViews = new ArrayList<View>();
//            }
//            /**
//             * 如果不需要换行，则累加
//             */
//            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
//            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
//                    + lp.bottomMargin);
//            lineViews.add(child);
//        }
//        // 记录最后一行  (因为最后一行肯定大于父布局的宽度，所以添加最后一行是必要的)
//        mLineHeight.add(lineHeight);
//        mAllViews.add(lineViews);
//
//        int viewLeft = 0;
//        int viewTop = 0;
//        int lineNums = mAllViews.size();
//        for (int i = 0; i < lineNums; i++) {
//            // 每一行的所有的views
//            lineViews = mAllViews.get(i);
//            // 当前行的最大高度  每一行的高度都相同  所以使用（i+1）进行设置高度
//            lineHeight = (i + 1) * mLineHeight.get(i);
//            for (int j = 0; j < lineViews.size(); j++) {
//                View lineChild = lineViews.get(j);
//                if (lineChild.getVisibility() == View.GONE) {
//                    continue;
//                }
//                MarginLayoutParams lp = (MarginLayoutParams) lineChild.getLayoutParams();
//                //开始画标签了。左边和上边的距离是要根据累计的数确定的。
//                int lc = viewLeft + lp.leftMargin;
//                int tc = viewTop + lp.topMargin;
//                int rc = lc + lineChild.getMeasuredWidth();
//                int bc = tc + lineChild.getMeasuredHeight();
//                lineChild.layout(lc, tc, rc, bc);
//                viewLeft += lineChild.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
//            }
//            viewLeft = 0;//将left归零
//            viewTop = lineHeight;
//        }
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
