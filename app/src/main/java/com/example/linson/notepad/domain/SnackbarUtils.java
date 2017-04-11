package com.example.linson.notepad.domain;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by linson on 2017/4/11.
 */

public class SnackbarUtils {
    private static final int red = 0xfff44336;
    private static final int green = 0xff4caf50;
    private static final int blue = 0xff2195f3;
    private static final int orange = 0xffffc107;

    public static void showOK(View view, String content) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(green);
        snackbar.show();
    }

    public static void showFail(View view, String content) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(red);
        snackbar.show();
    }
}
