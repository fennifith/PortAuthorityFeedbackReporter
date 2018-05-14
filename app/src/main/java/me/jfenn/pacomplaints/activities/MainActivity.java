package me.jfenn.pacomplaints.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
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

public class MainActivity extends AppCompatActivity implements Complainter.BlackboardListener {

    private static final String PREF_NAME_FIRST = "firstName";
    private static final String PREF_NAME_LAST = "lastName";
    private static final String PREF_PHONE = "phone";
    private static final String PREF_EMAIL = "email";

    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText phone;
    private TextInputEditText email;
    private AppCompatSpinner complaint;
    private TextInputEditText route;
    private TextInputEditText date;
    private TextInputEditText time;
    private TextInputEditText location;
    private AppCompatSpinner direction;
    private TextInputEditText vehicle;
    private TextInputEditText operator;
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

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        complaint = findViewById(R.id.complaint);
        route = findViewById(R.id.route);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        direction = findViewById(R.id.direction);
        vehicle = findViewById(R.id.vehicle);
        operator = findViewById(R.id.operator);
        description = findViewById(R.id.description);
        progressView = findViewById(R.id.progress);

        firstName.setText(prefs.getString(PREF_NAME_FIRST, ""));
        lastName.setText(prefs.getString(PREF_NAME_LAST, ""));
        phone.setText(prefs.getString(PREF_PHONE, ""));
        email.setText(prefs.getString(PREF_EMAIL, ""));

        Calendar noon = Calendar.getInstance();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        Calendar now = Calendar.getInstance();

        direction.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, new String[]{"Inbound", "Outbound", "Not Applicable"}));
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
                new OptionData("ADA/Accessibility", "ADA/Accessibility Complaint"),
                new OptionData("Civil Rights/Title VI", "Civil Rights/Title VI Complaint"),
                new OptionData("Company", "Company Complaint"),
                new OptionData("Employee", "Employee Complaint"),
                new OptionData("Other", "Miscellaneous Complaint"),
                new OptionData("Service", "Service - General Complaint"),
                new OptionData("ServiceLateEarly", "Service - Late/Early"),
                new OptionData("ServiceNoShow", "Service - No Show"),
                new OptionData("ServiceOvercrowding", "Service - Overcrowding"),
                new OptionData("ServicePassup", "Service - Pass Up"),
                new OptionData("ServiceServiceRequested", "Service - Service Requested"),
                new OptionData("Website", "Website Complaint")
        }));

        findViewById(R.id.review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit()
                        .putString(PREF_NAME_FIRST, firstName.getText().toString())
                        .putString(PREF_NAME_LAST, lastName.getText().toString())
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

        if (!complainter.isLoading()) {
            onPageFinished(null);
            progressView.setAlpha(0);
        }
    }

    @Override
    public void onPageFinished(String url) {
        progressView.animate().alpha(0).start();

        new InjectionTextWatcher(firstName, "firstname");
        new InjectionTextWatcher(lastName, "lastname");
        new InjectionTextWatcher(phone, "homephone");
        new InjectionTextWatcher(email, "email");
        new InjectionTextWatcher(route, "routenum");
        new InjectionTextWatcher(date, "incident_date");
        new InjectionTextWatcher(time, "boardtime");
        new InjectionTextWatcher(location, "boardloc");
        new InjectionTextWatcher(vehicle, "busnumber");
        new InjectionTextWatcher(operator, "opnumber");
        new InjectionTextWatcher(description, "txaMessage", 0);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (parent == direction) {
                    complainter.setAttributeByName("optDirection", 0, "checked", String.valueOf(position == 0));
                    complainter.setAttributeByName("optDirection", 1, "checked", String.valueOf(position == 1));
                } else if (parent == complaint) {
                    if (parent.getSelectedItem() instanceof OptionData) {
                        complainter.callFunctionByName("ddSubject", 0, "options[" + (position + 1) + "].setAttribute(\"selected\", \"selected\")", new ValueCallback<String>() {
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

        complainter.getHtmlContentByName("ddSubject", 0, new ValueCallback<String>() {
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
        });
    }

    @Override
    public void onRequest(String url) {
    }

    @Override
    public void onProgressChanged(int progress) {
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
