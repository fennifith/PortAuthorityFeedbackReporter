package me.jfenn.pacomplaints.listeners;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class NoKeyboardFocusListener implements View.OnFocusChangeListener {

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null)
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            v.performClick();
        }
    }
}
