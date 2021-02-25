package com.asg.ashish.privacybrowser.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.asg.ashish.privacybrowser.Interfaces.FragmentListOperations;
import com.asg.ashish.privacybrowser.Interfaces.WebViewBack;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.Utilities.BaseFragment;
import com.asg.ashish.privacybrowser.Utilities.Constants;
import com.asg.ashish.privacybrowser.WebViewUtils.MenuFunctions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;
import java.util.Stack;

public class HomeFragment extends BaseFragment {

    private AutoCompleteTextView addressBar;
    private FragmentListOperations operations;
    private TextView tabSwitcher;
    private ImageView menu;

    public HomeFragment(FragmentListOperations operations) {
        this.operations = operations;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addressBar = view.findViewById(R.id.addressBar);
        tabSwitcher = view.findViewById(R.id.tabSwitcher);
        menu = view.findViewById(R.id.menu);
        addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
        tabSwitcher.setText(operations.getCount() + "");

        addressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    addTab();
                    return true;
                }
                return false;
            }
        });

        tabSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new AllTabs(operations), "AllTabs");
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void addTab(){
        WebFragment webFragment = new WebFragment(operations, getSearchEngineUrl(), null);
        operations.addTab(webFragment, getSearchEngineName());
        Stack<String> stack = new Stack<>();
        stack.push(getSearchEngineUrl());
        operations.setStack(stack, webFragment);
        addFragment(webFragment, "web" + operations.getCount() + 1);
    }
    public void showDialog(){
        final BottomSheetDialog bottom_sheet_dialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
        View dialog = View.inflate(getContext(), R.layout.menu_home, null);
        bottom_sheet_dialog.setContentView(dialog);
        bottom_sheet_dialog.show();

        LinearLayout newTab, searchEngine, share, rate, report;

        newTab = dialog.findViewById(R.id.newTabMenu);
        searchEngine = dialog.findViewById(R.id.searchEngine);
        share = dialog.findViewById(R.id.share);
        rate = dialog.findViewById(R.id.rate);
        report = dialog.findViewById(R.id.report);

        newTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTab();
                bottom_sheet_dialog.dismiss();
            }
        });

        searchEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchEngineDialog();
                bottom_sheet_dialog.dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void showSearchEngineDialog(){
        final BottomSheetDialog bottom_sheet_dialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
        View dialog = View.inflate(getContext(), R.layout.search_engine_dialog, null);
        bottom_sheet_dialog.setContentView(dialog);
        bottom_sheet_dialog.show();

        LinearLayout google, duckDuckGo, bing, reset;
        google = dialog.findViewById(R.id.google);
        duckDuckGo = dialog.findViewById(R.id.duck_duck_go);
        bing = dialog.findViewById(R.id.bing);
        reset = dialog.findViewById(R.id.reset);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.GOOGLE_SEARCH_ENGINE, "Google", Constants.GOOGLE_SEARCH_LOGO);
                addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
                bottom_sheet_dialog.dismiss();
            }
        });

        duckDuckGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.DUCK_DUCK_GO_SEARCH_ENGINE, "Duck Duck Go", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
                addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
                bottom_sheet_dialog.dismiss();
            }
        });

        bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.BING_SEARCH_ENGINE, "Bing", Constants.BING_SEARCH_LOGO);
                addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
                bottom_sheet_dialog.dismiss();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.DUCK_DUCK_GO_SEARCH_ENGINE, "Duck Duck Go", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
                addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
                bottom_sheet_dialog.dismiss();
            }
        });

    }
}