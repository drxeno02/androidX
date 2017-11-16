package com.blog.ljtatum.androidx.gui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.blog.ljtatum.androidx.R;

/**
 * Created by LJTat on 11/4/2017.
 */

public class iPhoneNotch extends View {

    private final Context mContext;
    private FrameLayout mFrameLayout;
    private WindowManager mWindowManager;

    public iPhoneNotch(Context context) {
        super(context);

        mContext = context;
        mFrameLayout = new FrameLayout(mContext);

        // create notch and add to window manager
        addNotchToWindowManager();
    }

    /**
     * Method is used to add iPhoneX notch to window manager
     */
    private void addNotchToWindowManager() {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    // Allows the view to be on top of the StatusBar
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Keeps the button presses from going to the background window
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            // Enables the notification to recieve touch events
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            // Draws over status bar
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    // Allows the view to be on top of the StatusBar
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    // Keeps the button presses from going to the background window
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            // Enables the notification to recieve touch events
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            // Draws over status bar
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        // return the handle to a system-level service by name
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // adding views to window manager will display (draw) the view over any thing
        mWindowManager.addView(mFrameLayout, params);

        LayoutInflater rootView = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate a new view hierarchy from the specified xml resource
        rootView.inflate(R.layout.item_iphone_notch, mFrameLayout);
    }

    /**
     * Method is used to remove window manager view
     */
    public void destroy() {
        mWindowManager.removeView(mFrameLayout);
        mFrameLayout = null;
    }
}
