package com.santossingh.onlineinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Models.Admins;
import com.santossingh.onlineinventory.Models.Sellers;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.log_username) EditText Musername;
    @BindView(R.id.log_password) EditText Mpassword;
    @BindView(R.id.log_Admin) Button admin;
    @BindView(R.id.log_Seller) Button seller;
    List<Sellers> sellerses;
    Admins admins;
    String msg;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference adminRef, sellerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseDatabase=FirebaseDatabase.getInstance();
        sellerRef = firebaseDatabase.getReference().child("sellers");
        adminRef = firebaseDatabase.getReference().child("admin");
        sellerses = new ArrayList<>();
        admins = new Admins();
        sellerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Sellers sellers = dataSnapshot.getValue(Sellers.class);
                sellers.setKey(dataSnapshot.getKey());
                sellerses.add(0,sellers);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Sellers sellers = dataSnapshot.getValue(Sellers.class);
                sellers.setKey(dataSnapshot.getKey());
                sellerses.add(0,sellers);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "DataBase Error: " + databaseError.getMessage(), Toast.LENGTH_LONG);
            }
        });

        adminRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Admins admin = dataSnapshot.getValue(Admins.class);
                admin.setKey(dataSnapshot.getKey());
                admins.setValues(admin);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Admins admin = dataSnapshot.getValue(Admins.class);
                admin.setKey(dataSnapshot.getKey());
                admins.setValues(admin);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_Admin(admins);
            }
        });
        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_User(sellerses);
            }
        });
    }


    public void login_Admin(final Admins admins) {
        if (!Musername.getText().toString().isEmpty() || !Mpassword.getText().toString().isEmpty()) {

            if (Musername.getText().toString().equals(admins.getMobile()) && Mpassword.getText().toString().equals(admins.getPassword())) {
                Toast.makeText(MainActivity.this, "Welcome Admin.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Sorry, wrong mobile number or password.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please fill required fields.", Toast.LENGTH_LONG).show();
        }
    }

    public void login_User(final List<Sellers> sellerses) {
        String a = "Wrong login mobile or password!";
        if (!Musername.getText().toString().isEmpty() || !Mpassword.getText().toString().isEmpty()) {
            for (Sellers seller : sellerses) {
                if (Musername.getText().toString().equals(seller.getMobile()) && Mpassword.getText().toString().equals(seller.getPassword())) {
                    msg = "Welcome Mr. " + seller.getUsername();
                    Intent intent = new Intent(MainActivity.this, SellerActivity.class)
                            .putExtra("MOBILE", seller.getMobile());
                    startActivity(intent);
                }else{
                    msg = a;
                }
            }
            Toast.makeText(MainActivity.this, msg.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Please fill required fields.", Toast.LENGTH_LONG).show();
        }
    }

}
