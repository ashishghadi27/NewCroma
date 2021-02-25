package com.asg.ashish.privacybrowser.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;

import androidx.fragment.app.Fragment;

import com.asg.ashish.privacybrowser.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

public class BaseFragment extends Fragment {

    public void addFragment(Fragment fragment, String tag){
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right)
                .add(R.id.fragment_container, fragment, tag).addToBackStack(tag)
                .commit();
    }

    public void replace(Fragment fragment, String tag){
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right)
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    public void share(String link, String title){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title + " - " +link);
        startActivity(Intent.createChooser(shareIntent, title));
    }

    /*public void showDialog(){
        BottomSheetDialog bottom_sheet_dialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
        View dialog = View.inflate(getContext(), R.layout.menu_home, null);
        bottom_sheet_dialog.setContentView(dialog);
        bottom_sheet_dialog.show();
    }*/

    /*public void showSearchEngineDialog(){
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
             bottom_sheet_dialog.dismiss();
            }
        });

        duckDuckGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.DUCK_DUCK_GO_SEARCH_ENGINE, "Duck Duck Go", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
                bottom_sheet_dialog.dismiss();
            }
        });

        bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.BING_SEARCH_ENGINE, "Bing", Constants.BING_SEARCH_LOGO);
                bottom_sheet_dialog.dismiss();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchEngine(Constants.DUCK_DUCK_GO_SEARCH_ENGINE, "Duck Duck Go", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
                bottom_sheet_dialog.dismiss();
            }
        });

    }*/

    public Intent newEmailIntent(String address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //intent.setType("message/rfc822");
        intent.setType("text/plain");
        return intent;
    }

    public void setSearchEngine(String url, String name, int logo){
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("engine", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("url", url);
        editor.putString("name", name);
        editor.putInt("logo", logo);
        editor.apply();
    }

    public String getSearchEngineUrl(){
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("engine", Context.MODE_PRIVATE);
        return preferences.getString("url", Constants.DUCK_DUCK_GO_SEARCH_ENGINE);
    }

    public String getSearchEngineName(){
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("engine", Context.MODE_PRIVATE);
        return preferences.getString("name", "Duck Duck Go");
    }

    public int getSearchEngineLogo(){
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("engine", Context.MODE_PRIVATE);
        return preferences.getInt("logo", Constants.DUCK_DUCK_GO_SEARCH_LOGO);
    }

}
