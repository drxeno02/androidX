package com.blog.ljtatum.androidx.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.framework.utilities.FrameworkUtils;
import com.blog.ljtatum.androidx.R;
import com.blog.ljtatum.androidx.activity.BaseActivity;
import com.blog.ljtatum.androidx.fragments.AboutFragment;
import com.blog.ljtatum.androidx.fragments.PrivacyFragment;
import com.blog.ljtatum.androidx.fragments.SettingsFragment;
import com.blog.ljtatum.androidx.fragments.ShareFragment;

/**
 * Created by leonard on 3/16/2017.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    // menu options
    private static final String SETTINGS = "Settings";
    private static final String SHARE = "Share";
    private static final String ABOUT = "About";
    private static final String PRIVACY = "Privacy";

    private final Context mContext;
    private String[] arryMenuOptions;
    private int[] arryMenuIcons;

    /**
     * Default constructor
     *
     * @param context         Interface to global information about an application environment
     * @param arryMenuOptions List of menu options for drawer
     */
    public DrawerAdapter(Context context, String[] arryMenuOptions, int[] arryMenuIcons) {
        mContext = context;
        this.arryMenuOptions = arryMenuOptions;
        this.arryMenuIcons = arryMenuIcons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_drawer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMenuOption.setText(arryMenuOptions[position]);
        holder.ivMenuIcon.setImageDrawable(FrameworkUtils.getDrawable(mContext, arryMenuIcons[position]));

        // process menu options
        switch (arryMenuOptions[position]) {
            case SETTINGS:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity) mContext).addFragment(new SettingsFragment());
                    }
                });
                break;
            case SHARE:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity) mContext).addFragment(new ShareFragment());
                    }
                });
                break;
            case ABOUT:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity) mContext).addFragment(new AboutFragment());
                    }
                });
                break;
            case PRIVACY:
                holder.tvMenuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity) mContext).addFragment(new PrivacyFragment());
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arryMenuOptions.length;
    }

    /**
     * View holder class
     * <p>A ViewHolder describes an item view and metadata about its place within the RecyclerView</p>
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        final TextView tvMenuOption;
        final ImageView ivMenuIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize views
            tvMenuOption = itemView.findViewById(R.id.tv_menu_option);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
        }
    }
}
