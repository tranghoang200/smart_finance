package com.tranghoang.expense;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    ArrayList<String> expense;
    ArrayList<String> income;
    Double sum = 0.0;
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

        expense = new ArrayList<String>();
        income = new ArrayList<String>();
        cat=new HashMap();
        list=new ArrayList<>();

        pieChartView = myview.findViewById(R.id.chart);
        pieChartView_cat = myview.findViewById(R.id.chart_cat);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String firebaseUsername = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference transactionRef = database.getReference("users/" + firebaseUsername).child("transactions");

        transactionRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                if (dataSnapshot != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.i("new data", postSnapshot.toString());
                        String sign = postSnapshot.getValue().toString().substring(0, 1);
                        String value = postSnapshot.getValue().toString().substring(2, postSnapshot.getValue().toString().length());
                        String[] key = postSnapshot.getKey().split("=");
                        if (cat.containsKey(key[2])){
                            cat.put(key[2], cat.get(key[2])+ Double.parseDouble(value));
                        } else {
                            cat.put(key[2], Double.parseDouble(value));
                        }
                        sum += Double.parseDouble(postSnapshot.getValue().toString().substring(2, postSnapshot.getValue().toString().length()));
                        if (sign.equals("-")) {
                            expense.add(value);
                            Log.i("expense", expense.toString());
                        } else {
                            income.add(value);
                            Log.i("income", income.toString());
                        }
                        Log.i("sum", String.valueOf(sum));
                    }
                    drawchart(expense, income);
                    drawcat(cat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return myview;
    }

    public void drawchart(ArrayList<String> expense, ArrayList<String> income) {
        double sum_expense = 0.0;
        double sum_income = 0.0;
        for (int i = 0; i < expense.size(); i++) {
            sum_expense += Double.parseDouble(expense.get(i));
        }
        Log.i("sum_expense", String.valueOf(sum_expense));
        for (int i = 0; i < income.size(); i++) {
            sum_income += Double.parseDouble(income.get(i));
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
