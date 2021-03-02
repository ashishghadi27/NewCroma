package com.asg.ashish.privacybrowser.Interfaces;

import com.asg.ashish.privacybrowser.Fragments.WebFragment;
import com.asg.ashish.privacybrowser.WebViewUtils.FragmentStorage;

import java.util.List;
import java.util.Stack;

public interface FragmentListOperations {
    void addTab(WebFragment fragment, String title);
    List<FragmentStorage> getAllTabs();
    void updateTitle(WebFragment fragment, String title);
    void setStack(Stack<String> stack, WebFragment fragment);
    int getCount();
    void removeFragment(WebFragment fragment);
    void setActivityTheme();
}
