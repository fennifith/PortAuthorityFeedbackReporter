package me.jfenn.pacomplaints.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;

import me.jfenn.pacomplaints.Complainter;
import me.jfenn.pacomplaints.R;
import me.jfenn.pacomplaints.views.ProgressLineView;

public class ReviewActivity extends AppCompatActivity implements Complainter.BlackboardListener {

    private Complainter complainter;
    private ViewGroup main;

    private ProgressLineView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        complainter = (Complainter) getApplicationContext();
        complainter.addListener(this);

        main = findViewById(R.id.main);
        progressView = findViewById(R.id.progress);

        if (complainter.webView.getParent() != null && complainter.webView.getParent() instanceof ViewGroup)
            ((ViewGroup) complainter.webView.getParent()).removeView(complainter.webView);

        main.addView(complainter.webView);
        complainter.webView.setFocusable(false);
        complainter.webView.setClickable(false);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        complainter.getAttributeByClassName("Button", 1, "value", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                if (value.contains("Submit")) {
                                    complainter.callFunctionByClassName("Button", 1, "click()", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                        }
                                    });
                                } else {
                                    error("Second submit button not found.");
                                }
                            }
                        });
                    }
                });

                dialog.setContentView(content);
                dialog.show();
            }
        });

        if (complainter.webView.getUrl().equals(Complainter.BASE_URL)) {
            complainter.getAttributeByClassName("Button", 0, "value", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (value.contains("Submit")) {
                        complainter.callFunctionByClassName("Button", 0, "click()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
                    } else {
                        error("First submit button not found.");
                    }
                }
            });
        }

        if (!complainter.isLoading())
            progressView.setAlpha(0);
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
        progressView.animate().alpha(0).start();

        if (url.equals(Complainter.DONE_URL)) {
            new AlertDialog.Builder(this)
                    .setTitle("Form Submitted")
                    .setMessage("The form has been submitted successfully. Press \'ok\' to close this page and reset all fields.")
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
        } else if (!url.equals(Complainter.BASE_URL) && !url.equals(Complainter.CONFIRM_URL)) {
            error("URL not recognized.");
        }
    }

    @Override
    public void onRequest(String url) {
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progressView.getAlpha() == 0)
            progressView.animate().alpha(1).start();

        progressView.update((float) progress / 100);
    }

    private void error(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_unknown_error)
                .setMessage(getString(R.string.msg_unknown_error) + message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onAlert(String message) {
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
    }
}
