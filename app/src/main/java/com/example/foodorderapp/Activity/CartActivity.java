package com.example.foodorderapp.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;


import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.foodorderapp.Adapter.CartAdapter;

import com.example.foodorderapp.Helper.ChangeNumberItemsListener;
import com.example.foodorderapp.Helper.ManagmentCart;

import com.example.foodorderapp.databinding.ActivityCartBinding;

public class CartActivity extends BaseActivity implements PaymentResultListener {
    ActivityCartBinding binding;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart=new ManagmentCart(this);

        setVariable();
        calculateCart();
        initCartList();
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), managmentCart, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        }));
    }

    private void calculateCart() {
        double percenTax=0.02;
        double delivery=10;
        double tax=Math.round(managmentCart.getTotalFee()*percenTax*100.0)/100;
        double total=Math.round((managmentCart.getTotalFee()+tax+delivery)*100)/100;
        double itemTotal=Math.round(managmentCart.getTotalFee()*100)/100;

        binding.totalFeeTxt.setText("$"+itemTotal);
        binding.taxTxt.setText("$"+tax);
        binding.deliveryTxt.setText("$"+delivery);
        binding.totalTxt.setText("$"+total);
    }

    private void setVariable(){
        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(CartActivity.this,MainActivity.class)));

        binding.checkOutBtn.setOnClickListener(v -> startPayment());


    }

//payment API
    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_sg_1ubWYVhQAqMHHn"); // Replace with your Razorpay Key ID

        // Customize the payment UI
        //checkout.setImage(R.drawable.your_logo); // Optional: Add your app logo

        // Create payment details
        JSONObject options = new JSONObject();
        try {
            options.put("name", "FoodOrderApp");
            options.put("description", "Order Payment");
            options.put("currency", "INR");
            options.put("amount", (managmentCart.getTotalFee() + 10) * 100); // Convert to paise (INR)

            JSONObject prefill = new JSONObject();
            prefill.put("email", "sandunsanchitha@gmail.com"); // Replace with the user's email
            prefill.put("contact", "760651573"); // Replace with the user's phone number
            options.put("prefill", prefill);

            checkout.open(this, options); // Start Razorpay Checkout
        } catch (Exception e) {
            Log.e("RazorpayError", "Error in starting payment", e);
        }
    }


    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful! Payment ID: " + razorpayPaymentID, Toast.LENGTH_LONG).show();
        // Update order status or save payment details to Firebase
        Log.i("PaymentSuccess", "Payment ID: " + razorpayPaymentID);
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
        Log.e("PaymentError", "Code: " + code + ", Response: " + response);
    }

}