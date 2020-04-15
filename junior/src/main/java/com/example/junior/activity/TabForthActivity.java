package com.example.junior.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.junior.Me.Account_Security;
import com.example.junior.Me.SettingActivity;
import com.example.junior.Me.User_Info_Activity;
import com.example.junior.R;
import com.example.junior.bean.UserInfo;
import com.example.junior.database.UserDBHelper;

public class TabForthActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;          //得到头像，用户名所在的TextView
    private UserInfo info;              //得到该用户的用户类
    private UserDBHelper mHelper;       //声明一个用户数据库帮助器对象
    private SharedPreferences mShared;  //声明一个共享参数对象  记录用户手机号
    private String phone;               //得到登录用户的手机号码
    private Button Setting_button;      //设置按钮
    private Button Info_change_button;  //信息修改按钮
    private Button Account_Security_button; //账号安全按钮
    Intent intent;                      //构建意图

    private static final String TAG = "TabForthActivity";

    //在TabHost中，这个函数只会调用一次
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabforth);
        //打开共享参数编辑器
        mShared=getSharedPreferences("share_login",MODE_PRIVATE);
        //获得登录用户的手机号，它的设置相关代码可以在LoginActivity中找到
        phone=mShared.getString("user_phone","");
        //打开数据库链接,并获得实例
        mHelper=UserDBHelper.getInstance(this,2);
        mHelper.openWriteLink();
        //根据登录用户手机号，从数据库中获得该用户类
        info=mHelper.queryByPhone(phone);
        Log.d(TAG, "onCreate: "+ info.getUserName());
        //调用数据初始化函数
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //系统准备去启动其他活动时调用此函数，关闭数据库连接
        Log.d(TAG, "onPause: 数据库连接关闭");
        mHelper.closeLink();
    
    }

    private void initData(){
        Setting_button=findViewById(R.id.setting_button);               //获得设置按钮实例
        Setting_button.setOnClickListener(this);                        //为设置按钮设置点击监听器
        Info_change_button=findViewById(R.id.user_info_button);         //获得信息修改按钮
        Info_change_button.setOnClickListener(this);                    //设置监听器
        Account_Security_button=findViewById(R.id.Account_security_button); //得到账号安全按钮实例
        Account_Security_button.setOnClickListener(this);               //设置监听器
        textView=findViewById(R.id.user_name);                          //得到最上边显示用户名的textView
        textView.setText(info.getUserName());                           //设置该用户的用户名

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_button:
                Log.d(TAG, "onClick: setting_button()");
                intent=new Intent(TabForthActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.user_info_button:
                Log.d(TAG, "onClick: user_info_button()");
                intent=new Intent(TabForthActivity.this, User_Info_Activity.class);
                startActivity(intent);
                break;
            case R.id.Account_security_button:
                Log.d(TAG, "onClick: Account_security_button()");
                intent=new Intent(TabForthActivity.this, Account_Security.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
