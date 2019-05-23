package me.jfenn.pacomplaints.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import me.jfenn.pacomplaints.Complainter;

public class InjectionTextWatcher implements TextWatcher {

    private EditText[] editTexts;
    private String id;
    private String name;
    private int index;

    public InjectionTextWatcher( String id, EditText... editTexts) {
        this.editTexts = editTexts;
        this.id = id;

        for (EditText e : editTexts) {
            e.addTextChangedListener(this);
        }

        onTextChanged("", 0, 0, 0);
    }

    public InjectionTextWatcher(String name, int index, EditText... editTexts) {
        this.editTexts = editTexts;
        this.name = name;
        this.index = index;

        for (EditText e : editTexts) {
            e.addTextChangedListener(this);
        }

        onTextChanged("", 0, 0, 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Complainter complainter = ((Complainter) editTexts[0].getContext().getApplicationContext());

        String str = "";
        for (EditText e : editTexts) {
            str += " " + e.getText().toString();
        }

        if (id != null)
            complainter.setAttribute(id, "value", "\"" + str.substring(1) + "\"");
        else
            complainter.setAttributeByName(name, index, "value", "\"" + str.substring(1) + "\"");
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
