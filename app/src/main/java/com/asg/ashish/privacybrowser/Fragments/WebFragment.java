package com.asg.ashish.privacybrowser.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.asg.ashish.privacybrowser.Adapter.AutoSuggestAdapter;
import com.asg.ashish.privacybrowser.ApiCaller.VolleyUtil;
import com.asg.ashish.privacybrowser.Interfaces.FragmentListOperations;
import com.asg.ashish.privacybrowser.Interfaces.InstanceAccessor;
import com.asg.ashish.privacybrowser.Interfaces.WebViewBack;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.Utilities.BaseFragment;
import com.asg.ashish.privacybrowser.Utilities.Constants;
import com.asg.ashish.privacybrowser.WebViewUtils.KeyBoardInputListener;
import com.asg.ashish.privacybrowser.WebViewUtils.WebViewChromeRendererClient;
import com.asg.ashish.privacybrowser.WebViewUtils.WebViewRendererClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class WebFragment extends BaseFragment implements WebViewBack, InstanceAccessor {

    private AutoCompleteTextView addressBar;
    private WebView web;
    private WebSettings webSettings;
    private AppBarLayout appBarLayout;
    private LinearLayout optionsLay;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private ImageView clear, menu;
    private boolean cleared = false;
    private AutoSuggestAdapter autoSuggestAdapter;
    private VolleyUtil volleyUtil;
    private FragmentListOperations operations;
    private TextView tabSwitcher;
    private String url;
    private Stack<String> stack;
    boolean deactivateCanGoBack = false;
    private CoordinatorLayout main;

    public WebFragment(FragmentListOperations operations, String url, Stack<String> stack) {
        this.operations = operations;
        this.url = url;
        this.stack = stack;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);

    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addressBar = view.findViewById(R.id.addressBar);
        web = view.findViewById(R.id.webView);
        main = view.findViewById(R.id.main_container);
        setTheme();
        web.restoreState(savedInstanceState);
        appBarLayout = view.findViewById(R.id.appBarLay);
        optionsLay= view.findViewById(R.id.optionLay);
        progressBar = view.findViewById(R.id.progress);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        clear = view.findViewById(R.id.clear);
        menu = view.findViewById(R.id.menu);
        tabSwitcher = view.findViewById(R.id.tabSwitcher);
        tabSwitcher.setText(operations.getCount() + "");
        volleyUtil = new VolleyUtil(getContext(), this);
        initWebView();

        web.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY < 5){
                    appBarLayout.setExpanded(true);
                }
                else appBarLayout.setExpanded(false);
            }
        });

        addressBar.setOnEditorActionListener(new KeyBoardInputListener(addressBar, web, getContext(), this));

        addressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() == MotionEvent.ACTION_DOWN && optionsLay.getVisibility() == View.VISIBLE)) {
                        optionsLay.setVisibility(View.GONE);
                        addressBar.requestFocus();
                        openKeyboard(addressBar);
                        addressBar.setText(web.getUrl());
                        cleared = false;
                        clear.setVisibility(View.VISIBLE);
                        return true;
                    }
                return false;
            }
        });

        addressBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(optionsLay.getVisibility() == View.GONE && !addressBar.isFocused()){
                    optionsLay.setVisibility(View.VISIBLE);
                    closeKeyboard();
                    addressBar.setText(web.getTitle());
                    cleared = false;
                    setHttpsStatus(web.getUrl().contains("https"));
                    clear.setVisibility(View.GONE);
                }
            }
        });

        autoSuggestAdapter = new AutoSuggestAdapter(Objects.requireNonNull(getContext()),
                R.layout.suggest);
        addressBar.setThreshold(2);
        addressBar.setAdapter(autoSuggestAdapter);
        addressBar.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Log.v("SELECT", addressBar.getText()+"");
                        cleared = false;
                        String query = addressBar.getText().toString().trim();
                        if(query.contains("https") || query.contains("http")){
                            web.loadUrl(query);
                        }
                        else web.loadUrl(Constants.GOOGLE_SEARCH_ENGINE + query);
                        addressBar.clearFocus();
                    }
                });

        addressBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = addressBar.getText().toString().trim();
                if(!query.contains("https") && !query.contains("http") && cleared)
                    volleyUtil.getRequest(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = addressBar.getText().toString().trim();
                if(text.equals("")){
                    addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
                    cleared = true;
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                web.reload();
                refreshLayout.scheduleLayoutAnimation();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleared = true;
                addressBar.setText(null);
                addressBar.setCompoundDrawablesWithIntrinsicBounds(getSearchEngineLogo(), 0, 0, 0);
            }
        });

        tabSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragmentListStack();
                replace(new AllTabs(operations), "AllTabs");
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void initWebView(){
        webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);       //Zoom Control on web
        webSettings.setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
        webSettings.setAllowFileAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        web.setVerticalScrollBarEnabled(true);
        web.setWebViewClient(new WebViewRendererClient(this));
        web.setWebChromeClient(new WebViewChromeRendererClient(progressBar, this));
        web.setScrollContainer(true);
        if(!TextUtils.isEmpty(url)) web.loadUrl(url);
        else web.loadUrl(getSearchEngineUrl());
    }

    @Override
    public boolean goBack() {
        if(web.canGoBack() && ! deactivateCanGoBack){
            web.goBack();
            return false;
        }
        else if(stack != null && !stack.isEmpty()){
            Log.v("In upper", "else");
            deactivateCanGoBack = true;
            web.loadUrl(stack.pop());
            if(stack.isEmpty()) {
                deactivateCanGoBack = false;
                web.clearHistory();
            }
            return false;
        }
        else {
            Log.v("In this", "else");
            operations.removeFragment(this);
            return true;
        }
    }

    @Override
    public void stopRefreshing() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onPageStarted() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
    }

    @Override
    public void onPageFinished() {
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        operations.updateTitle(this, web.getTitle());
    }

    @Override
    public void setUrl(String url) {
        addressBar.setText(url);
    }

    @Override
    public void setHttpsStatus(boolean isHttps) {
        if(isHttps){
            addressBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_icons8_lock, 0, 0, 0);
        }
        else addressBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning_new, 0, 0, 0);
    }

    @Override
    public void initHttpsStatus() {
        addressBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void setAdapter(List<String> suggestions) {
        Log.v("LIST: " , suggestions.size() + "");
        autoSuggestAdapter.setData(suggestions);
        autoSuggestAdapter.notifyDataSetChanged();
    }

    @Override
    public String getSearchEngine() {
        return getSearchEngineUrl();
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
    }

    private void openKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public WebView getWebView(){
        return web;
    }

    public void setLoadUrl(String url){
        this.url = url;
        tabSwitcher.setText(operations.getCount() + "");
    }

    public void setStack(Stack<String> stack){
        this.stack = stack;
    }

    private void setFragmentListStack(){
        Stack<String> s = new Stack<>();
        WebBackForwardList forwardList = web.copyBackForwardList();
        for(int i = 0; i < forwardList.getSize(); i++){
            WebHistoryItem item = forwardList.getItemAtIndex(i);
            s.push(item.getUrl());
        }
        if(s.isEmpty()) s.push(getSearchEngineUrl());
        operations.setStack(s, this);
    }

    public void showDialog(){
        final BottomSheetDialog bottom_sheet_dialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
        View dialog = View.inflate(getContext(), R.layout.menulay, null);
        bottom_sheet_dialog.setContentView(dialog);
        bottom_sheet_dialog.show();

        final LinearLayout newTab, searchEngine, share, rate, report, reload, forward, dialogCont;

        newTab = dialog.findViewById(R.id.newTabMenu);
        searchEngine = dialog.findViewById(R.id.searchEngine);
        share = dialog.findViewById(R.id.share);
        rate = dialog.findViewById(R.id.rate);
        report = dialog.findViewById(R.id.report);
        reload = dialog.findViewById(R.id.reload);
        forward = dialog.findViewById(R.id.forward);
        dialogCont = dialog.findViewById(R.id.dialog_cont);
        dialogCont.setBackground(ResourcesCompat.getDrawable(getResources(), getTheme(), Objects.requireNonNull(getActivity()).getTheme()));


        if(web.canGoForward()) forward.setVisibility(View.VISIBLE);
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
                share(web.getUrl(), web.getTitle());
                bottom_sheet_dialog.dismiss();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.asg.ashish.privacybrowser"));
                ((Activity) Objects.requireNonNull(getContext())).startActivity(intent);
                bottom_sheet_dialog.dismiss();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = newEmailIntent(Constants.REPORT_MAIL, "Bug In Croma");
                Objects.requireNonNull(getContext()).startActivity(Intent.createChooser(i, "Select an email client"));
                bottom_sheet_dialog.dismiss();
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web.reload();
                bottom_sheet_dialog.dismiss();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(web.canGoForward()) web.goForward();
                else Toast.makeText(getContext(), "Can't Go Forward",Toast.LENGTH_SHORT).show();
                bottom_sheet_dialog.dismiss();
            }
        });
    }

    public void showSearchEngineDialog(){
        final BottomSheetDialog bottom_sheet_dialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
        View dialog = View.inflate(getContext(), R.layout.search_engine_dialog, null);
        bottom_sheet_dialog.setContentView(dialog);
        bottom_sheet_dialog.show();

        LinearLayout google, duckDuckGo, bing, reset, mainSearch;
        google = dialog.findViewById(R.id.google);
        duckDuckGo = dialog.findViewById(R.id.duck_duck_go);
        bing = dialog.findViewById(R.id.bing);
        reset = dialog.findViewById(R.id.reset);
        mainSearch = dialog.findViewById(R.id.main_search_cont);
        mainSearch.setBackground(ResourcesCompat.getDrawable(getResources(), getTheme(), Objects.requireNonNull(getActivity()).getTheme()));
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.GOOGLE_SEARCH_ENGINE, "Google", Constants.GOOGLE_SEARCH_LOGO);
                changeSearchEngine();
                bottom_sheet_dialog.dismiss();
            }
        });

        duckDuckGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.DUCK_DUCK_GO_SEARCH_ENGINE, "Duck Duck Go", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
                changeSearchEngine();
                bottom_sheet_dialog.dismiss();
            }
        });

        bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.BING_SEARCH_ENGINE, "Bing", Constants.BING_SEARCH_LOGO);
                changeSearchEngine();
                bottom_sheet_dialog.dismiss();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.DUCK_DUCK_GO_SEARCH_ENGINE, "Duck Duck Go", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
                changeSearchEngine();
                bottom_sheet_dialog.dismiss();
            }
        });

    }

    private void addTab(){
        setFragmentListStack();
        WebFragment webFragment = new WebFragment(operations, getSearchEngineUrl(), null);
        operations.addTab(webFragment, getSearchEngineName());
        Stack<String> stack = new Stack<>();
        stack.push(getSearchEngineUrl());
        operations.setStack(stack, webFragment);
        replace(webFragment, "web" + operations.getCount() + 1);
    }

    private void changeSearchEngine(){
        String url = web.getUrl();
        String engine = getSearchEngineUrl();
        if(url.equals(Constants.GOOGLE_AFTER_URL) || url.contains(Constants.BING_AFTER_URL) || url.equals(Constants.DUCK_AFTER_URL))
            web.loadUrl(engine);
    }

    private void setTheme(){
        main.setBackground(ResourcesCompat.getDrawable(getResources(), getTheme(), Objects.requireNonNull(getActivity()).getTheme()));
    }

}