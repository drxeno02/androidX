package com.blog.ljtatum.androidx.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blog.ljtatum.androidx.R;

import java.util.ArrayList;

/**
 * Created by leonard on 3/16/2017.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    // menu options
    private static final String SETTINGS = "Settings";
    private static final String SHARE = "Share";
    private static final String ABOUT = "About";
    private static final String PRIVACY = "Privacy";
    private final ArrayList<String> alDrawerMenuOptions;
    private final Context mContext;

    /**
     * Default constructor
     *
     * @param context       Interface to global information about an application environment
     * @param alMenuOptions List of menu options for drawer
     */
    public DrawerAdapter(Context context, ArrayList<String> alMenuOptions) {
        mContext = context;
        alDrawerMenuOptions = alMenuOptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_drawer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMenuOption.setText(alDrawerMenuOptions.get(position));

        // process menu options
        switch (alDrawerMenuOptions.get(position)) {
            case SETTINGS:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case SHARE:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case ABOUT:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case PRIVACY:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return alDrawerMenuOptions.size();
    }

    /**
     * View holder class
     * <p>A ViewHolder describes an item view and metadata about its place within the RecyclerView</p>
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        final TextView tvMenuOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize views
            tvMenuOption = itemView.findViewById(R.id.tv_menu_option);
        }
    }
}
