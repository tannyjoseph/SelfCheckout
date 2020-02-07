package com.g.barc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g.barc.Classes.Items;
import com.g.barc.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {

    TextView name, brand, price;
    ImageView imageView;
    Button checkout, add;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        final String sname, sbrand ,sprice, supc;

        name = findViewById(R.id.name);
        brand = findViewById(R.id.brand_name);
        price = findViewById(R.id.price);
        imageView = findViewById(R.id.image);
        checkout = findViewById(R.id.checkout);
        add = findViewById(R.id.add);

        sname = intent.getStringExtra("name");
        sbrand = intent.getStringExtra("brand_name");
        sprice = intent.getStringExtra("price");
        supc = intent.getStringExtra("upc");

        name.setText(sname);
        brand.setText(sbrand);
        price.setText(sprice);

        if (!(intent.getStringExtra("image").equals(""))){
            Picasso.with(getApplicationContext())
                    .load(intent.getStringExtra("image"))
                    .into(imageView);
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Details.this, Cart.class));
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Items items = new Items(sname,supc, sprice);
                db.child("Items").push().setValue(items);
            }
        });

    }
}
