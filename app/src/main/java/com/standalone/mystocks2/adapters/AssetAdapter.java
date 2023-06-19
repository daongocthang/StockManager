package com.standalone.mystocks2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.standalone.droid.adapters.AdapterFilterable;
import com.standalone.droid.utils.Humanize;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.api.SettingsUtil;
import com.standalone.mystocks2.constant.StringValues;
import com.standalone.mystocks2.fragments.TradeDialogFragment;
import com.standalone.mystocks2.helpers.AssetTableHandler;
import com.standalone.mystocks2.models.Stock;

import java.util.Collections;
import java.util.List;

public class AssetAdapter extends AdapterFilterable<Stock, AssetAdapter.ViewHolder> {

    private final AppCompatActivity activity;
    private final AssetTableHandler handler;

    private SharedPreferences settings;

    public AssetAdapter(AppCompatActivity activity, AssetTableHandler handler) {
        this.activity = activity;
        this.handler = handler;
        settings = activity.getSharedPreferences(StringValues.SETTINGS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(instantiateItemView(R.layout.item_asset, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Stock s = itemList.get(position);
        double netPrice = s.getPrice() * SettingsUtil.estimateTransactionCost(settings, Stock.OrderType.BUY);
        double stopLoss = netPrice * (1 - SettingsUtil.getStopLossThreshold(settings));

        holder.tvSymbol.setText(s.getSymbol());
        holder.tvPrice.setText(Humanize.doubleComma(netPrice));
        holder.tvShares.setText(Humanize.intComma(s.getShares()));
        holder.tvStopLoss.setText(Humanize.doubleComma(stopLoss));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public Context getContext() {
        return activity;
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void setItemList(List<Stock> itemList) {
        Collections.sort(itemList);
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    protected boolean applyFilterCriteria(CharSequence constraint, Stock stock) {
        String charString = constraint.toString();
        return stock.getSymbol().toLowerCase().contains(charString.toLowerCase());
    }

    public void removeItem(int pos) {
        Stock s = itemList.get(pos);
        handler.remove(s.getId());
        itemList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void updateItem(int pos) {
        Stock s = itemList.get(pos);
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", s);
        // Show a dialog fragment
        TradeDialogFragment fragment = new TradeDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), TradeDialogFragment.TAG);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol;
        TextView tvPrice;
        TextView tvShares;
        TextView tvStopLoss;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSymbol = itemView.findViewById(R.id.elAssetSymbol);
            tvPrice = itemView.findViewById(R.id.elAssetPrice);
            tvShares = itemView.findViewById(R.id.elAssetShares);
            tvStopLoss = itemView.findViewById(R.id.elAssetStopLoss);
        }
    }
}
