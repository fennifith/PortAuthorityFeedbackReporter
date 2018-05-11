package me.jfenn.pacomplaints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReviewActivity extends AppCompatActivity {

    private Complainter complainter;
    private ViewGroup main;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        complainter = (Complainter) getApplicationContext();

        main = findViewById(R.id.main);
        main.addView(complainter.webView);
        complainter.webView.setFocusable(false);
        complainter.webView.setClickable(false);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
                View content = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_confirm, null);

                content.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                content.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.exit(0);
                    }
                });

                dialog.setContentView(content);
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (main != null && complainter.webView.getParent() != null)
            main.removeView(complainter.webView);

        super.onDestroy();
    }
}
