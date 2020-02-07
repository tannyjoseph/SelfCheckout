package com.g.barc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.g.barc.Adapters.ItemsAdapter;
import com.g.barc.Classes.Items;
import com.g.barc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Items> list;
    ItemsAdapter adapter;
    Button pay;
    float sum;
    static String price_t;
    TextView tprice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.recycler_view);
        pay = findViewById(R.id.pay);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tprice = findViewById(R.id.tprice);

        final NumberFormat formatter = new DecimalFormat("#0.00");


        list = new ArrayList<Items>();

        DatabaseReference bd = FirebaseDatabase.getInstance().getReference().child("Items");

        bd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 sum = 0;


                for(DataSnapshot child : dataSnapshot.getChildren()){
                    System.out.println("hey" + child.getValue());

                    Items items1 = child.getValue(Items.class);
                    System.out.println("ka"+ items1.getItemname());


                    String name = items1.getItemname();
                    String price = items1.getPrice();
                    String upc = items1.getUpc();

                    String p = price.substring(1);

                    sum += Float.valueOf(p);

                    Items data = new Items(name,  upc, price);

                    list.add(data);

                    adapter = new ItemsAdapter(list, Cart.this);
                    recyclerView.setAdapter(adapter);
                }

                System.out.println(list);
                price_t = formatter.format(sum);

                tprice.setText("Total Price: " + price_t);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Cart.this, Payment.class));
            }
        });


    }
}
