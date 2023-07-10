package com.standalone.droid.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.adapters.SuggestionsAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ViewUtils {
    public static void setNumberSuggestion(Context context, EditText editText, RecyclerView recyclerView, int maxLength, boolean reverse) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        SuggestionsAdapter adapter = new SuggestionsAdapter(new SuggestionsAdapter.ItemClickListener() {
            @Override
            public void onClick(String text) {
                editText.setText(text);
                editText.setSelection(text.length());
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        editText.addTextChangedListener(new TextWatcher() {
            final List<String> itemList = new ArrayList<>();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.isEnabled()) return;

                editText.removeTextChangedListener(this);

                if (s.length() > 0) {

                    itemList.clear();
                    String sanityText = editText.getText().toString().replaceAll(",", "");

                    if (sanityText.length() > maxLength) {
                        sanityText = sanityText.substring(0, sanityText.length() - 1);
                    } else {
                        int value = Integer.parseInt(sanityText);

                        int count = 0;
                        while (count < maxLength) {
                            count++;
                            int nextValue = (int) (value * Math.pow(10, count));
                            String valueAsString = String.valueOf(nextValue);
                            if (valueAsString.length() > maxLength) break;
                            itemList.add(String.format(Locale.US, "%,d", nextValue));
                        }

                        if (reverse) Collections.reverse(itemList);
                        adapter.setItemList(itemList);
                    }

                    String newText = String.format(Locale.US, "%,d", Integer.parseInt(sanityText));
                    editText.setText(newText);

                    editText.setSelection(editText.getText().toString().length());
                } else {
                    adapter.clear();
                }

                editText.addTextChangedListener(this);
            }
        });
    }

    public static void addCancelButton(View view, EditText edt, int res) {
        ImageButton btn = view.findViewById(res);
        btn.setVisibility(ImageButton.GONE);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edt.isEnabled()) return;

                btn.setVisibility(s.length() > 0 ? ImageButton.VISIBLE : ImageButton.GONE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.getText().clear();
                edt.requestFocus();

                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public static void showDatePicker(View view, TextView tv) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String[] strDate = new String[3];
                strDate[0] = String.format(Locale.US, "%02d", year);
                strDate[1] = String.format(Locale.US, "%02d", month + 1);
                ;
                strDate[2] = String.format(Locale.US, "%02d", day);
                ;

                tv.setText(String.join("-", strDate));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private static boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            double d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
