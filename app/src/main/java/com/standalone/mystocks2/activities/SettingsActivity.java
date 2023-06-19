package com.standalone.mystocks2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.constant.StringValues;

public class SettingsActivity extends AppCompatActivity {

    final String TRANSACTION_COST = "Transaction Cost: ";
    final String VALUE_ADDED_TAX = "Value Added Tax: ";
    final String STOP_LOSS = "Stop Loss Threshold: ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = getSharedPreferences(StringValues.SETTINGS, MODE_PRIVATE);
        SeekBar sbTransactionFee = findViewById(R.id.sb_transaction_cost);
        SeekBar sbValueAddedTax = findViewById(R.id.sb_tax);
        SeekBar sbStopLossRate = findViewById(R.id.sb_stop_loss);

        final TextView tvTransactionFee = findViewById(R.id.tv_transaction_cost);
        final TextView tvValueAddedTax = findViewById(R.id.tv_tax);
        final TextView tvStopLossRate = findViewById(R.id.tv_stop_loss);

        final Button btBackup = findViewById(R.id.btBackup);
        final Button btRestore = findViewById(R.id.btRestore);

        Button btSave = findViewById(R.id.btSave);

        int[] values = new int[]{
                settings.getInt(StringValues.TRANSACTION_COST, 0),
                settings.getInt(StringValues.VALUE_ADDED_TAX, 0),
                settings.getInt(StringValues.STOP_THRESHOLD, 0)
        };


        sbTransactionFee.setProgress(values[0]);
        tvTransactionFee.setText(String.valueOf(TRANSACTION_COST + ((double) values[0] / 100) + "%"));
        sbTransactionFee.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTransactionFee.setText(String.valueOf(TRANSACTION_COST + ((double) progress / 100) + "%"));
                values[0] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbValueAddedTax.setProgress(values[1]);
        tvValueAddedTax.setText(String.valueOf(VALUE_ADDED_TAX + ((double) values[1] / 100) + "%"));
        sbValueAddedTax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvValueAddedTax.setText(String.valueOf(VALUE_ADDED_TAX + ((double) progress / 100) + "%"));
                values[1] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbStopLossRate.setProgress(values[2]);
        tvStopLossRate.setText(String.valueOf(STOP_LOSS + values[2] + "%"));
        sbStopLossRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvStopLossRate.setText(String.valueOf(STOP_LOSS + progress + "%"));
                values[2] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave(values);
            }
        });

        btRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRestore();
            }
        });
        btBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackup();
            }
        });
    }


    private void handleSave(int[] settings) {
        SharedPreferences sharedPrefer = getSharedPreferences(StringValues.SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefer.edit();
        editor.putInt(StringValues.TRANSACTION_COST, settings[0]);
        editor.putInt(StringValues.VALUE_ADDED_TAX, settings[1]);
        editor.putInt(StringValues.STOP_THRESHOLD, settings[2]);
        editor.apply();

        Toast.makeText(this, "Settings saved completely.", Toast.LENGTH_SHORT).show();
    }

    private void handleBackup() {
        boolean res = DatabaseManager.exportDB(this);
        if (res) {
            Toast.makeText(this, "Successfully back-up database.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Back-up database failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRestore() {
        boolean res = DatabaseManager.importDB(this);
        if (res) {
            Toast.makeText(this, "Successfully restore database.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Restore database failed.", Toast.LENGTH_SHORT).show();
        }
    }


}
