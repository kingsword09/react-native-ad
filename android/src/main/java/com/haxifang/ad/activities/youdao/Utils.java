package com.example.sdkdemo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;

class Utils {
    static final String LOGTAG = "Sample App";

    private Utils() {
    }

    static void validateAdUnitId(String adUnitId) throws IllegalArgumentException {
        if (adUnitId == null) {
            throw new IllegalArgumentException("Invalid Ad Unit ID: null ad unit.");
        } else if (adUnitId.length() == 0) {
            throw new IllegalArgumentException("Invalid Ad Unit Id: empty ad unit.");
        } else if (adUnitId.length() > 256) {
            throw new IllegalArgumentException("Invalid Ad Unit Id: length too long.");
        } else if (!isAlphaNumeric(adUnitId)) {
            throw new IllegalArgumentException("Invalid Ad Unit Id: contains non-alphanumeric characters.");
        }
    }

    static void hideSoftKeyboard(final View view) {
        final InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static boolean isAlphaNumeric(final String input) {
        return input.matches("^[a-zA-Z0-9-_]*$");
    }

    static void logToast(Context context, String message) {
        Log.d(LOGTAG, message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(final Context context) {
        if (context == null) {
            return false;
        }

        final int internetPermission = context
                .checkCallingOrSelfPermission(INTERNET);
        if (internetPermission == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        /**
         * This is only checking if we have permission to access the network
         * state It's possible to not have permission to check network state but
         * still be able to access the network itself.
         */
        final int networkAccessPermission = context
                .checkCallingOrSelfPermission(ACCESS_NETWORK_STATE);
        if (networkAccessPermission == PackageManager.PERMISSION_DENIED) {
            return true;
        }

        // Otherwise, perform the connectivity check.
        try {
            final ConnectivityManager connnectionManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connnectionManager
                    .getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
