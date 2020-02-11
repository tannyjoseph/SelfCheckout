package com.g.barc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g.barc.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class Payment extends AppCompatActivity implements PaymentResultListener {
    Button buttonConfirmOrder;
    EditText editTextPayment;
    DatabaseReference bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        findViews();
        listeners();

    }

    public void findViews() {
        buttonConfirmOrder = (Button) findViewById(R.id.buttonConfirmOrder);
        editTextPayment = (EditText) findViewById(R.id.editTextPayment);
        editTextPayment.setText("₹" + String.valueOf(Cart.price_t));
    }

    public void listeners() {


        buttonConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextPayment.getText().toString().equals(""))
                {
                    Toast.makeText(Payment.this, "Please fill payment", Toast.LENGTH_LONG).show();
                    return;
                }
                startPayment();
            }
        });
    }


    public void startPayment() {

        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "Demoing Charges");
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");

            String payment = editTextPayment.getText().toString();

            double total = Double.parseDouble(payment.substring(1));
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "sikander@gkmit.co");
            preFill.put("contact", "9680224241");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment successfully done! " + s, Toast.LENGTH_SHORT).show();

        bd = FirebaseDatabase.getInstance().getReference().child("Items");

        bd.removeValue();

        startActivity(new Intent(Payment.this, MainActivity.class));

    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }
}
