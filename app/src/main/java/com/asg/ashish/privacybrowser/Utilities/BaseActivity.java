package com.asg.ashish.privacybrowser.Utilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.asg.ashish.privacybrowser.Activities.MainActivity;
import com.asg.ashish.privacybrowser.Fragments.HomeFragment;
import com.asg.ashish.privacybrowser.Interfaces.WebViewBack;
import com.asg.ashish.privacybrowser.R;


public class BaseActivity extends AppCompatActivity {

    public void addFragmentAct(Fragment fragment, String tag){
        getSupportFragmentManager()
                .beginTransaction().add(R.id.fragment_container, fragment, tag)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right)
                .commit();
    }

    public void replaceFragment(Fragment fragment, String tag){
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.fragment_container, fragment, tag)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right)
                .commit();
    }


}
