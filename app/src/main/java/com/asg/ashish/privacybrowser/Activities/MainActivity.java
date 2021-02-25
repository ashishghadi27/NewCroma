package com.asg.ashish.privacybrowser.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.asg.ashish.privacybrowser.Fragments.HomeFragment;
import com.asg.ashish.privacybrowser.Fragments.WebFragment;
import com.asg.ashish.privacybrowser.Interfaces.FragmentListOperations;
import com.asg.ashish.privacybrowser.Interfaces.WebViewBack;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.Utilities.BaseActivity;
import com.asg.ashish.privacybrowser.WebViewUtils.FragmentStorage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

public class MainActivity extends BaseActivity implements FragmentListOperations {

    private List<FragmentStorage> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentList = new ArrayList<>();
        addFragmentAct(new HomeFragment(this), "Home");
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
                //super.onBackPressed();
            }
            /*if (getFragmentManager().getBackStackEntryCount() == 0){
                replaceFragment(new HomeFragment(this), "Home");
            }*/
        } else if (getFragmentManager().getBackStackEntryCount() == 0){
            replaceFragment(new HomeFragment(this), "Home");
        }
        else super.onBackPressed();
    }

}