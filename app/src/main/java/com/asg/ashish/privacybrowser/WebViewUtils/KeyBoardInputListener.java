package com.asg.ashish.privacybrowser.WebViewUtils;

import android.content.Context;
import android.location.Address;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.asg.ashish.privacybrowser.Interfaces.InstanceAccessor;
import com.asg.ashish.privacybrowser.Interfaces.TabPlacer;
import com.asg.ashish.privacybrowser.Utilities.Constants;

public class KeyBoardInputListener implements AutoCompleteTextView.OnEditorActionListener {

    private AutoCompleteTextView addressBar;
    private WebView webView;
    private Context context;
    private InstanceAccessor placer;

    public KeyBoardInputListener(AutoCompleteTextView addressBar, WebView webView, Context context, InstanceAccessor placer) {
        this.addressBar = addressBar;
        this.webView = webView;
        this.context = context;
        this.placer = placer;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            String query = addressBar.getText().toString().trim();
            if(!TextUtils.isEmpty(query)){
                if (!Patterns.WEB_URL.matcher(query).matches()) {
                    query = placer.getSearchEngine() + query;
                    webView.loadUrl(query);
                } else if (query.startsWith("https") || query.startsWith("http")) {
                    webView.loadUrl(query);
                } else {
                    query = "http://" + query;
                    webView.loadUrl(query);
                }
                addressBar.clearFocus();
                closeKeyboard();
                return true;
            }
        }
        return false;
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
    }
}
