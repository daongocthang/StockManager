package com.standalone.mystocks2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.droid.utils.Humanize;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.api.ApiStock;
import com.standalone.mystocks2.constant.StringValues;
import com.standalone.mystocks2.helpers.CompanyTableHandler;
import com.standalone.mystocks2.helpers.HistoryTableHandler;
import com.standalone.mystocks2.models.Company;
import com.standalone.mystocks2.models.Stock;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int PERMISSION_REQUEST_CODE = 101;

    GridLayout mainGrid;
    TextView tvWinRate;
    TextView tvNetProfit;
    TextView tvGain;
    TextView tvLoss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requirePermission();

        mainGrid = (GridLayout) findViewById(R.id.main_grid);

        tvWinRate = findViewById(R.id.tv_win_rate);
        tvNetProfit = findViewById(R.id.tv_net_profit);
        tvGain = findViewById(R.id.tv_gain);
        tvLoss = findViewById(R.id.tv_loss);

        setToggleEvent();
        loadDataCompany();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderView();
    }

    private void loadDataCompany() {
        final CompanyTableHandler tableHandler = new CompanyTableHandler(DatabaseManager.getDatabase(this));

        if (tableHandler.getCount() > 0) return;

        new ApiStock(this).requestAllStocks(new ApiStock.OnResponseListener<List<Company>>() {
            @Override
            public void onResponse(List<Company> dataStocks) {
                for (Company d : dataStocks) {
                    tableHandler.insert(d);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    private void renderView() {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            if (cardView.getCardBackgroundColor().getDefaultColor() != -1)
                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        Double[] values = evaluateInvestingValues();
        if (values == null) return;

        tvWinRate.setText(Humanize.doubleComma(values[0]));
        tvNetProfit.setText(Humanize.doubleComma(values[1]));
        tvGain.setText(Humanize.doubleComma(values[2]));
        tvLoss.setText(Humanize.doubleComma(values[3]));
    }

    private void setToggleEvent() {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalId = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardView.setCardBackgroundColor(Color.parseColor("#669900"));

                    switch (finalId) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, AssetActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            break;
                    }
                }
            });
        }
    }

    private Double[] evaluateInvestingValues() {
        int win = 0;
        int length = 0;
        double totalProfit = 0;
        double totalLoss = 0;

        SharedPreferences settings = getSharedPreferences(StringValues.SETTINGS, MODE_PRIVATE);

        HistoryTableHandler tableHandler = new HistoryTableHandler(DatabaseManager.getDatabase(this));

        for (Stock s : tableHandler.fetchAll()) {
            if (s.getOrder().equals(Stock.OrderType.BUY)) continue;
            length++;
            double netProfit = s.getNetProfit(settings);
            if (netProfit > 0) {
                totalProfit += netProfit;
                win++;
            } else {
                totalLoss += netProfit;
            }
        }

        if (length == 0) {
            return null;
        }

        return new Double[]{(double) win / length, totalProfit + totalLoss, totalProfit, Math.abs(totalLoss)};
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requirePermission() {
        if (checkPermission()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("packages:%s", getPackageName())));
                this.startActivityIfNeeded(intent, PERMISSION_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                this.startActivityIfNeeded(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}