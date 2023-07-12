package com.standalone.mystocks2.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.droid.utils.AlertUtils;
import com.standalone.droid.utils.StorageUtils;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.constant.StringValues;
import com.standalone.mystocks2.helpers.HistoryTableHandler;

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

                AlertUtils.showConfirmDialog(SettingsActivity.this, "Do you want to restore?", new AlertUtils.OnClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface, int i) {
                        handleRestore();
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface, int i) {

                    }
                });
            }
        });
        btBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showConfirmDialog(SettingsActivity.this, "Do you want to backup?", new AlertUtils.OnClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface, int i) {
                        handleBackup();
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface, int i) {
                    }
                });


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
        if (!StorageUtils.hasSDCard()) {
            Toast.makeText(this, "SD card is not detected.", Toast.LENGTH_SHORT).show();
            return;
        }

        HistoryTableHandler tableHandler = new HistoryTableHandler(DatabaseManager.getDatabase(this));
        boolean success = tableHandler.getCount() > 0;
        if (success) {
            success = DatabaseManager.exportDB(this);
        }

        if (success) {
            Toast.makeText(this, "Successfully backup database.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Back-up database failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRestore() {
        if (!StorageUtils.hasSDCard()) {
            Toast.makeText(this, "SD card is not detected.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean res = DatabaseManager.importDB(this);
        if (res) {
            Toast.makeText(this, "Successfully restore database.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Restore database failed.", Toast.LENGTH_SHORT).show();
        }
    }


}
