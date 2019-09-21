package mohaqeqi.mahdi.mymoney.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;

import mohaqeqi.mahdi.mymoney.Activity.MainActivity;
import mohaqeqi.mahdi.mymoney.R;
import mohaqeqi.mahdi.mymoney.Util.JalaliCalendar;

import static mohaqeqi.mahdi.mymoney.App.AppInitializer.db;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.editor;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.month;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.preferences;

public class AddTransactionFragment extends Fragment {

    EditText edtTxtTitle;
    EditText edtTxtAmount;
    NumberPicker numPickerDay;
    NumberPicker numPickerMonth;
    NumberPicker numPickerYear;
    EditText edtTxtDescription;
    Button btnAddTransaction;
    TextView txtCurrency;
    SwitchCompat switchTransType;
    TextView txtTransType;
    boolean editable;
    int transactionId;
    long oldAmount;
    long newAmount;
    String oldType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editable = getArguments().getBoolean("editable");

        txtCurrency.setText(preferences.getString("currency", getContext().getResources().getString(R.string.toman)));

        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtTxtTitle.getText().toString().equals("")) {
                    if (!edtTxtAmount.getText().toString().equals("")) {
                        if (!edtTxtAmount.getText().toString().equals("0")) {
                            if (editable)
                                editTransaction();
                            else
                                saveTransaction();
                        } else
                            Toast.makeText(getContext(), "قیمت نمی تواند صفر باشد", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "فیلد قیمت نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "فیلد عنوان نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
            }
        });
        setNumberPickerDate();
        setSwitchTransType();
        if (editable)
            setEditableData();
    }

    private void initView(View view) {

        edtTxtTitle = view.findViewById(R.id.edtTxtTitle);
        edtTxtAmount = view.findViewById(R.id.edtTxtAmount);
        numPickerDay = view.findViewById(R.id.numPickerDay);
        numPickerMonth = view.findViewById(R.id.numPickerMonth);
        numPickerYear = view.findViewById(R.id.numPickerYear);
        edtTxtDescription = view.findViewById(R.id.edtTxtDescription);
        btnAddTransaction = view.findViewById(R.id.btnAddTransaction);
        txtCurrency = view.findViewById(R.id.txtCurrency);
        switchTransType = view.findViewById(R.id.switchTransType);
        txtTransType = view.findViewById(R.id.txtTransType);
    }

    private void setSwitchTransType() {

        switchTransType.setThumbTintList(new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}},
                new int[]{getContext().getResources().getColor(R.color.colorIncomeDark), getContext().getResources().getColor(R.color.colorPaymentDark)}));
        switchTransType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {

                    txtTransType.setText(getContext().getResources().getString(R.string.income));
                    txtTransType.setTextColor(getContext().getResources().getColor(R.color.colorIncomeDark));
                    btnAddTransaction.setBackground(getContext().getDrawable(R.drawable.bg_btn_add_income));
                } else {
                    txtTransType.setText(getContext().getResources().getString(R.string.payment));
                    txtTransType.setTextColor(getContext().getResources().getColor(R.color.colorPaymentDark));
                    btnAddTransaction.setBackground(getContext().getDrawable(R.drawable.bg_btn_add_payment));
                }
            }
        });
    }

    private void setNumberPickerDate() {

        numPickerDay.setMinValue(1);
        numPickerDay.setMaxValue(31);
        numPickerMonth.setMinValue(1);
        numPickerMonth.setMaxValue(12);
        numPickerMonth.setDisplayedValues(month);
        numPickerYear.setMinValue(1390);
        numPickerYear.setMaxValue(1500);
        changeDividerColor(numPickerDay, Color.parseColor("#00ffffff"));
        changeDividerColor(numPickerMonth, Color.parseColor("#00ffffff"));
        changeDividerColor(numPickerYear, Color.parseColor("#00ffffff"));

        JalaliCalendar.gDate date = new JalaliCalendar.gDate(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        JalaliCalendar.gDate currentDate = JalaliCalendar.MiladiToJalali(date);
        numPickerDay.setValue(currentDate.getDay() + 1);
        numPickerMonth.setValue(currentDate.getMonth());
        numPickerYear.setValue(currentDate.getYear());
    }

    private void setEditableData() {

        transactionId = getArguments().getInt("id");
        oldAmount = getArguments().getLong("amount");
        oldType = getArguments().getString("type");
        edtTxtTitle.setText(getArguments().getString("title"));
        edtTxtAmount.setText(String.valueOf(oldAmount));
        if (oldType.equals("I")) {

            switchTransType.setChecked(false);
            txtTransType.setText(getResources().getString(R.string.income));
            btnAddTransaction.setBackground(getResources().getDrawable(R.drawable.bg_btn_add_income));
        } else {
            switchTransType.setChecked(true);
            txtTransType.setText(getResources().getString(R.string.payment));
            btnAddTransaction.setBackground(getResources().getDrawable(R.drawable.bg_btn_add_payment));
        }
        numPickerDay.setValue(getArguments().getInt("day"));
        numPickerMonth.setValue(getArguments().getInt("month"));
        numPickerYear.setValue(getArguments().getInt("year"));
        edtTxtDescription.setText(getArguments().getString("description"));
        btnAddTransaction.setText(getResources().getString(R.string.edit_transaction));
    }

    private void changeDividerColor(NumberPicker picker, int color) {

        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void saveTransaction() {

        String sDate = numPickerYear.getValue() + "" + numPickerMonth.getValue();
        if (!switchTransType.isChecked()) {
            editor.putLong("balance" + sDate, preferences.getLong("balance" + sDate, 0) + Long.valueOf(edtTxtAmount.getText().toString())).apply();
            MainActivity.txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + sDate, 0)));
            Toast.makeText(getContext(), "تراکنش با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
            db.addTransaction(edtTxtTitle.getText().toString(),
                    Long.valueOf(edtTxtAmount.getText().toString()),
                    numPickerDay.getValue(),
                    numPickerMonth.getValue(),
                    numPickerYear.getValue(),
                    edtTxtDescription.getText().toString(),
                    "I");
            getContext().startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            if (preferences.getLong("balance" + sDate, 0) - Long.valueOf(edtTxtAmount.getText().toString()) < 0)
                Toast.makeText(getContext(), "مبلغ کسری شما بیشتر از مقدار موجودی است!", Toast.LENGTH_SHORT).show();
            else {
                editor.putLong("balance" + sDate, preferences.getLong("balance" + sDate, 0) - Long.valueOf(edtTxtAmount.getText().toString())).apply();
                MainActivity.txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + sDate, 0)));
                Toast.makeText(getContext(), "تراکنش با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                db.addTransaction(edtTxtTitle.getText().toString(),
                        Long.valueOf(edtTxtAmount.getText().toString()),
                        numPickerDay.getValue(),
                        numPickerMonth.getValue(),
                        numPickerYear.getValue(),
                        edtTxtDescription.getText().toString(),
                        "P");
                getContext().startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        }
    }

    private void editTransaction() {

        String sDate = numPickerYear.getValue() + "" + numPickerMonth.getValue();
        if (oldType.equals("I"))
            newAmount = preferences.getLong("balance" + sDate, 0) - oldAmount;
        else
            newAmount = preferences.getLong("balance" + sDate, 0) + oldAmount;

        if (!switchTransType.isChecked()) {
            editor.putLong("balance" + sDate, newAmount + Long.valueOf(edtTxtAmount.getText().toString())).apply();
            MainActivity.txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + sDate, 0)));
            Toast.makeText(getContext(), "تراکنش با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
            db.editTransaction(transactionId,
                    edtTxtTitle.getText().toString(),
                    Long.valueOf(edtTxtAmount.getText().toString()),
                    numPickerDay.getValue(),
                    numPickerMonth.getValue(),
                    numPickerYear.getValue(),
                    edtTxtDescription.getText().toString(),
                    "I");
            getContext().startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            if (newAmount - Long.valueOf(edtTxtAmount.getText().toString()) < 0)
                Toast.makeText(getContext(), "مبلغ کسری شما بیشتر از مقدار موجودی است!", Toast.LENGTH_SHORT).show();
            else {
                editor.putLong("balance" + sDate, newAmount - Long.valueOf(edtTxtAmount.getText().toString())).apply();
                MainActivity.txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + sDate, 0)));
                Toast.makeText(getContext(), "تراکنش با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
                db.editTransaction(transactionId,
                        edtTxtTitle.getText().toString(),
                        Long.valueOf(edtTxtAmount.getText().toString()),
                        numPickerDay.getValue(),
                        numPickerMonth.getValue(),
                        numPickerYear.getValue(),
                        edtTxtDescription.getText().toString(),
                        "P");
                getContext().startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        }
    }
}
