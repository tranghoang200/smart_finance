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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public Spinner cat;
    public Button split;
    private Button exit;
    public Button add;
    private int index;


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
        exit = (Button) myview.findViewById(R.id.exit_button);
        add = (Button) myview.findViewById(R.id.addButton);
        index = 0;
        split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                split(view);

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                exit(view);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                add(view);

            }
        });

        return myview;
    }

    public void split(View view){
        Double result=Double.parseDouble(money_from.getText().toString()) / Double.parseDouble(people.getText().toString());
        money_to.setText(String.valueOf(result));

    }

    public void exit(View view){
//        Intent intent = new Intent(this, DashBoardFragment.class);
//        startActivity(intent);
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
//                    finish();
                }
            });
            builder.show();
//            Intent intent = new Intent(this, Expense.class);
//            startActivity(intent);

        } else {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//            Intent intent = new Intent(this, MainChatActivity.class);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String firebaseUsername = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference myRef = database.getReference("users/" + firebaseUsername).child("transactions");
            myRef.child(currentDate + "=Split=").setValue("- " + String.valueOf(money_to.getText()));
            final String a = String.valueOf(money_to.getText());
            final DatabaseReference balanceRef = database.getReference("users/" + firebaseUsername).child("balance");
            balanceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Double value = dataSnapshot.getValue(Double.class) - Double.parseDouble(a);
                    if (index < 1) {
                        balanceRef.setValue(value);
                        index++;
                    }
                    Log.d("DATABASE DEBUGGING", "Value is: " + value);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("DATABASE DEBUGGING", "Failed to read value.", error.toException());
                }
            });
            index = 0;
//            startActivity(intent);
        }
    }
}
