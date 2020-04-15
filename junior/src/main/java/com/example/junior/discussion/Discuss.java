package com.example.junior.discussion;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.junior.Base.BaseActivity;
import com.example.junior.R;


public class Discuss extends BaseActivity {
    private static final String TAG = "Discuss";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discuss);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewSetListener();
        iniData();
    }

    private void findViewSetListener(){

    }

    private void iniData(){
        //修改标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                                  //加载Toolbar控件
        setupBackAdUp("讨论吧");                                       //由于活动是继承了BaseActivity,里面有setupBackAdUp方法

    }


}
