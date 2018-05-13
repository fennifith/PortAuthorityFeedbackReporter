package me.jfenn.pacomplaints.data;

import android.util.JsonReader;
import android.util.JsonToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class OptionData {

    private String value;
    private String name;

    public OptionData(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<OptionData> fromHTML(String html) {
        List<OptionData> optionList = new ArrayList<>();
        JsonReader reader = new JsonReader(new StringReader(html));
        reader.setLenient(true);

        try {
            if (reader.peek() != JsonToken.NULL) {
                if (reader.peek() == JsonToken.STRING) {
                    Document document = Jsoup.parseBodyFragment(reader.nextString());
                    Elements options = document.select("option");

                    for (Element option : options) {
                        if (!option.attr("value").equals("0"))
                            optionList.add(new OptionData(option.attr("value"), option.text().trim()));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return optionList;
    }
}
