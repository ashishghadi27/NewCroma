package com.asg.ashish.privacybrowser.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asg.ashish.privacybrowser.Adapter.ListAllTabsAdapter;
import com.asg.ashish.privacybrowser.Adapter.ViewPagerAdapter;
import com.asg.ashish.privacybrowser.Interfaces.FragmentListOperations;
import com.asg.ashish.privacybrowser.Interfaces.TabPlacer;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.Utilities.BaseFragment;
import com.asg.ashish.privacybrowser.WebViewUtils.FragmentStorage;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class AllTabs extends BaseFragment implements TabPlacer {

    private FragmentListOperations operations;
    private RecyclerView recyclerView;
    private ListAllTabsAdapter allTabsAdapter;
    private TextView addTab;
    private LinearLayout main;

    public AllTabs(FragmentListOperations operations) {
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
        return inflater.inflate(R.layout.fragment_all_tabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        addTab = view.findViewById(R.id.addTab);
        main = view.findViewById(R.id.main_container);
        setTheme();
        allTabsAdapter = new ListAllTabsAdapter(operations.getAllTabs(), getContext(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(allTabsAdapter);
        allTabsAdapter.notifyDataSetChanged();

        addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebFragment webFragment = new WebFragment(operations, getSearchEngineUrl(), null);
                operations.addTab(webFragment, getSearchEngineName());
                Stack<String> stack = new Stack<>();
                stack.push(getSearchEngineUrl());
                operations.setStack(stack, webFragment);
                replace(webFragment, "web" + operations.getCount() + 1);
            }
        });
    }

    @Override
    public void placeTab(Fragment fragment, String tag){
        replace(fragment, tag);
    }

    public ViewPagerAdapter getAdapter(){
        return new ViewPagerAdapter(getChildFragmentManager());
    }

    @Override
    public void remove(WebFragment fragment) {
        operations.removeFragment(fragment);
        allTabsAdapter.notifyDataSetChanged();
    }

    private void setTheme(){
        main.setBackground(ResourcesCompat.getDrawable(getResources(), getTheme(), Objects.requireNonNull(getActivity()).getTheme()));
    }

}