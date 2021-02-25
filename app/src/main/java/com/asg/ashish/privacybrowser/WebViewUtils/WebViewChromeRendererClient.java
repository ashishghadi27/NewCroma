package com.asg.ashish.privacybrowser.WebViewUtils;

import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.asg.ashish.privacybrowser.Interfaces.InstanceAccessor;

public class WebViewChromeRendererClient extends WebChromeClient {

    private ProgressBar progress;
    private InstanceAccessor accessor;

    public WebViewChromeRendererClient(ProgressBar progressBar, InstanceAccessor accessor){
        this.progress = progressBar;
        this.accessor = accessor;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        ObjectAnimator animation = ObjectAnimator.ofInt(progress, "progress",newProgress);
        animation.setDuration(700); // 0.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        if(newProgress > 75)
            accessor.stopRefreshing();
    }
}
