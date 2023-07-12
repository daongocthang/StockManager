package com.standalone.droid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.standalone.droid.R;

public class AlertUtils {
    public static void showConfirmDialog(Context context, String message, final OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
        builder.setView(view);

        TextView textView = view.findViewById(R.id.tvAlertMessage);
        textView.setText(message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositive(dialogInterface, i);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegative(dialogInterface, i);
            }
        }).show();
    }

    public interface OnClickListener {
        void onPositive(DialogInterface dialogInterface, int i);

        void onNegative(DialogInterface dialogInterface, int i);
    }
}
