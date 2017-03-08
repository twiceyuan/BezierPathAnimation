package com.twiceyuan.bezierpathanimation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by twiceYuan on 2017/3/8.
 * <p>
 * 绘制 Bezier 路径的工具类
 */
@SuppressWarnings("WeakerAccess")
public class BezierHelper {

    private View               mSource;
    private View               mTarget;
    private ViewGroup          mContainer;
    private TimeInterpolator   mTimeInterpolator;
    private OnCompleteListener mCompleteListener;

    private int mDuration = 800;

    private BezierHelper() {
    }

    public static BezierHelper create() {
        return new BezierHelper();
    }

    private static Bitmap viewToBitmap(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public BezierHelper setSource(View source) {
        mSource = source;
        return this;
    }

    public BezierHelper setTarget(View target) {
        mTarget = target;
        return this;
    }

    public BezierHelper setContainer(ViewGroup container) {
        mContainer = container;
        return this;
    }

    public BezierHelper setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public BezierHelper setTimeInterpolator(TimeInterpolator timeInterpolator) {
        mTimeInterpolator = timeInterpolator;
        return this;
    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        mCompleteListener = completeListener;
    }

    public void start() {
        if (mSource == null) {
            throw new BezierPathException("mSource");
        }
        if (mTarget == null) {
            throw new BezierPathException("mTarget");
        }
        if (mContainer == null) {
            throw new BezierPathException("mContainer");
        }
        if (mTimeInterpolator == null) {
            mTimeInterpolator = new AccelerateInterpolator();
        }
        mSource.post(new Runnable() {
            @Override
            public void run() {
                startInternal();
            }
        });
    }

    private void startInternal() {
        // 一、创造出执行动画的主题---ImageView
        // 代码 new 一个 ImageView ，图片资源是上面的 ImageView 的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        final ImageView sourceShadow = new ImageView(mSource.getContext());
        sourceShadow.setImageBitmap(viewToBitmap(mSource));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                mSource.getWidth(),
                mSource.getHeight());
        mContainer.addView(sourceShadow, params);

        // 二、计算动画开始/结束点的坐标的准备工作
        // 得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        mContainer.getLocationInWindow(parentLocation);

        // 得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        mSource.getLocationInWindow(startLoc);

        // 得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        mTarget.getLocationInWindow(endLoc);

        // 三、正式开始计算动画开始/结束的坐标
        // 开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = startLoc[0] - parentLocation[0];
        float startY = startLoc[1] - parentLocation[1];

        // 商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0];
        float toY = endLoc[1] - parentLocation[1];

        // 四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        // 开始绘制贝塞尔曲线
        Path path = new Path();
        // 移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        // 使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        // mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        final PathMeasure mPathMeasure = new PathMeasure(path, false);

        // ★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(mDuration);
        // 匀速线性插值器
        valueAnimator.setInterpolator(mTimeInterpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                float[] mCurrentPosition = new float[2];
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                sourceShadow.setTranslationX(mCurrentPosition[0]);
                sourceShadow.setTranslationY(mCurrentPosition[1]);
            }
        });
        // 五、 开始执行动画
        valueAnimator.start();

        // 六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // 把移动的图片 ImageView 从父布局里移除
                mContainer.removeView(sourceShadow);

                if (mCompleteListener != null) {
                    mCompleteListener.onComplete();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    @SuppressWarnings("WeakerAccess")
    public static class BezierPathException extends RuntimeException {
        BezierPathException(String nullObject) {
            super(nullObject + " can not be null.");
        }
    }
}
