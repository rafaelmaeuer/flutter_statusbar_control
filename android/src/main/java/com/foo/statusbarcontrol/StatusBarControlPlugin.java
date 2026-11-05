package com.foo.statusbarcontrol;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * StatusBarControlPlugin
 */
public class StatusBarControlPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
    private static final String channelName = "status_bar_control";
    private static final String statusBarOverlayTag = "StatusBarControl:statusBarOverlay";
    private static final String navigationBarOverlayTag = "StatusBarControl:navigationBarOverlay";

    private MethodChannel channel;
    private static Activity activity;

    /**
     * Plugin registration.
     */

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        // The plugin is now attached to a Flutter experience
        Log.d("StatusBarControl", "StatusBarControl: Attached to Flutter Engine");
        channel = new MethodChannel(binding.getBinaryMessenger(), channelName);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        // The plugin is no longer attached to a Flutter experience
        Log.d("StatusBarControl", "StatusBarControl: Detached from Flutter Engine");
        channel.setMethodCallHandler(null);
    }

    /**
     * Activity registration.
     */

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        // The plugin is now attached to an Activity.
        Log.d("StatusBarControl", "StatusBarControl: Attached to Activity");
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        // The attached Activity was destroyed to change configuration.
        // This call will be followed by onReattachedToActivityForConfigChanges().
        Log.d("StatusBarControl", "StatusBarControl: Detached from Activity for Config changes");
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        // The plugin is now attached to a new Activity after a configuration change.
        Log.d("StatusBarControl", "StatusBarControl: Reattached to Activity for Config changes");
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        // The plugin is no longer associated with an Activity. Clean up references.
        Log.d("StatusBarControl", "StatusBarControl: Detached from Activity");
        activity = null;
    }

    /**
     * Method handling.
     */

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "setColor":
                handleSetColor(call, result);
                break;
            case "setTranslucent":
                handleSetTranslucent(call, result);
                break;
            case "setHidden":
                handleSetHidden(call, result);
                break;
            case "setStyle":
                handleSetStyle(call, result);
                break;
            case "getHeight":
                handleGetHeight(call, result);
                break;
            case "setNetworkActivityIndicatorVisible":
                result.success(true);
                break;
            case "setNavigationBarColor":
                handleSetNavigationBarColor(call, result);
                break;
            case "setNavigationBarStyle":
                handleSetNavigationBarStyle(call, result);
                break;
            default:
                result.notImplemented();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleSetColor(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.", null);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int color = ((Number) call.argument("color")).intValue();
            final boolean animated = call.argument("animated");

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (animated) {
                int curColor = getBarColor(true);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), curColor, color);

                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setBarColor(true, (Integer) valueAnimator.getAnimatedValue());
                    }
                });
                colorAnimation.setDuration(300).setStartDelay(0);
                colorAnimation.start();
                result.success(true);
            } else {
                setBarColor(true, color);
                result.success(true);
            }

        } else {
            Log.e("StatusBarControl",
                    "StatusBarControl: Can not change status bar color in pre lollipop android versions.");
            result.error("StatusBarControl",
                    "StatusBarControl: Can not change status bar color in pre lollipop android versions.", null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleSetTranslucent(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.", null);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final boolean translucent = call.argument("translucent");
            View decorView = activity.getWindow().getDecorView();
            if (translucent) {
                ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> {
                    WindowInsetsCompat defaultInsets = ViewCompat.onApplyWindowInsets(v, insets);
                    return new WindowInsetsCompat.Builder(defaultInsets)
                            .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.NONE)
                            .build();
                });
            } else {
                ViewCompat.setOnApplyWindowInsetsListener(decorView, null);
            }
            ViewCompat.requestApplyInsets(decorView);
            result.success(true);
        } else {
            Log.e("StatusBarControl",
                    "StatusBarControl: Can not change status bar color in pre lollipop android versions.");
            result.error("StatusBarControl",
                    "StatusBarControl: Can not change status bar color in pre lollipop android versions.", null);
        }
    }

    private void handleSetHidden(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.", null);
            return;
        }

        final boolean hidden = call.argument("hidden");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat controller = getInsetsController();
            if (hidden) {
                controller.hide(WindowInsetsCompat.Type.statusBars());
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars());
            }
        } else {
            setFullscreenFlags(hidden);
        }
        result.success(true);
    }

    @SuppressWarnings("deprecation")
    private void setFullscreenFlags(boolean hidden) {
        if (hidden) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handleSetStyle(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.", null);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final String style = call.argument("style");
            getInsetsController().setAppearanceLightStatusBars(style.equals("dark-content"));
            result.success(true);
        } else {
            Log.e("StatusBarControl",
                    "StatusBarControl: Can not change status bar style in pre M android versions.");
            result.error("StatusBarControl",
                    "StatusBarControl: Can not change status bar style in pre M android versions.", null);
        }
    }

    private void handleGetHeight(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar height request, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar height request, current activity is null.", null);
            return;
        }

        int height = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = activity.getResources().getDimensionPixelSize(resourceId);
        }
        result.success((double) toDIPFromPixel(height));
    }

    @TargetApi((Build.VERSION_CODES.LOLLIPOP))
    private void handleSetNavigationBarColor(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.", null);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int color = ((Number) call.argument("color")).intValue();
            final boolean animated = call.argument("animated");

            if (animated) {
                int curColor = getBarColor(false);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), curColor, color);

                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setBarColor(false, (Integer) valueAnimator.getAnimatedValue());
                    }
                });
                colorAnimation.setDuration(300).setStartDelay(0);
                colorAnimation.start();
                result.success(true);
            } else {
                setBarColor(false, color);
                result.success(true);
            }
        } else {
            Log.e("StatusBarControl",
                    "StatusBarControl: Can not change status bar style in pre M android versions.");
            result.error("StatusBarControl",
                    "StatusBarControl: Can not change status bar style in pre M android versions.", null);
        }
    }

    @TargetApi((Build.VERSION_CODES.O))
    private void handleSetNavigationBarStyle(MethodCall call, Result result) {
        if (activity == null) {
            Log.e("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.");
            result.error("StatusBarControl",
                    "StatusBarControl: Ignored status bar change, current activity is null.", null);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String style = call.argument("style");
            getInsetsController().setAppearanceLightNavigationBars(!style.equals("dark"));
            result.success(true);
        } else {
            Log.e("StatusBarControl",
                    "StatusBarControl: Can not change status bar style in pre M android versions.");
            result.error("StatusBarControl",
                    "StatusBarControl: Can not change status bar style in pre M android versions.", null);
        }
    }

    /**
     * System bar helpers.
     */

    private WindowInsetsControllerCompat getInsetsController() {
        Window window = activity.getWindow();
        return WindowCompat.getInsetsController(window, window.getDecorView());
    }

    // Apps targeting Android 15 (API 35) or higher are drawn edge-to-edge and
    // Window#setStatusBarColor / Window#setNavigationBarColor have no effect.
    // In this case an overlay view is drawn behind the system bar instead.
    private boolean isEdgeToEdgeEnforced() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
                && activity.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.VANILLA_ICE_CREAM;
    }

    @SuppressWarnings("deprecation")
    private int getBarColor(boolean statusBar) {
        if (isEdgeToEdgeEnforced()) {
            Drawable background = getBarOverlay(statusBar).getBackground();
            return background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : Color.TRANSPARENT;
        }
        Window window = activity.getWindow();
        return statusBar ? window.getStatusBarColor() : window.getNavigationBarColor();
    }

    @SuppressWarnings("deprecation")
    private void setBarColor(boolean statusBar, int color) {
        if (isEdgeToEdgeEnforced()) {
            getBarOverlay(statusBar).setBackgroundColor(color);
            return;
        }
        Window window = activity.getWindow();
        if (statusBar) {
            window.setStatusBarColor(color);
        } else {
            window.setNavigationBarColor(color);
        }
    }

    private View getBarOverlay(final boolean statusBar) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        String tag = statusBar ? statusBarOverlayTag : navigationBarOverlayTag;
        View overlay = decorView.findViewWithTag(tag);
        if (overlay != null) {
            return overlay;
        }

        overlay = new View(activity);
        overlay.setTag(tag);
        decorView.addView(overlay, new FrameLayout.LayoutParams(0, 0));
        ViewCompat.setOnApplyWindowInsetsListener(overlay, (v, insets) -> {
            // Use the root insets, as dispatched insets might be modified by setTranslucent
            WindowInsetsCompat rootInsets = ViewCompat.getRootWindowInsets(v);
            layoutBarOverlay(v, statusBar, rootInsets != null ? rootInsets : insets);
            return insets;
        });
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(decorView);
        if (insets != null) {
            layoutBarOverlay(overlay, statusBar, insets);
        }
        return overlay;
    }

    private void layoutBarOverlay(View overlay, boolean statusBar, WindowInsetsCompat insets) {
        Insets bar = insets.getInsets(statusBar
                ? WindowInsetsCompat.Type.statusBars()
                : WindowInsetsCompat.Type.navigationBars());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) overlay.getLayoutParams();
        if (statusBar || bar.bottom > 0) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = statusBar ? bar.top : bar.bottom;
            params.gravity = statusBar ? Gravity.TOP : Gravity.BOTTOM;
        } else {
            // Navigation bar on the side (landscape with button navigation)
            params.width = Math.max(bar.left, bar.right);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.gravity = bar.left > 0 ? Gravity.START : Gravity.END;
        }
        overlay.setLayoutParams(params);
    }

    private int toDIPFromPixel(int pixel) {
        float scale = getDensity();
        return (int) ((pixel - 0.5f) / scale);
    }

    private int toPixelFromDPI(int dip) {
        float scale = getDensity();
        return (int) (dip * scale + 0.5f);
    }

    private float getDensity() {
        return activity.getResources().getDisplayMetrics().density;
    }

}
