package com.asg.ashish.privacybrowser.Interfaces;

import android.webkit.WebView;

import java.util.List;

public interface InstanceAccessor {
    void stopRefreshing();
    void onPageStarted();
    void onPageFinished();
    void onPageCommitVisible();
    void setUrl(String url);
    void setHttpsStatus(boolean isHttps);
    void initHttpsStatus();
    void setAdapter(List<String> suggestions);
    String getSearchEngine();
    boolean hasStoragePermission();
    boolean requestStoragePermission();
    WebView getWebView();
}
