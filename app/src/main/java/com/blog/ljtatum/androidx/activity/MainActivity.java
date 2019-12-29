package com.blog.ljtatum.androidx.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.framework.sharedpref.SharedPref;
import com.app.framework.utilities.FrameworkUtils;
import com.app.framework.utilities.NetworkReceiver;
import com.app.framework.utilities.NetworkUtils;
import com.blog.ljtatum.androidx.R;
import com.blog.ljtatum.androidx.adapter.DrawerAdapter;
import com.blog.ljtatum.androidx.constants.Constants;
import com.blog.ljtatum.androidx.constants.Durations;
import com.blog.ljtatum.androidx.gui.iPhoneNotch;
import com.blog.ljtatum.androidx.service.iPhoneNotchService;
import com.blog.ljtatum.androidx.utils.DialogUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        NetworkReceiver.NetworkStatusObserver {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String PACKAGE = "package:";
    private static final String AD_ID_TEST = "950036DB8197D296BE390357BD9A964E";
    private static final int PERMISSION_REQUEST_CODE_DRAW_OVER_APP = 100; // permissions

    private Context mContext;
    private TextView tvEnableAndroidx;
    private ImageView ivHambugerMenu, ivEnableAndroidxInfo;
    private RelativeLayout mDrawer;
    private DrawerLayout mDrawerLayout;
    private iPhoneNotch mPhoneNotchView;
    private SharedPref mSharedPref;
    private AdView adView; // container for banner ads

    // network
    private NetworkReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_container);

        // initialize views and listeners
        initializeViews();
        initializeHandlers();
        initializeListeners();
    }

    /**
     * Method is used to initialize views
     */
    private void initializeViews() {
        mContext = MainActivity.this;
        mNetworkReceiver = new NetworkReceiver();
        mSharedPref = new SharedPref(mContext, com.app.framework.constants.Constants.PREF_FILE_NAME);

        // drawer
        tvEnableAndroidx = findViewById(R.id.tv_enable_androidx);
        ivEnableAndroidxInfo = findViewById(R.id.iv_enable_androidx_info);
        ivHambugerMenu = findViewById(R.id.iv_hamburger_menu);
        mDrawer = findViewById(R.id.drawer);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        RecyclerView mDrawerList = mDrawerLayout.findViewById(R.id.rv_menu);

        // update drawer menu options
        String[] arryMenuOptions = mContext.getResources().getStringArray(R.array.array_menu_options);
        int[] arryMenuIcons = {R.drawable.settings, R.drawable.share, R.drawable.about, R.drawable.privacy};
        mDrawerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DrawerAdapter drawerAdapter = new DrawerAdapter(this, arryMenuOptions, arryMenuIcons);
        mDrawerList.setAdapter(drawerAdapter);

        // default AndroidX mode
        if (mSharedPref.getBooleanPref(Constants.KEY_ANDROIDX_MODE, false)) {
            // enable AndroidX mode
            toggleAndroidXMode(true);
        } else {
            // disable AndroidX mode
            toggleAndroidXMode(false);
        }

        // ad banner
        adView = this.findViewById(R.id.ad_view);
        try {
            if (NetworkUtils.isNetworkAvailable(mContext)
                    && NetworkUtils.isConnected(mContext)) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // request banner ads
                        AdRequest adRequestBanner;
                        if (Constants.DEBUG) {
                            // load test ad
                            adRequestBanner = new AdRequest.Builder().addTestDevice(AD_ID_TEST).build();
                        } else {
                            // load production ad
                            adRequestBanner = new AdRequest.Builder().build();
                        }
                        // load banner ads
                        adView.loadAd(adRequestBanner);
                    }
                }, Durations.DELAY_INTERVAL_MS_500);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            adView.setBackgroundResource(R.drawable.banner);
        }
    }

    /**
     * Method is used to set click listeners
     */
    private void initializeHandlers() {
        tvEnableAndroidx.setOnClickListener(this);
        ivEnableAndroidxInfo.setOnClickListener(this);
        ivHambugerMenu.setOnClickListener(this);
    }

    /**
     * Method is used to initialize listeners and callbacks
     */
    private void initializeListeners() {
        // adView listener
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // do nothing
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // code to be executed when an ad request fails
                adView.setBackgroundResource(R.drawable.banner);
            }

            @Override
            public void onAdOpened() {
                // do nothing
            }

            @Override
            public void onAdLeftApplication() {
                // do nothing
            }

            @Override
            public void onAdClosed() {
                // do nothing
            }
        });
    }

    @SuppressLint("NewApi")
    private boolean checkDrawOverAppPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(mContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(PACKAGE.concat(getPackageName())));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE_DRAW_OVER_APP);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method is used to toggle AndroidX mode
     *
     * @param isEnabled True to enable AndroidX mode
     */
    private void toggleAndroidXMode(boolean isEnabled) {
        if (isEnabled) {
            // update AndroidX mode settings
            mSharedPref.setPref(Constants.KEY_ANDROIDX_MODE, true);

            // default power saving
            if (mSharedPref.getBooleanPref(Constants.KEY_POWER_SAVING, true)) {
                // add notch, no service
                mPhoneNotchView = new iPhoneNotch(this);
            } else {
                // add notch and run service
                mContext.startService(new Intent(mContext, iPhoneNotchService.class));
            }

            // set views
            tvEnableAndroidx.setBackground(FrameworkUtils.getDrawable(mContext, R.drawable.bg_grey_rad50));
            tvEnableAndroidx.setText(getResources().getString(R.string.disable_androidx));
        } else {
            // update AndroidX mode settings
            mSharedPref.setPref(Constants.KEY_ANDROIDX_MODE, false);

            // set views
            tvEnableAndroidx.setBackground(FrameworkUtils.getDrawable(mContext, R.drawable.bg_blue_rad50));
            tvEnableAndroidx.setText(getResources().getString(R.string.enable_androidx));

            // destroy notch
            destroyPhoneNotch();
        }
    }

    /**
     * Method is used to open and close the drawer
     *
     * @param isOpen True if drawer is open, otherwise false
     */
    private void setDrawerState(boolean isOpen) {
        if (!FrameworkUtils.checkIfNull(mDrawerLayout)) {
            if (isOpen) {
                mDrawerLayout.openDrawer(mDrawer);
            } else {
                mDrawerLayout.closeDrawer(mDrawer);
            }
        }
    }

    /**
     * Method is used to enable/disable drawer
     *
     * @param isEnabled True to enable drawer interaction, otherwise disable interaction
     */
    public void toggleDrawerState(boolean isEnabled) {
        if (!FrameworkUtils.checkIfNull(mDrawerLayout)) {
            if (isEnabled) {
                // only unlock (enable) drawer interaction if it is disabled
                if (mDrawerLayout.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            } else {
                // only allow disabling of drawer interaction if the drawer is closed
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (!FrameworkUtils.isViewClickable()) {
            return;
        }

        switch (view.getId()) {
            case R.id.iv_hamburger_menu:
                setDrawerState(true);
                break;
            case R.id.tv_enable_androidx:
                if (checkDrawOverAppPermission()) {
                    // default AndroidX mode
                    if (mSharedPref.getBooleanPref(Constants.KEY_ANDROIDX_MODE, false)) {
                        // enable AndroidX mode
                        toggleAndroidXMode(false);
                    } else {
                        // disable AndroidX mode
                        toggleAndroidXMode(true);
                    }
                }
                break;
            case R.id.iv_enable_androidx_info:
                Crouton.showText(MainActivity.this, getResources()
                        .getString(R.string.enable_androidx_feature_info), Style.INFO);
                break;
            default:
                break;
        }
    }

    /**
     * Method is used to destroy iPhone notch
     */
    private void destroyPhoneNotch() {
        if (!mSharedPref.getBooleanPref(Constants.KEY_POWER_SAVING, true)) {
            // stop service
            mContext.stopService(new Intent(mContext, iPhoneNotchService.class));
        }

        if (!FrameworkUtils.checkIfNull(mPhoneNotchView)) {
            mPhoneNotchView.destroy();
            mPhoneNotchView = null;
        }
        // invoke garbage collector
        System.gc();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (FrameworkUtils.checkIfNull(getTopFragment())) {
            if (requestCode == PERMISSION_REQUEST_CODE_DRAW_OVER_APP &&
                    Settings.canDrawOverlays(this)) {
                // update draw over app settings
                mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, true);
                // enable AndroidX mode
                toggleAndroidXMode(true);
            } else {
                // update draw over app settings
                mSharedPref.setPref(Constants.KEY_DRAW_OVER_APP, false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume adview
        adView.resume();
        // only register receiver if it has not already been registered
        if (!mNetworkReceiver.contains(this)) {
            // register network receiver
            mNetworkReceiver.addObserver(this);
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            // print observer list
            mNetworkReceiver.printObserverList();
        }
    }

    @Override
    protected void onPause() {
        // pause adview
        adView.pause();
        // unregister network receiver
        if (mNetworkReceiver.getObserverSize() > 0 && mNetworkReceiver.contains(this)) {
            try {
                // unregister network receiver
                unregisterReceiver(mNetworkReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            mNetworkReceiver.removeObserver(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // destroy notch ONLY if Service is not active
        if (mSharedPref.getBooleanPref(Constants.KEY_POWER_SAVING, true)) {
            if (!FrameworkUtils.checkIfNull(mPhoneNotchView)) {
                mPhoneNotchView.destroy();
                mPhoneNotchView = null;

                // invoke garbage collector
                System.gc();
            }
        }
        // unregister network receiver
        if (mNetworkReceiver.getObserverSize() > 0 && mNetworkReceiver.contains(this)) {
            try {
                // unregister network receiver
                unregisterReceiver(mNetworkReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            mNetworkReceiver.removeObserver(this);
        }
        if (!FrameworkUtils.checkIfNull(adView)) {
            // destroy the adview
            adView.destroy();
        }
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            // close drawer
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void notifyConnectionChange(boolean isConnected) {
        if (isConnected) {
            // app is connected to network
            DialogUtils.dismissNoNetworkDialog();
        } else {
            // app is not connected to network
            DialogUtils.showDefaultNoNetworkAlert(mContext, null,
                    mContext.getString(R.string.check_network));
        }
    }
}
