package com.example.linson.notepad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by linson on 2017/4/11.
 */

public class DialogUtils {
    public static void show(Context context, String content, String btnOk, DialogInterface.OnClickListener okOnClickListener, String btnFail, DialogInterface.OnClickListener failOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(content);
        builder.setPositiveButton(btnOk, okOnClickListener);
        builder.setNegativeButton(btnFail, failOnClickListener);
        builder.show();
    }
}
