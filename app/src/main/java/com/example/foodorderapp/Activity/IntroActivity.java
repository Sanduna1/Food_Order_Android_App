package com.example.foodorderapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.foodorderapp.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {
ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntroActivity.this.startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });

    }
}