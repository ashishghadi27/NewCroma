package com.asg.ashish.privacybrowser.WebViewUtils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.asg.ashish.privacybrowser.Interfaces.InstanceAccessor;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Downloader implements DownloadListener {

    private Context context;
    private InstanceAccessor accessor;

    public Downloader(Context context, InstanceAccessor accessor) {
        this.context = context;
        this.accessor = accessor;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if(accessor.hasStoragePermission()){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimetype);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            assert dm != null;
            dm.enqueue(request);
            Toast.makeText(context.getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
        }
        else accessor.requestStoragePermission();
    }
}
