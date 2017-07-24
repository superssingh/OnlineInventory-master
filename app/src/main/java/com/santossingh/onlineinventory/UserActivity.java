package com.santossingh.onlineinventory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Adapter.FirebaseRecyclerAdatper.UserRecycleAdapter;
import com.santossingh.onlineinventory.Models.Sellers;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity implements UserRecycleAdapter.GetDataFromAdapter {

    @BindView(R.id.user_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.Fab_add_user)
    FloatingActionButton FabButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference sellersReference;
    Sellers sellers;
    List<Sellers> sellersList;
    UserRecycleAdapter userRecycleAdapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        firebaseDatabase=FirebaseDatabase.getInstance();
        sellersReference = FirebaseDatabase.getInstance().getReference().child("sellers");
        sellers = new Sellers();
        sellersList = new ArrayList<>();
        userRecycleAdapter = new UserRecycleAdapter(this);

        sellersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Sellers sellers = dataSnapshot.getValue(Sellers.class);
                sellers.setKey(dataSnapshot.getKey());
                sellersList.add(sellers);
                RefeshAdapter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                sellersList.clear();
                Sellers sellers = dataSnapshot.getValue(Sellers.class);
                sellers.setKey(dataSnapshot.getKey());
                sellersList.add(sellers);
                RefeshAdapter();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                sellersList.clear();
                Sellers sellers = dataSnapshot.getValue(Sellers.class);
                sellers.setKey(dataSnapshot.getKey());
                sellersList.add(sellers);
                RefeshAdapter();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(userRecycleAdapter);

        FabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlergDialogBox();
            }
        });
    }

    @Override
    public void onCurrentUser(Sellers seller) {
        sellers = seller;
    }

    public void addAlergDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_user, null);

        final EditText user = (EditText) view.findViewById(R.id.userName);
        final EditText mobile = (EditText) view.findViewById(R.id.userMobile);
        final EditText pwd = (EditText) view.findViewById(R.id.userPassword);
        final Button add = (Button) view.findViewById(R.id.addUser);

        add.setText("ADD SELLER");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean is = sellersList.isEmpty();
                if (is.equals(true)) {
                    Sellers sellers = new Sellers(
                            user.getText().toString().toUpperCase(),
                            mobile.getText().toString(),
                            pwd.getText().toString()

                    );
                    sellersReference.push().setValue(sellers);
                    user.setText("");
                    mobile.setText("");
                    pwd.setText("");
                    Toast.makeText(UserActivity.this, "Seller added.", Toast.LENGTH_LONG).show();
                } else {
                    for (Sellers sellers : sellersList) {
                        if (mobile.getText().toString().equals(sellers.getMobile())) {
                            Toast.makeText(getApplicationContext(), "Seller already exist.", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                            Sellers seller = new Sellers();
                            seller.setUsername(user.getText().toString().toUpperCase());
                            seller.setMobile(mobile.getText().toString());
                            seller.setPassword(pwd.getText().toString());

                            sellersReference.push().setValue(seller);
                                user.setText("");
                                mobile.setText("");
                                pwd.setText("");
                            Toast.makeText(getApplicationContext(), "Seller added.", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }

            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    public void RefeshAdapter() {
        userRecycleAdapter = new UserRecycleAdapter(this);
        userRecycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(userRecycleAdapter);
    }


}