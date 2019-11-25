package com.tranghoang.expense;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Date;

public class ViewNotiActivity extends AppCompatActivity {

    EditText amountTextView;
    Button edit;
    Button add;
    TextView balanceInvi;

    private DatabaseReference mExpenseDatabase;
    DatabaseReference balanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_noti);

        balanceInvi = findViewById(R.id.balanceInvi);
        Intent intent = getIntent();
        amountTextView = findViewById(R.id.amountDisplay);
        amountTextView.setText(intent.getStringExtra("addMoney"));

        edit = findViewById(R.id.editAmount);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountTextView.setEnabled(true);
            }
        });

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
                balanceInvi.setText(String.valueOf(value));
                Log.d("DATABASE DEBUGGING", "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DATABASE DEBUGGING", "Failed to read value.", error.toException());
            }
        });

        add = findViewById(R.id.addAmount);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
                String uid=mUser.getUid();
                mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
                String id=mExpenseDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(Double.parseDouble(amountTextView.getText().toString()),"Split","Split",id,mDate);
                mExpenseDatabase.child(id).setValue(data);

//                Log.d("DATABASE DEBUGGING", "Value invi is: " + Double.parseDouble(balanceInvi.getText().toString()));
//                Log.d("DATABASE DEBUGGING", "Value minus is: " + Double.parseDouble(amountTextView.getText().toString()));
                balanceRef.setValue(Double.parseDouble(balanceInvi.getText().toString()) - Double.parseDouble(amountTextView.getText().toString()));

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("addMoney", amountTextView.getText().toString());
                startActivity(intent);
            }
        });


    }
}
