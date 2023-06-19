package com.standalone.mystocks2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.standalone.mystocks2.models.Stock;

import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends AdapterFilterable<Stock, HistoryAdapter.ViewHolder> {

    private final AppCompatActivity activity;
    final SharedPreferences settings;

    public HistoryAdapter(AppCompatActivity activity) {
        this.activity = activity;
        settings = activity.getSharedPreferences(StringValues.SETTINGS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryAdapter.ViewHolder(instantiateItemView(R.layout.item_history, parent));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Stock s = itemList.get(position);
        holder.tvSymbol.setText(s.getSymbol());
        holder.tvShares.setText(Humanize.intComma(s.getShares()));
        holder.tvDate.setText(s.getDate());
        Stock.OrderType orderType = s.getOrder();
        holder.tvPrice.setText(Humanize.doubleComma(s.getPrice() * SettingsUtil.estimateTransactionCost(settings, orderType)));

        holder.tvOrder.setText(orderType.toString());
        int colorPrimary = activity.getResources().getColor(R.color.primary);
        int colorDanger = activity.getResources().getColor(R.color.danger_dark);
        if (orderType.equals(Stock.OrderType.BUY)) {
            holder.tvOrder.setTextColor(colorPrimary);
            holder.tvProfit.setText("");
        } else {
            double netProfit = s.getNetProfit(settings);
            holder.tvOrder.setTextColor(colorDanger);
            holder.tvProfit.setText(Humanize.doubleComma(netProfit));
            if (netProfit > 0) {
                holder.tvProfit.setTextColor(colorPrimary);
            } else {
                holder.tvProfit.setTextColor(colorDanger);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Context getContext() {
        return activity;
    }

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol;
        TextView tvPrice;
        TextView tvShares;
        TextView tvOrder;
        TextView tvProfit;
        TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSymbol = itemView.findViewById(R.id.elHistorySymbol);
            tvPrice = itemView.findViewById(R.id.elHistoryPrice);
            tvShares = itemView.findViewById(R.id.elHistoryShares);
            tvOrder = itemView.findViewById(R.id.elHistoryOrder);
            tvProfit = itemView.findViewById(R.id.elHistoryProfit);
            tvDate = itemView.findViewById(R.id.elHistoryDate);
        }
    }
}
