package com.asg.ashish.privacybrowser.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.provider.Contacts;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asg.ashish.privacybrowser.Interfaces.FragmentListOperations;
import com.asg.ashish.privacybrowser.Interfaces.WebViewBack;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.Utilities.BaseFragment;
import com.asg.ashish.privacybrowser.Utilities.Constants;
import com.asg.ashish.privacybrowser.WebViewUtils.MenuFunctions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Objects;
import java.util.Stack;

public class HomeFragment extends BaseFragment{

    private AutoCompleteTextView addressBar;
    private FragmentListOperations operations;
    private TextView tabSwitcher;
    private ImageView menu;
    private RelativeLayout main;
    private BottomSheetDialog bottom_sheet_dialog;

    public HomeFragment(FragmentListOperations operations) {
        this.operations = operations;
    }

    public HomeFragment() {

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

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addressBar = view.findViewById(R.id.addressBar);
        tabSwitcher = view.findViewById(R.id.tabSwitcher);
        menu = view.findViewById(R.id.menu);
        main = view.findViewById(R.id.main_container);
        setTheme();
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
        bottom_sheet_dialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
        View dialog = View.inflate(getContext(), R.layout.menu_home, null);
        bottom_sheet_dialog.setContentView(dialog);
        bottom_sheet_dialog.show();

        final LinearLayout newTab, searchEngine, share, rate, report, clearCache;
        RelativeLayout dialogCont;
        TextView cacheText;
        CardView theme1, theme2, theme3, theme4, theme5, theme6, theme7, theme8;
        theme1 = dialog.findViewById(R.id.purple);
        theme2 = dialog.findViewById(R.id.greenish);
        theme3 = dialog.findViewById(R.id.reddish_yellow);
        theme4 = dialog.findViewById(R.id.pinkish_blue);
        theme5 = dialog.findViewById(R.id.theme5);
        theme6 = dialog.findViewById(R.id.theme6);
        theme7 = dialog.findViewById(R.id.theme7);
        theme8 = dialog.findViewById(R.id.theme8);

        newTab = dialog.findViewById(R.id.newTabMenu);
        searchEngine = dialog.findViewById(R.id.searchEngine);
        share = dialog.findViewById(R.id.share);
        rate = dialog.findViewById(R.id.rate);
        report = dialog.findViewById(R.id.report);
        clearCache = dialog.findViewById(R.id.clear_cache);
        cacheText = dialog.findViewById(R.id.cacheText);
        String size = "Clear Cache (" + getCacheSize() + ")";
        cacheText.setText(size);
        dialogCont = dialog.findViewById(R.id.dialog_cont);
        dialogCont.setBackground(ResourcesCompat.getDrawable(getResources(), getTheme(), Objects.requireNonNull(getActivity()).getTheme()));

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
                share(Constants.GET_CROMA, "Download Croma");
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
            }
        });

        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache(getContext());
                bottom_sheet_dialog.dismiss();
            }
        });

        theme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back, R.color.suggest1);
            }
        });

        theme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back2, R.color.suggest2);
            }
        });

        theme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back3, R.color.suggest3);
            }
        });

        theme4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back4, R.color.suggest4);
            }
        });

        theme5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back5, R.color.suggestDefault);
            }
        });

        theme6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back6, R.color.suggestDefault);
            }
        });

        theme7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back7, R.color.suggestDefault);
            }
        });

        theme8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSaving(R.drawable.browser_back8, R.color.suggestDefault);
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

    public void setOperations(FragmentListOperations operations){
        this.operations = operations;
    }

    private void setTheme(){
        main.setBackground(ResourcesCompat.getDrawable(getResources(), getTheme(), Objects.requireNonNull(getActivity()).getTheme()));
    }


    public void startSaving(int themeId, int suggestId){
        saveTheme(themeId, suggestId);
        operations.setActivityTheme();
        setTheme();
        bottom_sheet_dialog.dismiss();
    }

    private String getCacheSize() {
        long size = 0;
        size += getDirSize(Objects.requireNonNull(getContext()).getCacheDir());
        size += getDirSize(Objects.requireNonNull(getContext().getExternalCacheDir()));
        size += getDirSize(Objects.requireNonNull(getContext().getCodeCacheDir()));
        Log.v("Size",size + "");
        if(size / (1024 * 1024) > 0) return size / (1024 * 1024) + " MB";
        else return size / (1024) + " KB";
    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            dir = context.getExternalCacheDir();
            deleteDir(dir);
            dir = context.getCodeCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}