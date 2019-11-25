package com.tranghoang.expense;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranghoang.expense.Model.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment {
    ArrayList<Double> expense;
    ArrayList<Double> income;
    Double sum;
    double expenseSum;
    double incomeSum;
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mIncomeDatabase;

    String uid;
    HashMap<String, Double> cat;
    ArrayList<String> list;
    PieChartView pieChartView;
    PieChartView pieChartView_cat;
    public ChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_chart, container, false);

        expense = new ArrayList<Double>();
        income = new ArrayList<Double>();
        cat=new HashMap();
        list=new ArrayList<>();

        expenseSum=0.0;
        incomeSum=0.0;
        sum=0.0;

        pieChartView = myview.findViewById(R.id.chart);
        pieChartView_cat = myview.findViewById(R.id.chart_cat);


        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        uid=mUser.getUid();
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                for (DataSnapshot mysanapshot:dataSnapshot.getChildren()){

                    Data data=mysanapshot.getValue(Data.class);
                    expense.add((double) data.getAmount());
                    sum+=(double) data.getAmount();

                }
                mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

                mIncomeDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        for (DataSnapshot mysanapshot:dataSnapshot.getChildren()){

                            Data data=mysanapshot.getValue(Data.class);
                            income.add((double) data.getAmount());
                            sum+=(double) data.getAmount();

                        }
                        drawchart(expense, income);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return myview;
    }

    public void drawchart(ArrayList<Double> expense, ArrayList<Double> income) {
        double sum_expense = 0.0;
        double sum_income = 0.0;
        for (int i = 0; i < expense.size(); i++) {
            sum_expense += expense.get(i);
        }
        Log.i("sum_expense", String.valueOf(sum_expense));
        for (int i = 0; i < income.size(); i++) {
            sum_income += income.get(i);
        }
        Log.i("sum_income", String.valueOf(sum_income));
        int expense_percent = (int) Math.round((sum_expense *100) / sum);
        int income_percent = (int) Math.round((sum_income *100) / sum);
        Log.i("percent", String.valueOf(expense_percent) + " " + String.valueOf(income_percent));


        List<SliceValue> pieData = new ArrayList<>();
        PieChartData pieChartData = new PieChartData(pieData);
        pieData.add(new SliceValue(income_percent, Color.BLUE).setLabel("Income"));
        pieData.add(new SliceValue(expense_percent, Color.RED).setLabel("Expense"));
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true).setCenterText1("Expense vs Income").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);
    }

    public void drawcat(HashMap<String,Double> cat) {
        int value=0;
        List<SliceValue> pieData_cat = new ArrayList<>();
        PieChartData pieChartData_cat = new PieChartData(pieData_cat);
        for (String key: cat.keySet()){
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            value=(int) Math.round((cat.get(key)*100)/sum);
            pieData_cat.add(new SliceValue(value, color).setLabel(key));
        }
        pieChartData_cat.setHasLabels(true);
        pieChartView_cat.setPieChartData(pieChartData_cat);
    }

}
