package app.zerobugz.fcms.ims.helper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Mohanraj on 06/04/2018.
 */

public class MyViewPagerForgotPass extends ViewPager{
    public MyViewPagerForgotPass(Context context) {
        super(context);
    }

    public MyViewPagerForgotPass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}
