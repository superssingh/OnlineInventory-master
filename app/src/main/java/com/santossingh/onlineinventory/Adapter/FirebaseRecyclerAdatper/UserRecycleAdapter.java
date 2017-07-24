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
import com.santossingh.onlineinventory.Models.Sellers;
import com.santossingh.onlineinventory.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santoshsingh on 10/02/17.
 */

public class UserRecycleAdapter extends RecyclerView.Adapter<UserRecycleAdapter.Holder> {

    private List<Sellers> sellersList;
    private Sellers sellers;
    private View rcView;
    private GetDataFromAdapter callback;
    private DatabaseReference sellerRef;


    public UserRecycleAdapter(GetDataFromAdapter callback) {
        this.callback = callback;
        sellersList = new ArrayList<>();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("sellers");
        sellerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Sellers sellers = dataSnapshot.getValue(Sellers.class);
                sellers.setKey(dataSnapshot.getKey());
                sellersList.add(0, sellers);
                notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Sellers user : sellersList) {
                    if (user.equals(key)) {
                        sellersList.remove(user);
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

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        rcView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user, parent, false);
        return new Holder(rcView, callback);
    }

    @Override
    public int getItemCount() {
        return sellersList.size();
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Sellers seller = sellersList.get(position);
        holder.user.setText(seller.getUsername());
        holder.mobile.setText("Mobile: " + seller.getMobile());
        holder.password.setText("Password: " + seller.getPassword());
    }

    private void removeData(Sellers sellers, int position) {
        sellerRef.child(sellers.getKey()).removeValue();
        sellersList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, sellersList.size());
        notifyDataSetChanged();
    }

    public interface GetDataFromAdapter {
        void onCurrentUser(Sellers seller);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button delete;
        private TextView user, mobile,password;
        private GetDataFromAdapter callback;

        public Holder(View itemView, GetDataFromAdapter callback) {
            super(itemView);
            this.callback = callback;
            itemView.setOnClickListener(this);
            user = (TextView) itemView.findViewById(R.id.u_name);
            mobile = (TextView) itemView.findViewById(R.id.u_mobile);
            password = (TextView) itemView.findViewById(R.id.u_pwd);
            delete = (Button) itemView.findViewById(R.id.delete_user);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    sellers = sellersList.get(position);
                    removeData(sellers, position);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            sellers = sellersList.get(position);
            callback.onCurrentUser(sellers);
        }
    }

}
