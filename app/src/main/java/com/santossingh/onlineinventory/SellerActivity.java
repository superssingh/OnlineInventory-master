package com.santossingh.onlineinventory;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Adapter.FirebaseRecyclerAdatper.SellerRecycleAdapter;
import com.santossingh.onlineinventory.Models.Inventory;
import com.santossingh.onlineinventory.Models.Orders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerActivity extends AppCompatActivity implements SellerRecycleAdapter.GetDataFromAdapter {

    @BindView(R.id.Prd_recycleView)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SellerRecycleAdapter recycleAdapter;
    DatabaseReference inventoryRef, ordersRef;
    String sellerID = "000";
    AlertDialog dialog;
    List<Inventory> inventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        sellerID = bundle.getString("MOBILE");
        inventoryList = new ArrayList<>();

        inventoryRef = FirebaseDatabase.getInstance().getReference().child("inventory");
        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        recycleAdapter = new SellerRecycleAdapter(SellerActivity.this);

        inventoryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Inventory inventory = dataSnapshot.getValue(Inventory.class);
                inventory.setKey(dataSnapshot.getKey());
                inventoryList.add(0, inventory);
                RefeshAdapter();
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
    }

    @Override
    public void onCurrentMovie(Inventory currentData) {
        if (Integer.parseInt(currentData.getQuantity()) >= 1) {
            alergDialogBox(currentData);
        } else {
            String a = currentDate();
            Toast.makeText(this, a, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Selected product not available,\n Please add quantity.", Toast.LENGTH_LONG).show();
        }
    }

    public void alergDialogBox(final Inventory inventory) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SellerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.order_dailog, null);

        final EditText name = (EditText) view.findViewById(R.id.BuyerName);
        final EditText mobile = (EditText) view.findViewById(R.id.BuyerMobile);
        final EditText address = (EditText) view.findViewById(R.id.BuyerAddress);
        final TextView product = (TextView) view.findViewById(R.id.BuyProduct);
        final NumberPicker qty = (NumberPicker) view.findViewById(R.id.Buy_qty);
        final TextView price = (TextView) view.findViewById(R.id.BuyPrice);
        final EditText paid = (EditText) view.findViewById(R.id.PaidAmount);
        final TextView balance = (TextView) view.findViewById(R.id.Balance);
        final Button order = (Button) view.findViewById(R.id.Order);

        final String key = inventory.getKey();
        product.setText(inventory.getItem());
        qty.setMinValue(1);
        qty.setMaxValue(Integer.parseInt(inventory.getQuantity()));
        qty.setValue(1);
        paid.setText(inventory.getPrice());
        price.setText(inventory.getPrice());
        balance.setText("0");

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orderPrice = Integer.parseInt(inventory.getPrice()) * qty.getValue();
                int currentbalance = orderPrice - Integer.parseInt(paid.getText().toString());
                price.setText(Integer.toString(orderPrice));
                balance.setText(Integer.toString(currentbalance));
            }
        });

        qty.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int orderPrice = Integer.parseInt(inventory.getPrice()) * qty.getValue();
                int currentbalance = orderPrice - Integer.parseInt(paid.getText().toString());
                price.setText(Integer.toString(orderPrice));
                balance.setText(Integer.toString(currentbalance));
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty() || !mobile.getText().toString().isEmpty() || !address.getText().toString().isEmpty()) {
                    int quantity = Integer.parseInt(inventory.getQuantity()) - qty.getValue();
                    int orderPrice = Integer.parseInt(inventory.getPrice()) * qty.getValue();
                    int currentbalance = orderPrice - Integer.parseInt(paid.getText().toString());
                    if (quantity >= 0) {
                        Orders orders = new Orders(
                                name.getText().toString(),
                                mobile.getText().toString(),
                                address.getText().toString(),
                                inventory.getItem(),
                                Integer.toString(qty.getValue()),
                                Integer.toString(orderPrice),
                                paid.getText().toString(),
                                Integer.toString(currentbalance),
                                currentDate(),
                                sellerID
                        );
                        Inventory inventory1 = new Inventory();
                        inventory1.setItem(inventory.getItem());
                        inventory1.setQuantity(Integer.toString(quantity));
                        inventory1.setPrice(inventory.getPrice());

                        ordersRef.push().setValue(orders);
                        inventoryRef.child(key).setValue(inventory1);
                        Toast.makeText(SellerActivity.this, "Product Successfully booked.", Toast.LENGTH_LONG).show();
                        recycleAdapter = new SellerRecycleAdapter(SellerActivity.this);
                        recycleAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(recycleAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Product not available", Toast.LENGTH_LONG).show();
                    }
                    view.setVisibility(view.GONE);
                } else {
                    Toast.makeText(SellerActivity.this, "Please fill required fields in order.", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    public String currentDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String currentDate = date + "-" + month + "-" + year;

        return currentDate;
    }

    public String currentTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        String currentTime = hour + ":" + min + ":" + sec;
        return currentTime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.OrdersMenu:
                Intent intent = new Intent(SellerActivity.this, OrdersActivity.class)
                        .putExtra("MOBILE", sellerID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void RefeshAdapter() {
        recycleAdapter = new SellerRecycleAdapter(this);
        recycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recycleAdapter);
    }

}
