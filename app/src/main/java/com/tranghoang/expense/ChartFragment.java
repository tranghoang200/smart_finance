package com.tranghoang.expense;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranghoang.expense.Model.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    ArrayList<Double> expense;
    ArrayList<Double> income;
    String[] category;
    int itemCount;
    Double sum;
    double expenseSum;
    double incomeSum;
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mIncomeDatabase;
    private int foodExpense,travelExpense,clothesExpense,moviesExpense,groceryExpense,otherExpense;

    String uid;
    Spinner spinner;
    PieChartView pieChartView;
    PieChartView pieChartView_cat;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_chart, container, false);

        expense = new ArrayList<Double>();
        income = new ArrayList<Double>();

        spinner = myview.findViewById(R.id.category_spinner);
        spinner.setOnItemSelectedListener(this);

        category = getResources().getStringArray(R.array.categories_array);

        // list=new ArrayList<>();

        expenseSum = 0.0;
        incomeSum = 0.0;
        sum = 0.0;

        pieChartView = myview.findViewById(R.id.chart);
        pieChartView_cat = myview.findViewById(R.id.chart_cat);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot mysanapshot : dataSnapshot.getChildren()) {

                    Data data = mysanapshot.getValue(Data.class);
                    expense.add((double) data.getAmount());
                    category = getResources().getStringArray(R.array.categories_array);
                    sum += (double) data.getAmount();

                }
                mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

                mIncomeDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot mysanapshot : dataSnapshot.getChildren()) {

                            Data data = mysanapshot.getValue(Data.class);
                            income.add((double) data.getAmount());
                            sum += (double) data.getAmount();

                        }
                        drawchart(expense, income);
                        //cat(category, itemCount);


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
        int expense_percent = (int) Math.round((sum_expense * 100) / sum);
        int income_percent = (int) Math.round((sum_income * 100) / sum);
        Log.i("percent", String.valueOf(expense_percent) + " " + String.valueOf(income_percent));


        List<SliceValue> pieData = new ArrayList<>();
        PieChartData pieChartData = new PieChartData(pieData);
        pieData.add(new SliceValue(income_percent, Color.BLUE).setLabel("Income"));
        pieData.add(new SliceValue(expense_percent, Color.RED).setLabel("Expense"));
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true).setCenterText1("Expense vs Income").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }



    public void cat(String[] category, int itemCount) {
        category = getResources().getStringArray(R.array.categories_array);
        HashMap<String, Integer> mp = new HashMap<>();

        // Traverse through array elements and
        // count frequencies
        int cat = category.length;
        List<SliceValue> categories = new ArrayList<>();
        PieChartData pieChartData = new PieChartData(categories);

        for (int i = 0; i < cat; i++) {
            if (mp.containsKey(category[i])) {
                mp.put(category[i], mp.get(category[i]) + 1);
            } else {
                mp.put(category[i], 1);
            }
        }
        for (HashMap.Entry<String, Integer> entry : mp.entrySet()) {
            itemCount = entry.getValue();
        }
        Log.i("categories count", String.valueOf(itemCount));
        int cat_percent = (int) Math.round((itemCount * 100) / cat);

        if (categories.equals(category[0])) {
                categories.add(new SliceValue(cat_percent, Color.BLUE).setLabel("Food and Drinks"));
            }
            if (categories.equals(category[1])) {
                categories.add(new SliceValue(cat_percent, Color.RED).setLabel("Shopping"));
            }
            if (categories.equals(category[2])) {
                categories.add(new SliceValue(cat_percent, Color.MAGENTA).setLabel("Public Transport"));
            }
            if (categories.equals(category[3])) {
                categories.add(new SliceValue(cat_percent, Color.GREEN).setLabel("Groceries"));
            }
            if (categories.equals(category[3])) {
                categories.add(new SliceValue(cat_percent, Color.WHITE).setLabel("Education"));
            }
            if (categories.equals(category[4])) {
                categories.add(new SliceValue(cat_percent, Color.WHITE).setLabel("Investment"));
            }
            if (categories.equals(category[5])) {
                categories.add(new SliceValue(cat_percent, Color.WHITE).setLabel("Loan"));
            }
            if (categories.equals(category[6])) {
                categories.add(new SliceValue(cat_percent, Color.WHITE).setLabel("Entertainment"));
            }
            if (categories.equals(category[7])) {
                categories.add(new SliceValue(cat_percent, Color.WHITE).setLabel("Personal Care"));
            }
            if (categories.equals(category[8])) {
                categories.add(new SliceValue(cat_percent, Color.WHITE).setLabel("Other"));
            }
            pieChartData.setHasLabels(true);
       pieChartData.setHasCenterCircle(true).setCenterText1("Categories expense").setCenterText1FontSize(10).setCenterText1Color(Color.parseColor("#0097A7"));
       pieChartView_cat.setPieChartData(pieChartData);

        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
