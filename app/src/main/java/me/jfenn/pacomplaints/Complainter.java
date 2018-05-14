package me.jfenn.pacomplaints;

import android.app.Application;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import me.jfenn.pacomplaints.views.RestrictedWebView;

public class Complainter extends Application {

    public static final String BASE_URL = "http://www.portauthority.org/paac/apps/webcomments/pgcomment.asp?t=con";
    public static final String CONFIRM_URL = "http://www.portauthority.org/paac/apps/webcomments/pgConfirm.asp?form=concern";
    public static final String DONE_URL = "";

    public WebView webView;

    private List<BlackboardListener> listeners;

    private boolean isLoading;

    @Override
    public void onCreate() {
        super.onCreate();
        listeners = new ArrayList<>();
        webView = new RestrictedWebView(this);
        webView.setWebViewClient(new WebClient(this));
        webView.setWebChromeClient(new ChromeClient(this));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(false);
        webSettings.setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        webView.loadUrl(BASE_URL);
        isLoading = true;
    }

    /**
     * Get all HTML content inside the <body> element of the WebView, formatted as JSON
     * @param callback called once the action is completed
     */
    public void getHtml(ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('body')[0].innerHTML;})();", callback);
    }

    /**
     * Gets all HTML content inside an element, formatted as JSON
     * @param id the id of the element
     * @param callback called once the action is completed
     */
    public void getHtmlContent(String id, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('" + id + "').innerHTML;})()", callback);
    }

    /**
     * Gets all HTML content inside an element, formatted as JSON
     * @param className the class name of the element
     * @param i the index of the elements with the className
     * @param callback called once the action is completed
     */
    public void getHtmlContentByClassName(String className, int i, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementsByClassName('" + className + "')[" + i + "].innerHTML;})()", callback);
    }

    public void getHtmlContentByName(String name, int i, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementsByName('" + name + "')[" + i + "].innerHTML;})()", callback);
    }

    /**
     * Sets an attribute of an element in the WebView
     * @param id the element to apply the attribute to
     * @param attribute the name of the attribute to change
     * @param value the value to change the attribute to
     */
    public void setAttribute(String id, String attribute, String value) {
        webView.evaluateJavascript("(function(){document.getElementById('" + id + "')." + attribute + " = " + value + ";})();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }

    /**
     * Sets an attribute of an element in the WebView
     * @param name the element to apply the attribute to
     * @param index the index of the element in the page
     * @param attribute the name of the attribute to change
     * @param value the value to change the attribute to
     */
    public void setAttributeByName(String name, int index, String attribute, String value) {
        webView.evaluateJavascript("(function(){document.getElementsByName('" + name + "')[" + index + "]." + attribute + " = " + value + ";})();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }

    /**
     * Sets an attribute of an element in the WebView
     * @param name the tag name of element to apply the attribute to
     * @param index the index of the element in the page
     * @param attribute the name of the attribute to change
     * @param value the value to change the attribute to
     */
    public void setAttributeByTagName(String name, int index, String attribute, String value) {
        webView.evaluateJavascript("(function(){document.getElementsByName('" + name + "')[" + index + "]." + attribute + " = " + value + ";})();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }

    /**
     * Gets an attribute of an element in the WebView, formatted as JSON
     * @param id the id of the element
     * @param attribute the attribute to obtain
     * @param callback called once the action is completed
     */
    public void getAttribute(String id, String attribute, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('" + id + "')." + attribute + ";})()", callback);
    }

    /**
     * Gets an attribute of an element in the WebView, formatted as JSON
     *
     * @param name      the class name of the element
     * @param index     the index of the element in the page
     * @param attribute the attribute to obtain
     * @param callback  called once the action is completed
     */
    public void getAttributeByClassName(String name, int index, String attribute, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementsByClassName('" + name + "')[" + index + "]." + attribute + ";})()", callback);
    }

    /**
     * Calls a function inside the WebView
     * @param function the function to call
     * @param callback called once the action is completed
     */
    public void callFunction(String function, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){" + function + "})()", callback);
    }

    /**
     * Calls a function of an element inside the WebView
     * @param id the id of the element
     * @param function the function to call
     * @param callback called once the action is completed
     */
    public void callFunction(String id, String function, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementById('" + id + "')." + function + ";})();", callback);
    }

    /**
     * Calls a function of an element inside the WebView
     * @param name the name of the element
     * @param index the index of the elements with the name
     * @param function the function to call
     * @param callback called once the action is completed
     */
    public void callFunctionByName(String name, int index, String function, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementsByName('" + name + "')[" + index + "]." + function + ";})();", callback);
    }

    /**
     * Calls a function of an element inside the WebView
     *
     * @param name     the class name of the element
     * @param index    the index of the elements with the name
     * @param function the function to call
     * @param callback called once the action is completed
     */
    public void callFunctionByClassName(String name, int index, String function, ValueCallback<String> callback) {
        webView.evaluateJavascript("(function(){return document.getElementsByClassName('" + name + "')[" + index + "]." + function + ";})();", callback);
    }

    public void addListener(BlackboardListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BlackboardListener listener) {
        listeners.remove(listener);
    }

    private void onPageFinished(String url) {
        isLoading = false;
        for (BlackboardListener listener : listeners) {
            listener.onPageFinished(url);
        }
    }

    private void onRequest(String url) {
        for (BlackboardListener listener : listeners) {
            listener.onRequest(url);
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void onProgressChanged(int progress) {
        isLoading = true;
        for (BlackboardListener listener : listeners) {
            listener.onProgressChanged(progress);
        }
    }

    private void onAlert(String message) {
        for (BlackboardListener listener : listeners) {
            listener.onAlert(message);
        }
    }

    public interface BlackboardListener {
        void onPageFinished(String url);
        void onRequest(String url);

        void onProgressChanged(int progress);
        void onAlert(String message);
    }

    private static class WebClient extends WebViewClient {

        private Complainter blackboard;

        public WebClient(Complainter blackboard) {
            this.blackboard = blackboard;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            blackboard.onPageFinished(url);
            super.onPageFinished(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            blackboard.onRequest(url);
            return super.shouldInterceptRequest(view, url);
        }
    }

    private static class ChromeClient extends WebChromeClient {

        private Complainter blackboard;

        public ChromeClient(Complainter blackboard) {
            this.blackboard = blackboard;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            blackboard.onProgressChanged(newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            blackboard.onAlert(message);
            Log.d("Alert", message);
            return super.onJsAlert(view, url, message, result);
        }
    }
}
