package com.standalone.droid.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class ViewUtils {
    public static void setHumanizedDecimalType(EditText edt) {
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt.removeTextChangedListener(this);
                String desired = edt.getText().toString().replaceAll(",", "");

                // preventing Number Format Exception
                if (desired.length() > 15) {
                    desired = desired.substring(0, desired.length() - 1);
                }
                if (isNumeric(desired)) {
                    Long longValue = Long.parseLong(desired);
                    String fmtStr = String.format(Locale.US, "%,d", longValue);
                    edt.setText(fmtStr);
                    edt.setSelection(edt.getText().length());
                }
                edt.addTextChangedListener(this);
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
