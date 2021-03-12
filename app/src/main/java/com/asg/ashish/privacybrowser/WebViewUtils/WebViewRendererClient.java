package com.asg.ashish.privacybrowser.WebViewUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.MailTo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.asg.ashish.privacybrowser.Interfaces.InstanceAccessor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class WebViewRendererClient extends WebViewClient {

    private InstanceAccessor accessor;

    public WebViewRendererClient(InstanceAccessor accessor){
        this.accessor = accessor;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        accessor.onPageStarted();
        accessor.setUrl(url);
        accessor.initHttpsStatus();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        accessor.onPageFinished();
        accessor.stopRefreshing();
        accessor.setUrl(view.getTitle());
        accessor.setHttpsStatus(view.getUrl().contains("https"));
        //captureScreenshot();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.v("PLAY URL: ", url + "");
        if ((url.contains("play.google.com") || url.contains("intent")) && url.contains("=") && url.contains("&")) {
            String packageName = url.replace(url.substring(0, url.indexOf('=') + 1), "");
            packageName = packageName.replace(packageName.substring(packageName.indexOf('&'), packageName.length()), "");
            Log.v("URL iS: ", packageName);
            try {
                Intent intent = view.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
                ((Activity) view.getContext()).startActivity(intent);
                return true;
            } catch (ActivityNotFoundException | NullPointerException e) {
                goToPlayStore(view, packageName);
                return true;
            }

        }
        else if(url.contains("market"))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            ((Activity) view.getContext()).startActivity(intent);
            return true;
        }
        else if(url.contains("mailto:")){
            Log.v("HERE", Uri.parse("mailto") + "");
            MailTo mt = MailTo.parse(url);
            Intent i = newEmailIntent(view.getContext(), mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
            view.getContext().startActivity(i);
            view.reload();
            return true;
        }
        else if (url.startsWith("tel:")) {
            view.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
            return true;
        }
        else if(url.startsWith("whatsapp://")){
            onClickWhatsApp(view, Objects.requireNonNull(Uri.parse(url).getQuery()).replace("text=", ""));
            return true;
        }
        else {
            view.loadUrl(url);
            return true;
        }
    }

    private void goToPlayStore(View view, String packageName){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        ((Activity) view.getContext()).startActivity(intent);
    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    public void onClickWhatsApp(View view, String text) {

        PackageManager pm = view.getContext().getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            view.getContext().startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            goToPlayStore(view,"com.whatsapp");
        }

    }

    private void captureScreenshot(){
        Picture picture = accessor.getWebView().capturePicture();
        Bitmap b = Bitmap.createBitmap(picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        File sdCard = Environment.getExternalStorageDirectory();
        picture.draw(c);
        String path = sdCard.getAbsolutePath()+"/Croma";
        Log.v("Path: ", path);
        File directory = new File (path);
        directory.mkdirs();
        File file = new File(directory, "preview.jpg");
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(file);
            if (fos != null) {
                Log.v("Before compress", "Compressed");
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.v("After compress", "Compressed");
                fos.close();
            }
        } catch (Exception e) {
            Log.v("Exception: ", e.getCause() + "");
        }
    }

}
