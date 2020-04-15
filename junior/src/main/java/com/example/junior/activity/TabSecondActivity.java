package com.example.junior.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.junior.R;
import com.example.junior.discussion.Discuss;

public class TabSecondActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabsecond);
        Button button = findViewById(R.id.start_discuss);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TabSecondActivity.this, Discuss.class);
                startActivity(intent);
            }
        });
    }
}
