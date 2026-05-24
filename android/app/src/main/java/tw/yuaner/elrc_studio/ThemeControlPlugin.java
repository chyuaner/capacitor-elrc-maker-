package tw.yuaner.elrc_studio;

import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ThemeControl")
public class ThemeControlPlugin extends Plugin {

    @PluginMethod
    public void setNavigationBarColor(PluginCall call) {
        String colorString = call.getString("color");
        if (colorString == null) {
            call.reject("Color is required");
            return;
        }
        try {
            final int color = Color.parseColor(colorString);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Window window = getActivity().getWindow();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setNavigationBarColor(color);
                        
                        // Set light/dark navigation bar buttons
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            int flags = window.getDecorView().getSystemUiVisibility();
                            boolean isDark = isColorDark(color);
                            if (isDark) {
                                // Dark background -> light buttons (clear light navigation bar flag)
                                flags &= ~android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                            } else {
                                // Light background -> dark buttons (set light navigation bar flag)
                                flags |= android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                            }
                            window.getDecorView().setSystemUiVisibility(flags);
                        }
                    }
                    call.resolve();
                }
            });
        } catch (Exception e) {
            call.reject("Failed to set navigation bar color: " + e.getMessage());
        }
    }

    @PluginMethod
    public void setFullscreen(PluginCall call) {
        final Boolean fullscreen = call.getBoolean("fullscreen");
        if (fullscreen == null) {
            call.reject("fullscreen parameter is required");
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Window window = getActivity().getWindow();
                    if (fullscreen) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            window.getInsetsController().hide(
                                android.view.WindowInsets.Type.statusBars() | 
                                android.view.WindowInsets.Type.navigationBars()
                            );
                            window.getInsetsController().setSystemBarsBehavior(
                                android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            );
                        } else {
                            int flags = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                            window.getDecorView().setSystemUiVisibility(flags);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            window.getInsetsController().show(
                                android.view.WindowInsets.Type.statusBars() | 
                                android.view.WindowInsets.Type.navigationBars()
                            );
                        } else {
                            int flags = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                            window.getDecorView().setSystemUiVisibility(flags);
                        }
                    }
                    call.resolve();
                } catch (Exception e) {
                    call.reject("Failed to set fullscreen: " + e.getMessage());
                }
            }
        });
    }

    @PluginMethod
    public void setScreenOrientation(PluginCall call) {
        final String orientation = call.getString("orientation");
        if (orientation == null) {
            call.reject("orientation parameter is required");
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ("portrait".equalsIgnoreCase(orientation)) {
                        getActivity().setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else if ("landscape".equalsIgnoreCase(orientation)) {
                        getActivity().setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        // Return to user/system default auto-rotation
                        getActivity().setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                    call.resolve();
                } catch (Exception e) {
                    call.reject("Failed to set screen orientation: " + e.getMessage());
                }
            }
        });
    }

    @PluginMethod
    public void getStatusBarHeight(PluginCall call) {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        float density = getContext().getResources().getDisplayMetrics().density;
        float dpHeight = result / density;
        com.getcapacitor.JSObject ret = new com.getcapacitor.JSObject();
        ret.put("height", dpHeight);
        call.resolve(ret);
    }

    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }
}
