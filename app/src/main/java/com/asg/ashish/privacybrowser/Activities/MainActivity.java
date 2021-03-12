package com.asg.ashish.privacybrowser.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.asg.ashish.privacybrowser.Fragments.HomeFragment;
import com.asg.ashish.privacybrowser.Fragments.WebFragment;
import com.asg.ashish.privacybrowser.Interfaces.FragmentListOperations;
import com.asg.ashish.privacybrowser.Interfaces.WebViewBack;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.Utilities.BaseActivity;
import com.asg.ashish.privacybrowser.WebViewUtils.FragmentStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

public class MainActivity extends BaseActivity implements FragmentListOperations {

    private List<FragmentStorage> fragmentList;
    private RelativeLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);
        main = findViewById(R.id.main_container);
        setActivityTheme();
        clearCookies(this);
        fragmentList = new ArrayList<>();

        Intent intent = getIntent();
        final Uri uri = intent.getData();
        try {
            assert uri != null;
            String url = uri.toString();
            if (Patterns.WEB_URL.matcher(url).matches()) {
                if (!url.startsWith("http")) url = "http://" + url;
                addWebTab(url);
            }
            else loadHome();

        } catch (Exception e) {
            loadHome();
        }
    }

    private void loadHome(){
        HomeFragment fragment = new HomeFragment();
        fragment.setOperations(this);
        addFragmentAct(fragment, "Home");
    }

    @Override
    public void addTab(WebFragment fragment, String title) {
        fragmentList.add(new FragmentStorage(title, fragment, null));
    }

    @Override
    public List<FragmentStorage> getAllTabs() {
        return fragmentList;
    }

    @Override
    public void updateTitle(WebFragment fragment, String title) {
        for(FragmentStorage fragmentStorage: fragmentList){
            if(fragmentStorage.getFragment().equals(fragment)){
                fragmentStorage.setTitle(title);
                fragmentStorage.setFragment(fragment);
                break;
            }
        }
    }

    @Override
    public void setStack(Stack<String> stack, WebFragment fragment) {
        for(FragmentStorage fragmentStorage: fragmentList){
            if(fragmentStorage.getFragment().equals(fragment)){
                fragmentStorage.setHistory(stack);
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public void removeFragment(WebFragment fragment) {
        Log.v("HERE", "Removing");
        for(FragmentStorage tab: fragmentList){
            if(tab.getFragment().equals(fragment)){
                fragmentList.remove(tab);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        assert fragment != null;
        if (fragment instanceof WebViewBack) {
            if (((WebViewBack) fragment).goBack()) {
                replaceFragment(new HomeFragment(this), "Home");
            }
        }
        else if(fragment instanceof HomeFragment){
            Log.v("hello", "home");
            super.onBackPressed();
            super.onBackPressed();
        }
        else if (getFragmentManager().getBackStackEntryCount() == 0){
            replaceFragment(new HomeFragment(this), "Home");
        }
        else super.onBackPressed();
    }

    public void setActivityTheme(){
        main.setBackground(ResourcesCompat.getDrawable(getResources(), loadTheme(), getTheme()));
    }

    private void addWebTab(String url){
        WebFragment webFragment = new WebFragment(this, url, null);
        this.addTab(webFragment, url);
        Stack<String> stack = new Stack<>();
        stack.push(url);
        this.setStack(stack, webFragment);
        addFragmentAct(webFragment, "web" + this.getCount() + 1);
    }

    public void clearCookies(Context context){
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
    }

}