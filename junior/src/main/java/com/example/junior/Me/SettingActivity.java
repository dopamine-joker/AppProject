package com.example.junior.Me;

import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

import com.example.junior.Base.BaseActivity;
import com.example.junior.R;

public class SettingActivity extends BaseActivity {

    private Toolbar toolbar;                    //标题栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //初始化数据
        iniData();
    }

    private void iniData(){
        //修改标题栏相关信息
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupBackAdUp("设置");
    }
}
