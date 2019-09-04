package mohaqeqi.mahdi.mymoney.Activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mohaqeqi.mahdi.mymoney.Adapter.RecyclerTransactionAdapter;
import mohaqeqi.mahdi.mymoney.Fragment.AddTransactionFragment;
import mohaqeqi.mahdi.mymoney.Model.Transaction;
import mohaqeqi.mahdi.mymoney.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static mohaqeqi.mahdi.mymoney.App.AppInitializer.db;
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
    NavigationView navMenu;
    DrawerLayout drawerLay;
    AddTransactionFragment addTransactionFragment;
    Bundle bundle;
    List<Transaction> allList;
    List<Transaction> incomeList;
    List<Transaction> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nav);
        initView();
        fragmentManager = getSupportFragmentManager();
        setAllTransactions();

        txtBalance.setText(String.format("%,3d", preferences.getLong("balance", 0)));
        btnAdd.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnIncomes.setOnClickListener(this);
        btnPayments.setOnClickListener(this);
        menu.setOnClickListener(this);
        arrange.setOnClickListener(this);
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
                bundle = new Bundle();
                bundle.putString("tType", "I");
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
                drawerLay.openDrawer(Gravity.START);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 0) {

            fragmentManager.popBackStack();
            frameLayout.setVisibility(View.GONE);
            layParent.setVisibility(View.VISIBLE);
        } else if (drawerLay.isDrawerOpen(Gravity.START))
            drawerLay.closeDrawer(Gravity.START);
        else
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
        navMenu = findViewById(R.id.navMenu);
        drawerLay = findViewById(R.id.drawerLay);
    }

    private void setAllTransactions() {

        allList = new ArrayList<>();
        incomeList = new ArrayList<>();
        paymentList = new ArrayList<>();

        Cursor data = db.getAllTransactions();

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
                btnAll.setBackground(getResources().getDrawable(R.drawable.tab_button_selected));
                btnIncomes.setBackground(getResources().getDrawable(R.drawable.tab_button_unselected));
                btnPayments.setBackground(getResources().getDrawable(R.drawable.tab_button_unselected));
                btnAll.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnIncomes.setTextColor(getResources().getColor(R.color.colorWhite));
                btnPayments.setTextColor(getResources().getColor(R.color.colorWhite));
                recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, allList));
                break;
            case R.id.btnIncomes:
                btnAll.setBackground(getResources().getDrawable(R.drawable.tab_button_unselected));
                btnIncomes.setBackground(getResources().getDrawable(R.drawable.tab_button_selected));
                btnPayments.setBackground(getResources().getDrawable(R.drawable.tab_button_unselected));
                btnAll.setTextColor(getResources().getColor(R.color.colorWhite));
                btnIncomes.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnPayments.setTextColor(getResources().getColor(R.color.colorWhite));
                recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, incomeList));
                break;
            case R.id.btnPayments:
                btnAll.setBackground(getResources().getDrawable(R.drawable.tab_button_unselected));
                btnIncomes.setBackground(getResources().getDrawable(R.drawable.tab_button_unselected));
                btnPayments.setBackground(getResources().getDrawable(R.drawable.tab_button_selected));
                btnAll.setTextColor(getResources().getColor(R.color.colorWhite));
                btnIncomes.setTextColor(getResources().getColor(R.color.colorWhite));
                btnPayments.setTextColor(getResources().getColor(R.color.colorPrimary));
                recyclerTransactions.setAdapter(new RecyclerTransactionAdapter(context, paymentList));
                break;
        }
    }
}
