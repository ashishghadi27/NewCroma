package com.asg.ashish.privacybrowser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.asg.ashish.privacybrowser.Interfaces.TabPlacer;
import com.asg.ashish.privacybrowser.R;
import com.asg.ashish.privacybrowser.WebViewUtils.FragmentStorage;

import java.util.List;

public class ListAllTabsAdapter extends RecyclerView.Adapter<ListAllTabsAdapter.MyViewHolder> {

    private List<FragmentStorage> listItems;
    private TabPlacer placer;
    private static int CLICK_THRESHOLD = 100;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private WebView webView;
        private CardView previewCont;
        private LinearLayout closeTab;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            webView = view.findViewById(R.id.webView);
            previewCont = view.findViewById(R.id.previewCont);
            closeTab = view.findViewById(R.id.closeTab);
            webView.setInitialScale(100);
            webView.getSettings().setJavaScriptEnabled(true);
        }

    }

    public ListAllTabsAdapter(List<FragmentStorage> list, Context context, TabPlacer placer) {
        this.listItems = list;
        this.placer = placer;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab_preview, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FragmentStorage tab = listItems.get(position);
        holder.title.setText(tab.getTitle());
        if(tab.getHistory() != null && !tab.getHistory().isEmpty())
            holder.webView.loadUrl(tab.getHistory().peek());
        //else holder.webView.loadUrl(tab.getFragment().getSearchEngineUrl());

        holder.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long duration = event.getEventTime() - event.getDownTime();
                if (event.getAction() == MotionEvent.ACTION_UP && duration < CLICK_THRESHOLD)
                    holder.previewCont.performClick();
                return true;
            }
        });

        holder.previewCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placer.placeTab(tab.getFragment(), tab.getFragment().getTag());
                if(tab.getHistory() != null && !tab.getHistory().isEmpty())
                    tab.getFragment().setLoadUrl(tab.getHistory().pop());
                tab.getFragment().setStack(tab.getHistory());
            }
        });

        holder.closeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placer.remove(tab.getFragment());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

}
