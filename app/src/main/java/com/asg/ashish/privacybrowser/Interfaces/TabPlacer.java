package com.asg.ashish.privacybrowser.Interfaces;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.asg.ashish.privacybrowser.Adapter.ViewPagerAdapter;
import com.asg.ashish.privacybrowser.Fragments.WebFragment;

import java.util.Stack;

public interface TabPlacer {
    void placeTab(Fragment fragment, String tag);
    ViewPagerAdapter getAdapter();
    void remove(WebFragment fragment);
}
