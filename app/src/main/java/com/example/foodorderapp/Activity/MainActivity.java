package com.example.foodorderapp.Activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foodorderapp.Adapter.CategoryAdapter;
import com.example.foodorderapp.Adapter.SliderAdapter;
import com.example.foodorderapp.Domain.Category;
import com.example.foodorderapp.Domain.SliderItems;
import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private FirebaseDatabase database;
    private final String GOOGLE_SEARCH_API = "https://www.googleapis.com/customsearch/v1";
    private final String API_KEY = "AIzaSyDPA--uEgdGbrtfJsFaZEbVuVHFK8_-Bek"; // Replace with your Google API Key
    private final String CX = "70e87f71adcdd4857"; // Replace with your Custom Search Engine ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use ViewBinding for layout inflation
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Use binding.getRoot()

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG); // Enable Firebase debug logging

        // Initialize category data
        initCategory();
        initBanner();
        setVariable();

        // Set onClickListener for banners
        binding.web.setOnClickListener(v -> fetchPizzaSearchResults());
    }

    private void fetchPizzaSearchResults() {
        String query = "Hot pizza"; // Search query
        String url = GOOGLE_SEARCH_API + "?key=" + API_KEY + "&cx=" + CX + "&q=" + query;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle API response
                    Toast.makeText(MainActivity.this, "Search Successful!", Toast.LENGTH_SHORT).show();

                    // Optionally parse JSON response and display results
                    // For now, launch Google with the search query
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + query));
                    startActivity(browserIntent);
                },
                error -> {
                    // Handle API error
                    Toast.makeText(MainActivity.this, "Search Failed!", Toast.LENGTH_SHORT).show();
                    Log.e("GoogleAPIError", error.toString());
                });

        requestQueue.add(stringRequest);
    }

    private void initBanner() {
        DatabaseReference myRef=database.getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items=new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue:snapshot.getChildren()){
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banners(ArrayList<SliderItems> items){
        binding.viewpager2.setAdapter(new SliderAdapter(items,binding.viewpager2));
        binding.viewpager2.setClipChildren(false);
        binding.viewpager2.setClipToPadding(false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpager2.setPageTransformer(compositePageTransformer);
    }
    private void setVariable() {

        binding.bottomMenu.setItemSelected(R.id.home,true);
        binding.bottomMenu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (i==R.id.cart){
                    startActivity(new Intent(MainActivity.this,CartActivity.class));
                }
                if (i==R.id.profile){
                    startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                }
            }
        });
    }

    private void initCategory() {
        // Reference to the "Category" node in Firebase Database
        DatabaseReference myRef = database.getReference("Category");

        // Show progress bar while loading data
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        ArrayList<Category> list = new ArrayList<>();

        // Add a listener for single-time data retrieval
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class)); // Add categories to list
                    }

                    if (!list.isEmpty()) {
                        // Set up RecyclerView with GridLayout
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                        binding.categoryView.setAdapter(new CategoryAdapter(list));
                    }

                    // Hide progress bar once data is loaded
                    binding.progressBarCategory.setVisibility(View.GONE);
                } else {
                    // Hide progress bar if no data exists
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log database errors
                System.out.println("Firebase Database Error: " + error.getMessage());
                binding.progressBarCategory.setVisibility(View.GONE); // Hide progress bar
            }
        });
    }

}
