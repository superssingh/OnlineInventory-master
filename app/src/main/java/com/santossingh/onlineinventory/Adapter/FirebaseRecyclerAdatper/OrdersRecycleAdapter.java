package com.santossingh.onlineinventory.Adapter.FirebaseRecyclerAdatper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Models.Orders;
import com.santossingh.onlineinventory.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshsingh on 10/02/17.
 */

public class OrdersRecycleAdapter extends RecyclerView.Adapter<OrdersRecycleAdapter.Holder> {

    private List<Orders> ordersList;
    private Orders currentData;
    private View rcView;
    private GetDataFromAdapter callback;
    private DatabaseReference ordersRef, inventroyRef;

    public OrdersRecycleAdapter(GetDataFromAdapter callback) {
        this.callback = callback;
        ordersList = new ArrayList<>();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        inventroyRef = FirebaseDatabase.getInstance().getReference().child("inventory");
        ordersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Orders orders = dataSnapshot.getValue(Orders.class);
                orders.setKey(dataSnapshot.getKey());
                ordersList.add(0, orders);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Orders orders = dataSnapshot.getValue(Orders.class);
                for (Orders item : ordersList) {
                    if (item.equals(key)) {
                        item.setValues(orders);
                        break;
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Orders item : ordersList) {
                    if (item.equals(key)) {
                        ordersList.remove(item);
                        break;
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // create View object and pass it on Holder class constructor
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        rcView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order, parent, false);
        return new Holder(rcView, callback);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Orders current = ordersList.get(position);
        holder.buyer.setText(current.getBuyer());
        holder.product.setText("Product: " + current.getProduct());
        holder.qty.setText("Quantity: " + current.getQty());
        holder.price.setText("Billing Amount: " + current.getPrice());
        holder.balance.setText("Balance: " + current.getBalance());
    }

    private void removeData(Orders orders, int position) {
        ordersRef.child(orders.getKey()).removeValue();
        ordersList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ordersList.size());
        notifyDataSetChanged();
    }

    public interface GetDataFromAdapter {
        void onCurrentMovie(Orders currentData);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView buyer, product, qty, price, balance;
        private Button Delete;
        private GetDataFromAdapter callback;

        public Holder(View itemView, GetDataFromAdapter callback) {
            super(itemView);
            this.callback = callback;
            itemView.setOnClickListener(this);
            buyer = (TextView) itemView.findViewById(R.id.O_buyer);
            product = (TextView) itemView.findViewById(R.id.O_product);
            qty = (TextView) itemView.findViewById(R.id.O_qty);
            price = (TextView) itemView.findViewById(R.id.O_price);
            balance = (TextView) itemView.findViewById(R.id.O_balance);
            Delete = (Button) itemView.findViewById(R.id.O_delete);

            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    currentData = ordersList.get(position);
                    removeData(currentData, position);
                }
            });

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            currentData = ordersList.get(position);
            callback.onCurrentMovie(currentData);
        }
    }

}
