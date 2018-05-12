package me.jfenn.pacomplaints;

import android.content.Context;
import android.view.MotionEvent;
import android.webkit.WebView;

public class RestrictedWebView extends WebView {

    public RestrictedWebView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP)
            return super.onTouchEvent(event);
        else return false;
    }
}
