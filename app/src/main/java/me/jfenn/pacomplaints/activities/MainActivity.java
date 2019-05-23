package me.jfenn.pacomplaints.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.jfenn.attribouter.Attribouter;
import me.jfenn.pacomplaints.Complainter;
import me.jfenn.pacomplaints.R;
import me.jfenn.pacomplaints.data.OptionData;
import me.jfenn.pacomplaints.listeners.InjectionTextWatcher;
import me.jfenn.pacomplaints.listeners.NoKeyboardFocusListener;
import me.jfenn.pacomplaints.listeners.NoKeyboardTouchListener;
import me.jfenn.pacomplaints.views.ProgressLineView;

public class MainActivity extends AppCompatActivity implements Complainter.FeedbackListener {

    private static final String PREF_NAME = "firstName";
    private static final String PREF_PHONE = "phone";
    private static final String PREF_EMAIL = "email";

    private TextInputEditText name;
    private TextInputEditText phone;
    private TextInputEditText email;
    private AppCompatSpinner complaint;
    private TextInputEditText route;
    private TextInputEditText date;
    private TextInputEditText time;
    private TextInputEditText location;
    private AppCompatSpinner direction;
    private TextInputEditText vehicle;
    private TextInputEditText description;
    private ProgressLineView progressView;

    private Complainter complainter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        complainter = (Complainter) getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        complainter.addListener(this);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        complaint = findViewById(R.id.complaint);
        route = findViewById(R.id.route);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        direction = findViewById(R.id.direction);
        vehicle = findViewById(R.id.vehicle);
        description = findViewById(R.id.description);
        progressView = findViewById(R.id.progress);

        name.setText(prefs.getString(PREF_NAME, ""));
        phone.setText(prefs.getString(PREF_PHONE, ""));
        email.setText(prefs.getString(PREF_EMAIL, ""));

        Calendar noon = Calendar.getInstance();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        Calendar now = Calendar.getInstance();

        direction.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, new OptionData[]{
                new OptionData("Inbound", "Inbound"),
                new OptionData("Outbound", "Outbound"),
                new OptionData("Other", "Other"),
                new OptionData("I don't know", "I don't know")
        }));
        direction.setSelection(now.after(noon) ? 1 : 0);

        date.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(now.getTime()));
        date.setTag(now);
        date.setOnTouchListener(new NoKeyboardTouchListener());
        date.setOnFocusChangeListener(new NoKeyboardFocusListener());
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (v.getTag() != null && v.getTag() instanceof Calendar)
                    calendar = (Calendar) v.getTag();

                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        date.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(calendar.getTime()));
                        date.setTag(calendar);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now.getTime()));
        time.setTag(now);
        time.setOnTouchListener(new NoKeyboardTouchListener());
        time.setOnFocusChangeListener(new NoKeyboardFocusListener());
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (v.getTag() != null && v.getTag() instanceof Calendar)
                    calendar = (Calendar) v.getTag();

                new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        time.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.getTime()));
                        time.setTag(calendar);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(v.getContext())).show();
            }
        });

        complaint.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, new OptionData[]{
                new OptionData("Question", "Question"),
                new OptionData("Complaint", "Complaint"),
                new OptionData("Suggestion", "Suggestion"),
                new OptionData("Praise", "Praise"),
                new OptionData("Other", "Other")
        }));

        findViewById(R.id.review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit()
                        .putString(PREF_NAME, name.getText().toString())
                        .putString(PREF_PHONE, phone.getText().toString())
                        .putString(PREF_EMAIL, email.getText().toString())
                        .apply();

                startActivity(new Intent(MainActivity.this, ReviewActivity.class));
            }
        });

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Attribouter.from(MainActivity.this).show();
            }
        });

        if (!complainter.webView.getUrl().equals(Complainter.BASE_URL)) {
            complainter.webView.loadUrl(Complainter.BASE_URL);
        } else if (!complainter.isLoading()) {
            onPageFinished(null);
            progressView.setAlpha(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (complainter.webView.getUrl().equals(Complainter.CONFIRM_URL)) {
            complainter.getAttributeByClassName("Button", 0, "value", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (value.contains("Changes")) {
                        complainter.callFunctionByClassName("Button", 0, "click()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
                    } else {
                        error("Backwards navigation changes button not found.");
                    }
                }
            });
        } else if (!complainter.webView.getUrl().equals(Complainter.BASE_URL)) {
            if (complainter.webView.getUrl().equals(Complainter.DONE_URL)) {
                route.setText("");
                location.setText("");
                vehicle.setText("");
                description.setText("");
            }

            complainter.webView.loadUrl(Complainter.BASE_URL);
            if (progressView != null)
                progressView.animate().alpha(1).start();
        }
    }

    @Override
    public void onPageFinished(String url) {
        progressView.animate().alpha(0).start();

        new InjectionTextWatcher("09b8bada-079d-4cbb-9151-3b34e5b0f1ca", name);
        new InjectionTextWatcher("914d71a9-02be-46e9-a16e-e2f74110a947", phone);
        new InjectionTextWatcher("25691d51-213f-4814-8e56-b7e361f92df6", email);
        new InjectionTextWatcher("37370ded-c9f3-4582-84b0-59f5163fa234", route);
        new InjectionTextWatcher("d8afa4f3-3425-43bd-bbeb-1ce68c618687", date, time);
        new InjectionTextWatcher("c593eba5-38f1-4145-b737-5c664448361a", location);
        new InjectionTextWatcher("ad246e5d-40e5-483a-999e-9063432f110e", vehicle);
        new InjectionTextWatcher("f76aafe7-c665-4015-b490-b1628242885d", description);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (parent == direction) {
                    if (parent.getSelectedItem() instanceof OptionData) {
                        complainter.callFunctionByName("__field_3372", 0, "options[" + (position + 1) + "].setAttribute(\"selected\", \"selected\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
                    }
                } else if (parent == complaint) {
                    if (parent.getSelectedItem() instanceof OptionData) {
                        complainter.callFunctionByName("__field_972", 0, "options[" + (position + 1) + "].setAttribute(\"selected\", \"selected\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        complaint.setOnItemSelectedListener(listener);
        listener.onItemSelected(complaint, null, complaint.getSelectedItemPosition(), 0);
        direction.setOnItemSelectedListener(listener);
        listener.onItemSelected(direction, null, direction.getSelectedItemPosition(), 0);

        /*complainter.getHtmlContentByName("ddSubject", 0, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (value.length() > 2) {
                    List<OptionData> options = OptionData.fromHTML(value);
                    complaint.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, options.toArray(new OptionData[options.size()])));
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Scraping Error")
                            .setMessage("The app has failed to scrape the complaint options from the website. Form editing may still work, but is not reliable.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });*/
    }

    private void error(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Unknown Error")
                .setMessage("Something has gone wrong while attempting to interact with the form. This usually signifies that part of the form has been updated and that the app may no longer be compatible with it. \n\nError message: " + message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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

    @Override
    public void onAlert(String message) {
    }

    @Override
    protected void onDestroy() {
        complainter.removeListener(this);
        super.onDestroy();
    }
}
