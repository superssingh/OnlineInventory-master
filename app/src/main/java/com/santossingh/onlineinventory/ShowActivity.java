package com.santossingh.onlineinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Adapter.FirebaseRecyclerAdatper.RecycleAdapter;
import com.santossingh.onlineinventory.Models.Admins;
import com.santossingh.onlineinventory.Models.Inventory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowActivity extends AppCompatActivity implements RecycleAdapter.GetDataFromAdapter {

    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.Fab_add_product)
    FloatingActionButton floatingActionButton;
    LinearLayoutManager linearLayoutManager;
    RecycleAdapter recycleAdapter;
    DatabaseReference databaseReference, adminDatabase;
    AlertDialog dialog;
    List<Inventory> inventoryList;
    Admins adminsData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("inventory");
        adminDatabase = FirebaseDatabase.getInstance().getReference().child("admin");
        inventoryList = new ArrayList<>();
        adminsData = new Admins();
        recycleAdapter = new RecycleAdapter(ShowActivity.this);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Inventory inventory = dataSnapshot.getValue(Inventory.class);
                inventory.setKey(dataSnapshot.getKey());
                inventoryList.add(0, inventory);
                recycleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Inventory inventory = dataSnapshot.getValue(Inventory.class);
                inventory.setKey(dataSnapshot.getKey());
                inventoryList.add(0, inventory);
                RefeshAdapter();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (inventoryList != null) {
                    inventoryList.clear();
                }
                Inventory inventory = dataSnapshot.getValue(Inventory.class);
                inventory.setKey(dataSnapshot.getKey());
                inventoryList.add(0, inventory);
                recycleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No network found OR Database Error", Toast.LENGTH_LONG).show();
            }
        });

        adminDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Admins admins = dataSnapshot.getValue(Admins.class);
                admins.setKey(dataSnapshot.getKey());
                adminsData = admins;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Admins admins = dataSnapshot.getValue(Admins.class);
                admins.setKey(dataSnapshot.getKey());
                adminsData = admins;
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


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlergDialogBox();
            }
        });
    }


    @Override
    public void onCurrentMovie(Inventory currentData) {
        updateAlergDialogBox(currentData);
    }

    public void addAlergDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_product_dailogbox, null);

        final EditText product = (EditText) view.findViewById(R.id.AddproductName);
        final EditText qty = (EditText) view.findViewById(R.id.Addquantity);
        final EditText price = (EditText) view.findViewById(R.id.Addprice);
        final Button add = (Button) view.findViewById(R.id.Addadd);

        add.setText("ADD PRODUCT");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean is = inventoryList.isEmpty();
                if (is.equals(true)) {
                    Toast.makeText(getApplicationContext(), "First Record", Toast.LENGTH_LONG).show();
                    Inventory add = new Inventory(
                            product.getText().toString().toUpperCase(),
                            qty.getText().toString(),
                            price.getText().toString(), null);
                    databaseReference.push().setValue(add);
                    product.setText("");
                    qty.setText("");
                    price.setText("");
                    Toast.makeText(getApplicationContext(), "Product added.", Toast.LENGTH_LONG).show();

                } else {
                    for (Inventory inventory : inventoryList) {
                        if (product.getText().toString().toUpperCase().equals(inventory.getItem())) {
                            Toast.makeText(getApplicationContext(), "Product already exist.", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                            Inventory add = new Inventory(
                                    product.getText().toString().toUpperCase(),
                                    qty.getText().toString(),
                                    price.getText().toString(), null);
                            databaseReference.push().setValue(add);
                            product.setText("");
                            qty.setText("");
                            price.setText("");
                            Toast.makeText(getApplicationContext(), "Product added.", Toast.LENGTH_LONG).show();
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

    public void updateAlergDialogBox(final Inventory inventory) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_product_dailogbox, null);
        view.setVisibility(View.VISIBLE);
        final EditText product = (EditText) view.findViewById(R.id.AddproductName);
        final EditText qty = (EditText) view.findViewById(R.id.Addquantity);
        final EditText price = (EditText) view.findViewById(R.id.Addprice);
        final Button update = (Button) view.findViewById(R.id.Addadd);

        final String key = inventory.getKey();
        product.setText(inventory.getItem());
        qty.setText(inventory.getQuantity());
        price.setText(inventory.getPrice());
        update.setText("UPDATE PRODUCT");

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!product.getText().toString().isEmpty() || !qty.getText().toString().isEmpty() || !price.getText().toString().isEmpty()) {

                    Inventory inventory1 = new Inventory();
                    inventory1.setItem(product.getText().toString().toUpperCase());
                    inventory1.setQuantity(qty.getText().toString());
                    inventory1.setPrice(price.getText().toString());

                    databaseReference.child(key).setValue(inventory1);
                    Toast.makeText(ShowActivity.this, "Product updated.", Toast.LENGTH_LONG).show();
                    recycleAdapter = new RecycleAdapter(ShowActivity.this);
                    recycleAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(recycleAdapter);
                    view.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ShowActivity.this, "Please fill required fields.", Toast.LENGTH_LONG).show();
                }

            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    public void updateAdminDialogBox(final Admins admins) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        View view = getLayoutInflater().inflate(R.layout.update_admin, null);

        final EditText name = (EditText) view.findViewById(R.id.adminName);
        final EditText mobile = (EditText) view.findViewById(R.id.adminMobile);
        final EditText password = (EditText) view.findViewById(R.id.adminPassword);
        final Button upadte = (Button) view.findViewById(R.id.update_Admin);

        final String key = admins.getKey();

        name.setText(admins.getName());
        mobile.setText(admins.getMobile());
        password.setText(admins.getPassword());
        upadte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!name.getText().toString().isEmpty() || !mobile.getText().toString().isEmpty() || !password.getText().toString().isEmpty()) {

                    Admins updates = new Admins(
                            name.getText().toString().toUpperCase(),
                            mobile.getText().toString(),
                            password.getText().toString()
                    );

                    adminDatabase.child(key).setValue(updates);
                    Toast.makeText(ShowActivity.this, "Password Changed.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShowActivity.this, "Please fill required fields.", Toast.LENGTH_LONG).show();
                }

            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.OrderMenu:
                Intent intent = new Intent(ShowActivity.this, OrdersActivity.class)
                        .putExtra("MOBILE", adminsData.getMobile());
                startActivity(intent);
                return true;
            case R.id.SellerMenu:
                Intent intent1 = new Intent(ShowActivity.this, UserActivity.class);
                startActivity(intent1);
                return true;
            case R.id.AdminMenu:
                updateAdminDialogBox(adminsData);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void RefeshAdapter() {
        recycleAdapter = new RecycleAdapter(this);
        recycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recycleAdapter);
    }

}