package com.asg.ashish.privacybrowser.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
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
    private static int CLICK_THRESHOLD = 500;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        //private WebView webView;
        private CardView previewCont;
        private LinearLayout closeTab;
        private ImageView cachePreview, favicon;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            //webView = view.findViewById(R.id.webView);
            previewCont = view.findViewById(R.id.previewCont);
            closeTab = view.findViewById(R.id.closeTab);
            cachePreview = view.findViewById(R.id.cachePreview);
            favicon = view.findViewById(R.id.favicon);
            /*webView.setInitialScale(100);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/
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
    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FragmentStorage tab = listItems.get(position);
        WebView webView = tab.getFragment().getWebView();
        holder.title.setText(webView.getTitle());
        holder.favicon.setImageBitmap(webView.getFavicon());
        if(tab.getHistory() != null && !tab.getHistory().isEmpty()){
            Bitmap bitmap = webView.getDrawingCache();
            if(bitmap != null && !bitmap.isRecycled()) {
                holder.cachePreview.setImageBitmap(bitmap);
                holder.cachePreview.setVisibility(View.VISIBLE);
            }
        }

        holder.cachePreview.setOnTouchListener(new View.OnTouchListener() {
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
