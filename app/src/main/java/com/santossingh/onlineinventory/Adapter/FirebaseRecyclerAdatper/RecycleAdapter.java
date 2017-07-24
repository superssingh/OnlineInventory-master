package com.santossingh.onlineinventory.Adapter.FirebaseRecyclerAdatper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santossingh.onlineinventory.Models.Inventory;
import com.santossingh.onlineinventory.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshsingh on 10/02/17.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.Holder>{

    private List<Inventory> inventories;
    private Inventory currentData;
    private View rcView;
    private GetDataFromAdapter callback;
    private DatabaseReference inventoryRef;

    public RecycleAdapter(GetDataFromAdapter callback) {
        this.callback = callback;
        inventories = new ArrayList<>();
        inventoryRef = FirebaseDatabase.getInstance().getReference().child("inventory");

        inventoryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Inventory inventory = dataSnapshot.getValue(Inventory.class);
                inventory.setKey(dataSnapshot.getKey());
                inventories.add(0, inventory);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Inventory inventory = dataSnapshot.getValue(Inventory.class);
                for (Inventory item : inventories) {
                    if (item.equals(key)) {
                        item.setValues(inventory);
                        break;
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Inventory item : inventories) {
                    if (item.equals(key)) {
                        inventories.remove(item);
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
                .inflate(R.layout.list_item, parent, false);
        return new Holder(rcView, callback);
    }

    @Override
    public int getItemCount() {
        return inventories.size();
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Inventory current = inventories.get(position);
        holder.product.setText(current.getItem());
        holder.qty.setText(current.getQuantity());
        holder.price.setText(current.getPrice());
    }

    public interface GetDataFromAdapter {
        void onCurrentMovie(Inventory currentData);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView product, qty, price;
        private GetDataFromAdapter callback;

        public Holder(View itemView, GetDataFromAdapter callback) {
            super(itemView);
            this.callback=callback;
            itemView.setOnClickListener(this);
            product=(TextView) itemView.findViewById(R.id.R_product);
            qty=(TextView)itemView.findViewById(R.id.R_qty);
            price=(TextView)itemView.findViewById(R.id.R_price);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            currentData = inventories.get(position);
            callback.onCurrentMovie(currentData);
        }
    }


}
