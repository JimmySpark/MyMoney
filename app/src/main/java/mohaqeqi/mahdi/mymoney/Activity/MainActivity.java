package mohaqeqi.mahdi.mymoney.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mohaqeqi.mahdi.mymoney.Adapter.RecyclerTransactionAdapter;
import mohaqeqi.mahdi.mymoney.Fragment.AddTransactionFragment;
import mohaqeqi.mahdi.mymoney.Model.Transaction;
import mohaqeqi.mahdi.mymoney.R;
import mohaqeqi.mahdi.mymoney.Util.JalaliCalendar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static mohaqeqi.mahdi.mymoney.App.AppInitializer.db;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.editor;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.month;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.preferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Context context = this;
    public static FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView menu;
    ImageView arrange;
    Button btnPayments;
    Button btnAll;
    Button btnIncomes;
    RecyclerView recyclerTransactions;
    FloatingActionButton btnAdd;
    public static TextView txtBalance;
    public static RelativeLayout layParent;
    public static FrameLayout frameLayout;
    /*NavigationView navigationView;
    DrawerLayout drawerLay;*/
    AddTransactionFragment addTransactionFragment;
    List<Transaction> allList;
    List<Transaction> incomeList;
    List<Transaction> paymentList;
    ProgressBar progressCounter;
    TextView txtCounter;
    CardView laySnackBar;
    TextView txtCurrency;
    Button btnUndo;
    TextView btnMonth;
    TextView btnYear;
    public static String selectedDate;
    public static TextView txtNoTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        fragmentManager = getSupportFragmentManager();
        setAllTransactions();

        JalaliCalendar.gDate date = new JalaliCalendar.gDate(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        JalaliCalendar.gDate currentDate = JalaliCalendar.MiladiToJalali(date);
        btnMonth.setText(month[preferences.getInt("month", currentDate.getMonth()) - 1]);
        btnYear.setText(String.valueOf(preferences.getInt("year", currentDate.getYear())));
        selectedDate = preferences.getInt("year", currentDate.getYear()) + "" + preferences.getInt("month", currentDate.getMonth());
        txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + selectedDate, 0)));
        txtCurrency.setText(preferences.getString("currency", getResources().getString(R.string.toman)));
        btnAdd.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnIncomes.setOnClickListener(this);
        btnPayments.setOnClickListener(this);
        menu.setOnClickListener(this);
        arrange.setOnClickListener(this);
//        navigationView.setNavigationItemSelectedListener(this);
        btnMonth.setOnClickListener(this);
        btnYear.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnAdd:
                frameLayout.setVisibility(View.VISIBLE);
                layParent.setVisibility(View.GONE);
                addTransactionFragment = new AddTransactionFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("editable", false);
                addTransactionFragment.setArguments(bundle);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.frameLayout, addTransactionFragment, "addTransactionFrag");
                fragmentTransaction.addToBackStack("addTransactionFrag");
                fragmentTransaction.commit();
                break;
            case R.id.btnAll:
                changeTab(btnAll.getId());
                break;
            case R.id.btnIncomes:
                changeTab(btnIncomes.getId());
                break;
            case R.id.btnPayments:
                changeTab(btnPayments.getId());
                break;
            case R.id.menu:
//                drawerLay.openDrawer(Gravity.START);
                PopupMenu popupMenu = new PopupMenu(context, menu);
                popupMenu.getMenuInflater().inflate(R.menu.nav_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.currency:
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                                View v = LayoutInflater.from(context).inflate(R.layout.dialog_currency, null);
                                dialogBuilder.setView(v);

                                TextView toman = v.findViewById(R.id.txtToman);
                                TextView rial = v.findViewById(R.id.txtRial);

                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                                alertDialog.show();

                                toman.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        editor.putString("currency", getResources().getString(R.string.toman)).apply();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });

                                rial.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        editor.putString("currency", getResources().getString(R.string.rial)).apply();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });

                                Display d = getWindowManager().getDefaultDisplay();
                                Point s = new Point();
                                d.getSize(s);
                                int w = s.x;
                                w = (int) ((w) * (2.5 / 5));
                                alertDialog.getWindow().setLayout(w, LinearLayout.LayoutParams.WRAP_CONTENT);
                                break;
                            case R.id.erase_all:
                                if (db.getAllTransactionsCount() > 0) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_erase, null);
                                    builder.setView(view);

                                    TextView eraseMonth = view.findViewById(R.id.txtEraseMonth);
                                    TextView eraseAll = view.findViewById(R.id.txtEraseAll);

                                    final AlertDialog dialog = builder.create();
                                    dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                                    dialog.show();

                                    eraseMonth.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                                            builder.setMessage("همه تراکنش ها حذف شوند؟");
                                            builder.setPositiveButton("حذف همه", null);
                                            builder.setNegativeButton("رد کردن", null);

                                            final AlertDialog dialog = builder.create();
                                            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                                            dialog.show();

                                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    setEraseMonthSnackBar();
                                                }
                                            });

                                            Display display = getWindowManager().getDefaultDisplay();
                                            Point size = new Point();
                                            display.getSize(size);
                                            int width = size.x;
                                            width = (int) ((width) * (4.5 / 5));
                                            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        }
                                    });

                                    eraseAll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                                            builder.setMessage("همه تراکنش ها حذف شوند؟");
                                            builder.setPositiveButton("حذف همه", null);
                                            builder.setNegativeButton("رد کردن", null);

                                            final AlertDialog dialog = builder.create();
                                            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                                            dialog.show();

                                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    setEraseAllSnackBar();
                                                }
                                            });

                                            Display display = getWindowManager().getDefaultDisplay();
                                            Point size = new Point();
                                            display.getSize(size);
                                            int width = size.x;
                                            width = (int) ((width) * (4.5 / 5));
                                            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        }
                                    });

                                    Display display = getWindowManager().getDefaultDisplay();
                                    Point size = new Point();
                                    display.getSize(size);
                                    int width = size.x;
                                    width = (int) ((width) * (3.5 / 5));
                                    dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                } else
                                    Toast.makeText(context, "تراکنشی برای حذف موجود نیست", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                break;
            case R.id.arrange:
                showArrangeDialog();
                break;
            case R.id.btnMonth:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_date, null);
                builder.setView(view);
                builder.setMessage("نمایش بر اساس ماه:");
                builder.setPositiveButton("انتخاب", null);
                builder.setNegativeButton("رد کردن", null);

                JalaliCalendar.gDate date = new JalaliCalendar.gDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH) + 1,
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                JalaliCalendar.gDate currentDate = JalaliCalendar.MiladiToJalali(date);
                final NumberPicker numberPicker = view.findViewById(R.id.numPicker);
                String[] month = new String[]{"فروردین", "اردیبهشت", "خرداد",
                        "تیر", "مرداد", "شهریور",
                        "مهر", "آبان", "آذر",
                        "دی", "بهمن", "اسفند"};
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(12);
                numberPicker.setDisplayedValues(month);
                numberPicker.setValue(preferences.getInt("month", currentDate.getMonth()));
                changeNumberPickerDividerColor(numberPicker, getResources().getColor(R.color.colorAccent));

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        editor.putInt("month", numberPicker.getValue()).apply();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                width = (int) ((width) * (3.5 / 5));
                dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case R.id.btnYear:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_date, null);
                builder1.setView(view1);
                builder1.setMessage("نمایش بر اساس سال:");
                builder1.setPositiveButton("انتخاب", null);
                builder1.setNegativeButton("رد کردن", null);

                JalaliCalendar.gDate date1 = new JalaliCalendar.gDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH) + 1,
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                JalaliCalendar.gDate currentDate1 = JalaliCalendar.MiladiToJalali(date1);
                final NumberPicker numberPicker1 = view1.findViewById(R.id.numPicker);

                numberPicker1.setMinValue(1390);
                numberPicker1.setMaxValue(1500);
                numberPicker1.setValue(preferences.getInt("year", currentDate1.getYear()));
                changeNumberPickerDividerColor(numberPicker1, getResources().getColor(R.color.colorAccent));

                final AlertDialog dialog1 = builder1.create();
                dialog1.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                dialog1.show();

                dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                        editor.putInt("year", numberPicker1.getValue()).apply();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });

                Display display1 = getWindowManager().getDefaultDisplay();
                Point size1 = new Point();
                display1.getSize(size1);
                int width1 = size1.x;
                width1 = (int) ((width1) * (3.5 / 5));
                dialog1.getWindow().setLayout(width1, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
        }
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.setting:
                drawerLay.closeDrawer(Gravity.START);
                break;
            case R.id.erase_all:
                drawerLay.closeDrawer(Gravity.START);

                if (db.getAllTransactionsCount() > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_erase, null);
                    builder.setView(view);

                    TextView eraseMonth = view.findViewById(R.id.txtEraseMonth);
                    TextView eraseAll = view.findViewById(R.id.txtEraseAll);

                    final AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                    dialog.show();

                    eraseMonth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                            builder.setMessage("همه تراکنش ها حذف شوند؟");
                            builder.setPositiveButton("حذف همه", null);
                            builder.setNegativeButton("رد کردن", null);

                            final AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                            dialog.show();

                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    setEraseMonthSnackBar();
                                }
                            });

                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int width = size.x;
                            width = (int) ((width) * (4.5 / 5));
                            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    });

                    eraseAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                            builder.setMessage("همه تراکنش ها حذف شوند؟");
                            builder.setPositiveButton("حذف همه", null);
                            builder.setNegativeButton("رد کردن", null);

                            final AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
                            dialog.show();

                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    setEraseAllSnackBar();
                                }
                            });

                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int width = size.x;
                            width = (int) ((width) * (4.5 / 5));
                            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    });

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    width = (int) ((width) * (3.5 / 5));
                    dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                } else
                    Toast.makeText(context, "تراکنشی برای حذف موجود نیست", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }*/

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 0) {

            fragmentManager.popBackStack();
            frameLayout.setVisibility(View.GONE);
            layParent.setVisibility(View.VISIBLE);
        } /*else if (drawerLay.isDrawerOpen(Gravity.START))
            drawerLay.closeDrawer(Gravity.START);*/ else
            super.onBackPressed();
    }

    private void initView() {

        menu = findViewById(R.id.menu);
        arrange = findViewById(R.id.arrange);
        btnPayments = findViewById(R.id.btnPayments);
        btnAll = findViewById(R.id.btnAll);
        btnIncomes = findViewById(R.id.btnIncomes);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        btnAdd = findViewById(R.id.btnAdd);
        txtBalance = findViewById(R.id.txtBalance);
        layParent = findViewById(R.id.layParent);
        frameLayout = findViewById(R.id.frameLayout);
        /*navigationView = findViewById(R.id.navigationView);
        drawerLay = findViewById(R.id.drawerLay);*/
        progressCounter = findViewById(R.id.progressCounter);
        txtCounter = findViewById(R.id.txtCounter);
        laySnackBar = findViewById(R.id.laySnackBar);
        txtCurrency = findViewById(R.id.txtCurrency);
        btnUndo = findViewById(R.id.btnUndo);
        btnMonth = findViewById(R.id.btnMonth);
        btnYear = findViewById(R.id.btnYear);
        txtNoTransaction = findViewById(R.id.txtNoTransaction);
    }

    private void setAllTransactions() {

        allList = new ArrayList<>();
        incomeList = new ArrayList<>();
        paymentList = new ArrayList<>();

        JalaliCalendar.gDate date = new JalaliCalendar.gDate(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        JalaliCalendar.gDate currentDate = JalaliCalendar.MiladiToJalali(date);
        Cursor data = null;
        switch (preferences.getString("arrange", "n")) {
            case "n":
                data = db.getNewestTransactions(preferences.getInt("month", currentDate.getMonth()), preferences.getInt("year", currentDate.getYear()));
                break;
            case "o":
                data = db.getOldestTransactions(preferences.getInt("month", currentDate.getMonth()), preferences.getInt("year", currentDate.getYear()));
                break;
            case "h":
                data = db.getHighestAmountTransactions(preferences.getInt("month", currentDate.getMonth()), preferences.getInt("year", currentDate.getYear()));
                break;
            case "l":
                data = db.getLowestAmountTransactions(preferences.getInt("month", currentDate.getMonth()), preferences.getInt("year", currentDate.getYear()));
                break;
        }

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {

            int id = data.getInt(data.getColumnIndex(db.col_id));
            String title = data.getString(data.getColumnIndex(db.col_title));
            long amount = data.getLong(data.getColumnIndex(db.col_amount));
            int day = data.getInt(data.getColumnIndex(db.col_day));
            int month = data.getInt(data.getColumnIndex(db.col_month));
            int year = data.getInt(data.getColumnIndex(db.col_year));
            String description = data.getString(data.getColumnIndex(db.col_description));
            String type = data.getString(data.getColumnIndex(db.col_type));

            allList.add(new Transaction(id, title, amount, day, month, year, description, type));
            if (type.equals("I"))
                incomeList.add(new Transaction(id, title, amount, day, month, year, description, type));
            else
                paymentList.add(new Transaction(id, title, amount, day, month, year, description, type));
        }

        recyclerTransactions.setLayoutManager(new LinearLayoutManager(context));
        recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, allList));
    }

    private void changeTab(int selectedButtonId) {

        switch (selectedButtonId) {

            case R.id.btnAll:
                btnAll.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_selected));
                btnIncomes.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_unselected));
                btnPayments.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_unselected));
                btnAll.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnIncomes.setTextColor(getResources().getColor(R.color.colorWhite));
                btnPayments.setTextColor(getResources().getColor(R.color.colorWhite));
                recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, allList));
                break;
            case R.id.btnIncomes:
                btnAll.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_unselected));
                btnIncomes.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_selected));
                btnPayments.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_unselected));
                btnAll.setTextColor(getResources().getColor(R.color.colorWhite));
                btnIncomes.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnPayments.setTextColor(getResources().getColor(R.color.colorWhite));
                recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, incomeList));
                break;
            case R.id.btnPayments:
                btnAll.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_unselected));
                btnIncomes.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_unselected));
                btnPayments.setBackground(getResources().getDrawable(R.drawable.bg_tab_button_selected));
                btnAll.setTextColor(getResources().getColor(R.color.colorWhite));
                btnIncomes.setTextColor(getResources().getColor(R.color.colorWhite));
                btnPayments.setTextColor(getResources().getColor(R.color.colorPrimary));
                recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, paymentList));
                break;
        }
    }

    private void setEraseMonthSnackBar() {

        txtBalance.setText("0");
        final List<Transaction> allTrashedList = new ArrayList<>();
        final List<Transaction> paymentTrashedList = new ArrayList<>();
        final List<Transaction> incomeTrashedList = new ArrayList<>();
        allTrashedList.addAll(allList);
        paymentTrashedList.addAll(paymentList);
        incomeTrashedList.addAll(incomeList);
        allList.clear();
        paymentList.clear();
        incomeList.clear();

        recyclerTransactions.getAdapter().notifyDataSetChanged();
        laySnackBar.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_top));
        laySnackBar.setVisibility(View.VISIBLE);

        final CountDownTimer countDownTimer = new CountDownTimer(5000, 1) {

            @Override
            public void onTick(long millisUntilFinished) {

                txtCounter.setText(String.valueOf(millisUntilFinished / 1000));
                progressCounter.setProgress((int) (millisUntilFinished / 50));
            }

            @Override
            public void onFinish() {
                laySnackBar.setVisibility(View.GONE);
                JalaliCalendar.gDate date = new JalaliCalendar.gDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH) + 1,
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                JalaliCalendar.gDate currentDate = JalaliCalendar.MiladiToJalali(date);
                db.deleteBy(preferences.getInt("month", currentDate.getMonth()), preferences.getInt("year", currentDate.getYear()));
                editor.putLong("balance" + selectedDate, 0).apply();
                txtBalance.setText("0");
            }
        };
        countDownTimer.start();

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countDownTimer.cancel();
                txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + selectedDate, 0)));
                allList.addAll(allTrashedList);
                paymentList.addAll(paymentTrashedList);
                incomeList.addAll(incomeTrashedList);
                recyclerTransactions.getAdapter().notifyDataSetChanged();
                laySnackBar.setVisibility(View.GONE);
            }
        });
    }

    private void setEraseAllSnackBar() {

        txtBalance.setText("0");
        final List<Transaction> allTrashedList = new ArrayList<>();
        final List<Transaction> paymentTrashedList = new ArrayList<>();
        final List<Transaction> incomeTrashedList = new ArrayList<>();
        allTrashedList.addAll(allList);
        paymentTrashedList.addAll(paymentList);
        incomeTrashedList.addAll(incomeList);
        allList.clear();
        paymentList.clear();
        incomeList.clear();

        recyclerTransactions.getAdapter().notifyDataSetChanged();
        laySnackBar.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_top));
        laySnackBar.setVisibility(View.VISIBLE);

        final CountDownTimer countDownTimer = new CountDownTimer(5000, 1) {

            @Override
            public void onTick(long millisUntilFinished) {

                txtCounter.setText(String.valueOf(millisUntilFinished / 1000));
                progressCounter.setProgress((int) (millisUntilFinished / 50));
            }

            @Override
            public void onFinish() {
                laySnackBar.setVisibility(View.GONE);
                db.deleteAll();
                for (int i = 1390; i <= 1500; i++) {
                    for (int y = 1; y <= 12; y++) {
                        editor.putLong("balance" + i + "" + y, 0).apply();
                    }
                }
                //editor.putLong("balance" + selectedDate, 0).apply();
                txtBalance.setText("0");
            }
        };
        countDownTimer.start();

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countDownTimer.cancel();
                txtBalance.setText(String.format("%,3d", preferences.getLong("balance" + selectedDate, 0)));
                allList.addAll(allTrashedList);
                paymentList.addAll(paymentTrashedList);
                incomeList.addAll(incomeTrashedList);
                recyclerTransactions.getAdapter().notifyDataSetChanged();
                laySnackBar.setVisibility(View.GONE);
            }
        });
    }

    private void changeNumberPickerDividerColor(NumberPicker picker, int color) {

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

    private void showArrangeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_arrange, null);
        builder.setMessage("نمایش بر اساس:");
        builder.setView(view);

        final TextView btnNewest = view.findViewById(R.id.btnNewest);
        final TextView btnOldest = view.findViewById(R.id.btnOldest);
        final TextView btnHighestAmount = view.findViewById(R.id.btnHighestAmount);
        final TextView btnLowestAmount = view.findViewById(R.id.btnLowestAmount);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        switch (preferences.getString("arrange", "n")) {
            case "n":
                btnNewest.setBackground(getResources().getDrawable(R.drawable.bg_arrange_button_selected));
                btnNewest.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case "o":
                btnOldest.setBackground(getResources().getDrawable(R.drawable.bg_arrange_button_selected));
                btnOldest.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case "h":
                btnHighestAmount.setBackground(getResources().getDrawable(R.drawable.bg_arrange_button_selected));
                btnHighestAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case "l":
                btnLowestAmount.setBackground(getResources().getDrawable(R.drawable.bg_arrange_button_selected));
                btnLowestAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }

        btnNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("arrange", "n").apply();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        btnOldest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("arrange", "o").apply();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        btnHighestAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("arrange", "h").apply();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        btnLowestAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("arrange", "l").apply();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (4.5 / 5));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
