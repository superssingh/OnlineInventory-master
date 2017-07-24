package com.santossingh.onlineinventory;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Adapter.FirebaseRecyclerAdatper.OrdersRecycleAdapter;
import com.santossingh.onlineinventory.Models.Orders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersActivity extends AppCompatActivity implements OrdersRecycleAdapter.GetDataFromAdapter {

    @BindView(R.id.orders_recycleView)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    OrdersRecycleAdapter recycleAdapter;
    DatabaseReference ordersRef;
    AlertDialog dialog;
    List<Orders> ordersList;
    String sellerID = "admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        sellerID = bundle.getString("MOBILE");

        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        ordersList = new ArrayList<>();

        ordersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Orders orders = dataSnapshot.getValue(Orders.class);
                orders.setKey(dataSnapshot.getKey());
                ordersList.add(0, orders);
                RefeshAdapter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ordersList.clear();
                Orders orders = dataSnapshot.getValue(Orders.class);
                orders.setKey(dataSnapshot.getKey());
                ordersList.add(0, orders);
                RefeshAdapter();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Orders orders = dataSnapshot.getValue(Orders.class);
                orders.setKey(dataSnapshot.getKey());
                ordersList.add(0, orders);
                RefeshAdapter();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        recycleAdapter = new OrdersRecycleAdapter(OrdersActivity.this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleAdapter);

    }

    @Override
    public void onCurrentMovie(Orders currentData) {
        Toast.makeText(this, currentData.getBuyer(), Toast.LENGTH_LONG).show();
        updatealergDialogBox(currentData);
    }


    public void updatealergDialogBox(final Orders orders) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
        View view = getLayoutInflater().inflate(R.layout.update_order, null);

        final TextView name = (TextView) view.findViewById(R.id.O_nameBuyer);
        final EditText paid = (EditText) view.findViewById(R.id.O_namePaid);
        final TextView balance = (TextView) view.findViewById(R.id.O_nameBalance);
        final Button order = (Button) view.findViewById(R.id.O_updateButton);

        final String key = orders.getKey();
        name.setText(orders.getBuyer());
        paid.setText(orders.getPaid());
        balance.setText(orders.getBalance());

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!paid.getText().toString().isEmpty()) {
                    int currentbalance = Integer.parseInt(orders.getPrice()) - Integer.parseInt(paid.getText().toString());

                    Orders order = new Orders(
                            name.getText().toString(),
                            orders.getMobile(),
                            orders.getAddress(),
                            orders.getProduct(),
                            orders.getQty(),
                            orders.getPrice(),
                            paid.getText().toString(),
                            Integer.toString(currentbalance),
                            currentDate(),
                            sellerID
                    );

                    ordersRef.child(key).setValue(order);
                    Toast.makeText(getApplicationContext(), "Order updated.", Toast.LENGTH_LONG).show();
                    RefeshAdapter();
                    view.setVisibility(view.GONE);

                } else {
                    Toast.makeText(getApplicationContext(), "Please fill paid  field.", Toast.LENGTH_LONG).show();
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

    public void RefeshAdapter(){
        recycleAdapter = new OrdersRecycleAdapter(this);
        recycleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recycleAdapter);
    };


}
