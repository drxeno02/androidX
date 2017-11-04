package com.blog.ljtatum.androidx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.framework.sharedpref.SharedPref;
import com.app.framework.utilities.DeviceUtils;
import com.app.framework.utilities.FrameworkUtils;
import com.blog.ljtatum.androidx.R;
import com.blog.ljtatum.androidx.activity.MainActivity;

/**
 * Created by LJTat on 2/27/2017.
 */

public class SettingsFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private Context mContext;
    private View mRootView;
    private TextView tvFragmentHeader;
    private SharedPref mSharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // instantiate views
        initializeViews();
        initializeHandlers();
        initializeListeners();

        return mRootView;
    }

    /**
     * Method is used to instantiate views
     */
    private void initializeViews() {
        mContext = getActivity();
        mSharedPref = new SharedPref(mContext, com.app.framework.constants.Constants.PREF_FILE_NAME);

        // instantiate views
        tvFragmentHeader = mRootView.findViewById(R.id.tv_fragment_header);


        // set fragment header
        tvFragmentHeader.setText(getResources().getString(R.string.setting));

        // update settings
        updateSettings();
    }

    /**
     * Method is used to set click listeners
     */
    private void initializeHandlers() {
        tvFragmentHeader.setOnClickListener(this);

    }

    /**
     * Method is used to initialize listeners and callbacks
     */
    private void initializeListeners() {

    }

    /**
     * Method is used to update settings
     */
    private void updateSettings() {

    }

    @Override
    public void onClick(View v) {
        if (!FrameworkUtils.isViewClickable()) {
            return;
        }

        switch (v.getId()) {
            case R.id.tv_fragment_header:
                remove();
                popBackStack();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // disable drawer
        ((MainActivity) mContext).toggleDrawerState(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        // hide keyboard
        DeviceUtils.hideKeyboard(mContext, getActivity().getWindow().getDecorView().getWindowToken());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!FrameworkUtils.checkIfNull(mOnFragmentRemovedListener)) {
            // set listener
            mOnFragmentRemovedListener.onFragmentRemoved();
        }
        // hide keyboard
        DeviceUtils.hideKeyboard(mContext, getActivity().getWindow().getDecorView().getWindowToken());
        // enable drawer
        ((MainActivity) mContext).toggleDrawerState(true);
    }

}
