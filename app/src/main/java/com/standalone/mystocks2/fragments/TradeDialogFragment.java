package com.standalone.mystocks2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.droid.utils.Humanize;
import com.standalone.droid.utils.ViewUtils;
import com.standalone.mystocks.constant.ErrorMessages;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.adapters.CompanyAdapter;
import com.standalone.mystocks2.api.SettingsUtil;
import com.standalone.mystocks2.constant.StringValues;
import com.standalone.mystocks2.helpers.AssetTableHandler;
import com.standalone.mystocks2.helpers.CompanyTableHandler;
import com.standalone.mystocks2.helpers.HistoryTableHandler;
import com.standalone.mystocks2.interfaces.DialogCloseListener;
import com.standalone.mystocks2.models.Company;
import com.standalone.mystocks2.models.Stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TradeDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = TradeDialogFragment.class.getSimpleName();
    private AutoCompleteTextView edSymbol;
    private EditText edShares;
    private EditText edPrice;

    private TextView tvDate;

    private Stock referenceStock;
    private AssetTableHandler assetTableHandler;
    private HistoryTableHandler historyTableHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trade, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize variables
        edSymbol = view.findViewById(R.id.edSymbol);
        edPrice = view.findViewById(R.id.edPrice);
        edShares = view.findViewById(R.id.edShares);
        tvDate = view.findViewById(R.id.tvDate);

        SQLiteDatabase db = DatabaseManager.getDatabase(getContext());

        Button btSubmit = view.findViewById(R.id.btSubmit);
        ImageButton btDatePicker = view.findViewById(R.id.imDatePicker);

        final RecyclerView rvSuggestion = view.findViewById(R.id.rvSuggestion);
        ViewUtils.setNumberSuggestion(this.getContext(), edShares, rvSuggestion, 5, true);

        ViewUtils.addCancelButton(view, edSymbol, R.id.imSymbol);
        ViewUtils.addCancelButton(view, edPrice, R.id.imPrice);
        ViewUtils.addCancelButton(view, edShares, R.id.imShares);

        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.showDatePicker(view, tvDate);
            }
        });

        // Fill fields if exists
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(today);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("stock")) {
            Stock s = (Stock) bundle.get("stock");
            assert s != null;
            btSubmit.setText(Stock.OrderType.SELL.toString());
            btSubmit.setBackgroundResource(R.color.danger_dark);
            isUpdate = true;
            referenceStock = s;
            disableEditText(edSymbol);
            SharedPreferences settings = view.getContext().getSharedPreferences(StringValues.SETTINGS, Context.MODE_PRIVATE);
            double finalPrice = s.getPrice() * SettingsUtil.estimateTransactionCost(settings, Stock.OrderType.BUY);
            edSymbol.setText(s.getSymbol());
            edPrice.setText(Humanize.doubleComma(finalPrice));
            edShares.setText(String.valueOf(s.getShares()));
        } else {
            btSubmit.setText(Stock.OrderType.BUY.toString());

            // Try add Adapter to AutoCompleteTextView
            List<Company> dataStockList = new CompanyTableHandler(db).fetchAll();
            if (dataStockList != null) {
                edSymbol.setAdapter(new CompanyAdapter(requireActivity(), R.layout.item_suggestion, dataStockList));
            }
            edSymbol.requestFocus();
        }

        assetTableHandler = new AssetTableHandler(db);
        historyTableHandler = new HistoryTableHandler(db);

        final boolean finalIsUpdate = isUpdate;
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit(finalIsUpdate);
            }
        });
    }

    private void onSubmit(boolean isUpdate) {
        // Assert the fields are not empty
        if (edSymbol.getText().toString().equals("")) {
            edSymbol.setError(ErrorMessages.REQUIRED);
            return;
        }
        if (edPrice.getText().toString().equals("")) {
            edPrice.setError(ErrorMessages.REQUIRED);
            return;
        }
        if (edShares.getText().toString().equals("")) {
            edShares.setError(ErrorMessages.REQUIRED);
            return;
        }

        String inputSymbol = edSymbol.getText().toString().toUpperCase();

        double inputPrice;
        try {
            inputPrice = Double.parseDouble(edPrice.getText().toString());
        } catch (NumberFormatException e) {
            inputPrice = 0.0;
        }

        int inputShares;
        try {
            inputShares = Integer.parseInt(edShares.getText().toString().replace(",", ""));
        } catch (NumberFormatException e) {
            inputShares = 0;
        }

        if (isUpdate) {
            // Check if out of range
            Stock s = referenceStock;
            int remainingShares = s.getShares() - inputShares;
            double matchedPrice = s.getPrice();

            if (remainingShares < 0) {
                edShares.setError(ErrorMessages.INVALID);
                return;
            }

            s.setShares(inputShares);
            s.setReference(matchedPrice);
            s.setPrice(inputPrice);
            s.setOrder(Stock.OrderType.SELL);
            s.setDate(tvDate.getText().toString());

            historyTableHandler.insert(s);
            if (remainingShares == 0) {
                assetTableHandler.remove(s.getId());
            } else {
                s.setShares(remainingShares);
                s.setPrice(matchedPrice);
                assetTableHandler.update(s);
            }
        } else {
            Stock s = new Stock();
            s.setSymbol(inputSymbol);
            s.setPrice(inputPrice);
            s.setShares(inputShares);
            s.setOrder(Stock.OrderType.BUY);
            s.setReference(0);
            s.setDate(tvDate.getText().toString());

            historyTableHandler.insert(s);
            assetTableHandler.insert(s);
        }

        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();

        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        Log.e(TAG, "Dismiss when pressing outside");
        dismiss();
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }
}
