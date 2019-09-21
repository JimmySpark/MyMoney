package mohaqeqi.mahdi.mymoney.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mohaqeqi.mahdi.mymoney.Activity.MainActivity;
import mohaqeqi.mahdi.mymoney.Fragment.AddTransactionFragment;
import mohaqeqi.mahdi.mymoney.Model.Transaction;
import mohaqeqi.mahdi.mymoney.R;

import static mohaqeqi.mahdi.mymoney.App.AppInitializer.month;
import static mohaqeqi.mahdi.mymoney.App.AppInitializer.preferences;

public class RecyclerTransactionAdapter extends RecyclerView.Adapter<RecyclerTransactionAdapter.ViewHolder> {

    Context context;
    List<Transaction> transactionList;

    public RecyclerTransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_transactions, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Transaction transaction = transactionList.get(i);

        viewHolder.txtTitle.setText(transaction.getTitle());
        viewHolder.txtAmount.setText(String.format("%,3d", transaction.getAmount()) + preferences.getString("currency", context.getResources().getString(R.string.toman)));
        viewHolder.txtDate.setText(transaction.getDay() + " " + month[transaction.getMonth() - 1] + " " + transaction.getYear());
        viewHolder.txtDescription.setText(transaction.getDescription());
        switch (transaction.getTransactionType()) {
            case "I":
                viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.colorIncome));
                viewHolder.imgIcon.setImageResource(R.drawable.ic_income);
                break;
            case "P":
                viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.colorPayment));
                viewHolder.imgIcon.setImageResource(R.drawable.ic_payment);
                break;
        }
        if (transaction.getDescription().equals(""))
            viewHolder.layDescription.setVisibility(View.GONE);
        else
            viewHolder.layDescription.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if (transactionList.size() > 0)
            MainActivity.txtNoTransaction.setVisibility(View.GONE);
        else
            MainActivity.txtNoTransaction.setVisibility(View.VISIBLE);
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTitle;
        TextView txtAmount;
        TextView txtDate;
        ImageView imgIcon;
        ImageView imgArrow;
        TextView txtDescription;
        LinearLayout layDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            layDescription = itemView.findViewById(R.id.layDescription);

            layDescription.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.transaction_option_menu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.edit) {

                                Transaction transaction = transactionList.get(getAdapterPosition());

                                MainActivity.frameLayout.setVisibility(View.VISIBLE);
                                MainActivity.layParent.setVisibility(View.GONE);

                                AddTransactionFragment addTransactionFragment = new AddTransactionFragment();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("editable", true);
                                bundle.putInt("id", transaction.getId());
                                bundle.putString("title", transaction.getTitle());
                                bundle.putLong("amount", transaction.getAmount());
                                bundle.putString("type", transaction.getTransactionType());
                                bundle.putInt("day", transaction.getDay());
                                bundle.putInt("month", transaction.getMonth());
                                bundle.putInt("year", transaction.getYear());
                                bundle.putString("description", transaction.getDescription());
                                addTransactionFragment.setArguments(bundle);

                                FragmentTransaction fragmentTransaction = MainActivity.fragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.frameLayout, addTransactionFragment, "addTransactionFrag");
                                fragmentTransaction.addToBackStack("addTransactionFrag");
                                fragmentTransaction.commit();
                            }
                            return true;
                        }
                    });
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.layDescription) {

                if (txtDescription.getVisibility() != View.VISIBLE) {

                    imgArrow.setImageResource(R.drawable.ic_collapse_arrow);
                    txtDescription.setVisibility(View.VISIBLE);
                } else {
                    imgArrow.setImageResource(R.drawable.ic_expand_arrow);
                    txtDescription.setVisibility(View.GONE);
                }
            }
        }
    }
}
