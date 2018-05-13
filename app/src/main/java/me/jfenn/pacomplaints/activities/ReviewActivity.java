package me.jfenn.pacomplaints.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.Toast;

import me.jfenn.pacomplaints.Complainter;
import me.jfenn.pacomplaints.R;

public class ReviewActivity extends AppCompatActivity implements Complainter.BlackboardListener {

    private boolean isSubmitted;
    private Complainter complainter;
    private ViewGroup main;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        complainter = (Complainter) getApplicationContext();
        complainter.addListener(this);

        main = findViewById(R.id.main);
        main.addView(complainter.webView);
        complainter.webView.setFocusable(false);
        complainter.webView.setClickable(false);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSubmitted = true;

                final BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
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
                        complainter.callFunctionByClassName("Button", 0, "click()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                dialog.dismiss();
                            }
                        });
                    }
                });

                dialog.setContentView(content);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isSubmitted = false;
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        complainter.removeListener(this);
        if (main != null && complainter.webView.getParent() != null)
            main.removeView(complainter.webView);

        super.onDestroy();
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onRequest(String url) {
    }

    @Override
    public void onAlert(String message) {
        if (isSubmitted) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();
        } else Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
