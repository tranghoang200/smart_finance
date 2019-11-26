package com.tranghoang.expense;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranghoang.expense.Model.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplitFragment extends Fragment {

    public EditText money_to;
    public EditText money_from;
    public EditText people;
    public Button split;
    public Button add;

    private DatabaseReference mExpenseDatabase;
    DatabaseReference balanceRef;
    TextView balanceInvi;


    public SplitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_split, container, false);

        money_from =  myview.findViewById(R.id.amount_from);
        money_to = (EditText) myview.findViewById(R.id.amount_to);
        people = (EditText) myview.findViewById(R.id.num_people_input);
//        cat = (Spinner) findViewById(R.id.split_types);
        split = (Button) myview.findViewById(R.id.splitButton);
        add = (Button) myview.findViewById(R.id.addButton);
        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                split(view);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                add(view);

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String firebaseUsername = FirebaseAuth.getInstance().getCurrentUser().getUid();
        balanceRef = database.getReference("users/" + firebaseUsername).child("balance");

        balanceInvi = myview.findViewById(R.id.balanceINvi);


        // Read from the database
        balanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double value = dataSnapshot.getValue(Double.class);
                balanceInvi.setText(String.valueOf(value));
                Log.d("DATABASE DEBUGGING", "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DATABASE DEBUGGING", "Failed to read value.", error.toException());
            }
        });

        return myview;
    }

    public void split(View view){
        Double result=Double.parseDouble(money_from.getText().toString()) / Double.parseDouble(people.getText().toString());
        money_to.setText(String.valueOf(result));

    }


    public void add(View view){
        final Context text=getContext();
        if (String.valueOf(money_to.getText()).equals("") || money_from.getText().equals("")){

            AlertDialog.Builder builder = new AlertDialog.Builder(text);
            builder.setTitle("Missing Information");
            builder.setMessage("Please fill all your information");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();

        } else {
            FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
            String uid=mUser.getUid();
            mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
            String id=mExpenseDatabase.push().getKey();
            String mDate= DateFormat.getDateInstance().format(new Date());
            Data data=new Data(Double.parseDouble(money_to.getText().toString()),"Split","Split",id,mDate);
            mExpenseDatabase.child(id).setValue(data);
            balanceRef.setValue(Double.parseDouble(balanceInvi.getText().toString()) - Double.parseDouble(money_to.getText().toString()));
            Intent intent = new Intent(getContext(), ChooseUserActivity.class);
            intent.putExtra("addMoney", money_to.getText().toString());
            startActivity(intent);

        }
    }
}
