package com.asg.ashish.privacybrowser.WebViewUtils;

import com.asg.ashish.privacybrowser.Fragments.WebFragment;

import java.util.Stack;

public class FragmentStorage {

    private String title;
    private WebFragment fragment;
    private Stack<String> history;

    public FragmentStorage(String title, WebFragment fragment, Stack<String> history) {
        this.title = title;
        this.fragment = fragment;
        this.history = history;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WebFragment getFragment() {
        return fragment;
    }

    public void setFragment(WebFragment fragment) {
        this.fragment = fragment;
    }

    public Stack<String> getHistory() {
        return history;
    }

    public void setHistory(Stack<String> history) {
        this.history = history;
    }
}
