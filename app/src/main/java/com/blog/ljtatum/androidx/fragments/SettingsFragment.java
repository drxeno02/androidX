package com.blog.ljtatum.androidx.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.app.framework.sharedpref.SharedPref;
import com.app.framework.utilities.DeviceUtils;
import com.app.framework.utilities.FrameworkUtils;
import com.blog.ljtatum.androidx.R;
import com.blog.ljtatum.androidx.activity.MainActivity;
import com.blog.ljtatum.androidx.constants.Constants;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by LJTat on 2/27/2017.
 */

public class SettingsFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final String PACKAGE = "package:";
    private static final int PERMISSION_REQUEST_CODE_DRAW_OVER_APP = 100; // permissions

    private Context mContext;
    private View mRootView;
    private TextView tvFragmentHeader, tvPowerSaving, tvDrawOverApp;
    private ImageView ivPowerSaving, ivDrawOverApp;
    private Switch switchPowerSaving, switchDrawOverApp;
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
        tvPowerSaving = mRootView.findViewById(R.id.tv_power_saving);
        tvDrawOverApp = mRootView.findViewById(R.id.tv_draw_over_app);
        ivPowerSaving = mRootView.findViewById(R.id.iv_power_saving);
        ivDrawOverApp = mRootView.findViewById(R.id.iv_draw_over_app);
        switchPowerSaving = mRootView.findViewById(R.id.switch_power_saving);
        switchDrawOverApp = mRootView.findViewById(R.id.switch_draw_over_app);

        // set fragment header
        tvFragmentHeader.setText(getResources().getString(R.string.setting));

        // check for permissions
        if (checkDrawOverAppPermission()) {
            // update draw over app settings
            mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, true);
        }

        // update settings
        updateSettings();
    }

    /**
     * Method is used to set click listeners
     */
    private void initializeHandlers() {
        tvFragmentHeader.setOnClickListener(this);
        ivPowerSaving.setOnClickListener(this);
        ivDrawOverApp.setOnClickListener(this);

    }

    /**
     * Method is used to initialize listeners and callbacks
     */
    private void initializeListeners() {
        // OnCheckedChangeListener
        switchPowerSaving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // update power saving settings
                mSharedPref.setPref(Constants.KEY_POWER_SAVING, b);
                tvPowerSaving.setText(b ? getActivity().getResources().getString(R.string.settings_power_saving, "Enabled") :
                        getActivity().getResources().getString(R.string.settings_power_saving, "Disabled"));
                // show banner
                Crouton.showText(getActivity(), "Saved!", Style.CONFIRM);
            }
        });

        // OnCheckedChangeListener
        switchDrawOverApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (checkDrawOverAppPermission()) {
                        // update draw over app settings
                        mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, true);
                        tvDrawOverApp.setText(getActivity().getResources().getString(R.string.settings_draw_over_app, "Enabled"));
                        // show banner
                        Crouton.showText(getActivity(), "Saved!", Style.CONFIRM);
                    } else {
                        // update draw over app settings
                        mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, false);
                        tvDrawOverApp.setText(getActivity().getResources().getString(R.string.settings_draw_over_app, "Disabled"));
                        // show banner
                        Crouton.showText(getActivity(), "Sorry, this device does not support this feature", Style.ALERT);
                    }
                } else {
                    // update draw over app settings
                    mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, false);
                    tvDrawOverApp.setText(getActivity().getResources().getString(R.string.settings_draw_over_app, "Disabled"));
                }
            }
        });
    }

    /**
     * Method is used to update settings
     */
    private void updateSettings() {
        // default power saving
        if (mSharedPref.getBooleanPref(Constants.KEY_POWER_SAVING, true)) {
            // enable
            switchPowerSaving.setChecked(true);
        } else {
            // disable
            switchPowerSaving.setChecked(false);
        }

        // default draw over app (dictated by permissions): FALSE
        if (mSharedPref.getBooleanPref(Constants.KEY_DRAW_OVER_APP, false)) {
            // enable
            switchDrawOverApp.setChecked(true);
        } else {
            // disable
            switchDrawOverApp.setChecked(false);
        }

        // update default label values
        tvPowerSaving.setText(mSharedPref.getBooleanPref(Constants.KEY_POWER_SAVING, true) ?
                getActivity().getResources().getString(R.string.settings_power_saving, "Enabled") :
                getActivity().getResources().getString(R.string.settings_power_saving, "Disabled"));

        // update default label values
        tvDrawOverApp.setText(mSharedPref.getBooleanPref(Constants.KEY_DRAW_OVER_APP, false) ?
                getActivity().getResources().getString(R.string.settings_draw_over_app, "Enabled") :
                getActivity().getResources().getString(R.string.settings_draw_over_app, "Disabled"));
    }

    @SuppressLint("NewApi")
    private boolean checkDrawOverAppPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(mContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(PACKAGE.concat(mContext.getPackageName())));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE_DRAW_OVER_APP);
            return false;
        } else {
            return true;
        }
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
            case R.id.iv_power_saving:
                Crouton.showText(getActivity(), getActivity().getResources().getString(R.string.settings_power_saving_info), Style.INFO);
                break;
            case R.id.iv_draw_over_app:
                Crouton.showText(getActivity(), getActivity().getResources().getString(R.string.settings_draw_over_app_info), Style.INFO);
                break;
            default:
                break;
        }
    }


    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE_DRAW_OVER_APP) {
            if (Settings.canDrawOverlays(mContext)) {
                // update draw over app settings
                mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, true);
            } else {
                // update draw over app settings
                mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, false);
            }
            // update settings
            updateSettings();
            // show banner
            Crouton.showText(getActivity(), "Saved!", Style.CONFIRM);
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
