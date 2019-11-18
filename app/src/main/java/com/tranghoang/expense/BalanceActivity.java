package com.tranghoang.expense;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BalanceActivity extends AppCompatActivity {

    public EditText lastName;
    public EditText firstName;
    public EditText balance;
    public Button registerFinish;

    Cursor result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        //call the read database method
    }

    public void signUp(View v) {
        lastName = (EditText) findViewById(R.id.last_name);
        firstName = (EditText) findViewById(R.id.first_name);
        balance = (EditText) findViewById(R.id.balance);
        registerFinish = (Button) findViewById(R.id.register_finish);

        //Instantiate the Balance object
        String last = lastName.getText().toString();
        String first = firstName.getText().toString();
        Double addedBalance = Double.valueOf(balance.getText().toString());
//        Balance newUser = new Balance(addedBalance, first, last);

        Toast.makeText(BalanceActivity.this, "Successfully entered data", Toast.LENGTH_LONG).show();
        Intent a = getIntent();

        Intent i = new Intent(BalanceActivity.this, HomeActivity.class);
//        i.putExtra("last name", last);
//        i.putExtra("first name", first);
//        i.putExtra("balance", addedBalance);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String firebaseUsername = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference("users/" + firebaseUsername);
        myRef.child("balance").setValue(addedBalance);
        myRef.child("fullname").setValue(first + " " +last);
        myRef.child("email").setValue(a.getStringExtra("reg_email"));
        startActivity(i);

    }


}