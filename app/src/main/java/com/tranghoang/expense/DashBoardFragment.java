package com.tranghoang.expense;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranghoang.expense.Model.Data;

import java.text.DateFormat;
import java.util.Date;

import static android.text.TextUtils.isEmpty;
import static android.widget.AdapterView.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {

    //Floating buttton

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview..

    private TextView fab_income_txt;
    private TextView fab_expense_txt;


    //boolen

    private boolean isOpen=false;


    //Animation.


    private Animation FadOpen,FadeClose;

    //Dasbord income and expense result..

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private TextView totalBalance;


    ///Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    double balanceInit;
    double balanceChange;
    DatabaseReference balanceRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        totalBalance = myview.findViewById(R.id.balance);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String firebaseUsername = FirebaseAuth.getInstance().getCurrentUser().getUid();
        balanceRef = database.getReference("users/" + firebaseUsername).child("balance");


        // Read from the database
        balanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double value = dataSnapshot.getValue(Double.class);
                balanceInit = value;
                totalBalance.setText(String.valueOf(value));
                Log.d("DATABASE DEBUGGING", "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DATABASE DEBUGGING", "Failed to read value.", error.toException());
            }
        }
        );


        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //Connect floationg button to layout

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);

        //Connect floating text.

        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set..

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);


        //Animation connect..

        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if (isOpen){

                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;

                }else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;

                }

            }
        });


        //Calculate total income..


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalsum = 0;
                double total = balanceInit;

                for (DataSnapshot mysnap:dataSnapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);

                    totalsum+=data.getAmount();
                    total += data.getAmount();

                    String stResult=String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult+".00");
//                    balanceChange = total;
//                    totalBalance.setText(String.valueOf(total));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Calculate total expense..


        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalsum = 0;

                double total = balanceChange;

                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();

                    total -= data.getAmount();
                    String strTotalSum=String.valueOf(totalsum);

                    totalExpenseResult.setText(strTotalSum+".00");

//                    totalBalance.setText(String.valueOf(total));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Read from the database
//        balanceRef.setValue(Double.parseDouble(totalBalance.getText().toString()));


        // Read from the database
        balanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double value = dataSnapshot.getValue(Double.class);

                totalBalance.setText(String.valueOf(value));
                Log.d("DATABASE DEBUGGING", "Value is: " + value);
                                             }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            });


        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_epense);


        //Recycler

        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setReverseLayout(true);
        layoutManagerIncome.setStackFromEnd(true);

        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);



        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);



        return  myview;
    }

    //Floating button animation

    private void ftAnimation(){
        if (isOpen){

            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        }else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;

        }

    }

    private void addData(){

        //Fab Button income..

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                expenseDataInsert();
            }
        });

    }


    public void incomeDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm=inflater.inflate(R.layout.custom_layout_for_income,null);
        mydialog.setView(myviewm);
        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);


        final EditText edtAmount=myviewm.findViewById(R.id.amount_edt);
        final EditText edtType=myviewm.findViewById(R.id.type_edt);
        final Spinner incomeCategory  = myviewm.findViewById(R.id.category_spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.categories_income_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeCategory.setAdapter(adapter);
        incomeCategory.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type= edtType.getText().toString().trim();
                String amount= edtAmount.getText().toString().trim();
                String category = incomeCategory.getSelectedItem().toString().trim();

                if (isEmpty(type)){
                    edtType.setError("Required Field..");
                    return;
                }

                if (isEmpty(amount)){
                    edtAmount.setError("Required Field..");
                    return;
                }

                int ourAmountInt= Integer.parseInt(amount);

                String id= mIncomeDatabase.push().getKey();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data= new Data(ourAmountInt,type,category,id,mDate);

                balanceRef.setValue(balanceInit + ourAmountInt);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void expenseDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

       final AlertDialog dialog=mydialog.create();

       dialog.setCancelable(false);

        final EditText amount=myview.findViewById(R.id.amount_edt);
        final EditText name =myview.findViewById(R.id.type_edt);
        final Spinner expenseCategory =myview.findViewById(R.id.category_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.categories_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategory.setAdapter(adapter);
        expenseCategory.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmount= amount.getText().toString().trim();
                String tmName=  name.getText().toString().trim();
                String tmCategory = expenseCategory.getSelectedItem().toString().trim();

                if (isEmpty(tmAmount)){
                    amount.setError("Required Field..");
                    return;
                }

                int intAmount=Integer.parseInt(tmAmount);

                if (isEmpty(tmName)){
                    name.setError("Required Field..");
                    return;
                }

                if (isEmpty(tmCategory)){
                    expenseCategory.setAdapter(adapter);
                    expenseCategory.setOnItemSelectedListener(new CustomOnItemSelectedListener());
                    return;
                }


                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intAmount,tmName,tmCategory,id,mDate);
                balanceRef.setValue(balanceInit - intAmount);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });


        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        DashBoardFragment.IncomeViewHolder.class,
                        mIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, Data model, int position) {

                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeAmmount(model.getAmount());
                viewHolder.setIncomeDate(model.getDate());
                viewHolder.setIncomeCategory(model.getCategory());

            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);


        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_expense,
                        DashBoardFragment.ExpenseViewHolder.class,
                        mExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, Data model, int position) {

                viewHolder.setExpenseType(model.getType());
                viewHolder.setExpenseAmmount(model.getAmount());
                viewHolder.setExpenseDate(model.getDate());
                viewHolder.setExpenseCategory(model.getCategory());

            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);

    }


    //For Income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){

            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);

        }

        public void setIncomeAmmount(double ammount){

            TextView mAmmount=mIncomeView.findViewById(R.id.ammoun_income_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public void setIncomeDate(String date){

            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);

        }

        public void setIncomeCategory (String categoryIncome){

            TextView mDate=mIncomeView.findViewById(R.id.category_income_ds);
            mDate.setText(categoryIncome);

        }

    }

    //For expense data..

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){
            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }

        public void setExpenseCategory(String category){
            TextView mCategory=mExpenseView.findViewById(R.id.category_expense_ds);
            mCategory.setText(category);
        }

        public void setExpenseAmmount(double ammount){
            TextView mAmmount = mExpenseView.findViewById(R.id.ammoun_expense_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public void setExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }

    }




}
