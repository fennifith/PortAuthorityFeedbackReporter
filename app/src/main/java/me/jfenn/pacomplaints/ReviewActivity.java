package me.jfenn.pacomplaints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class ReviewActivity extends AppCompatActivity {

    private Complainter complainter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        complainter = (Complainter) getApplicationContext();

        ((ViewGroup) findViewById(R.id.main)).addView(complainter.webView);
        complainter.webView.setFocusable(false);
        complainter.webView.setClickable(false);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
