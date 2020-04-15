package com.example.junior.Me;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.junior.Base.BaseActivity;
import com.example.junior.R;
import com.example.junior.bean.UserInfo;
import com.example.junior.database.UserDBHelper;

public class Account_Security_change extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Account_Security_change";
    
    private String Choose;              //参数，根据参数选择显示什么界面
    private Intent intent;              //Intent意图，用来接收上一个活动传递过来的参数

    private UserInfo info;                      //得到该用户的用户类
    private UserDBHelper mHelper;               //声明一个用户数据库帮助器对象
    private SharedPreferences mShared;          //声明一个共享参数对象  记录用户手机号
    private String phone;
    private Button save_button;                 //保存按钮

    //以下为修改密码界面变量声明
    private EditText editText1;         //初始密码框
    private EditText editText2;         //新密码框
    private EditText editText3;         //再次确认框
    //修改密码界面变量声明结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent=getIntent();
        Choose=intent.getStringExtra("choose");
        assert Choose != null;
        switch (Choose){
            //如果是选择了修改密码，则加载修改密码界面布局
            case "password":
                setContentView(R.layout.password_change);
                break;
            default:
                setContentView(R.layout.activity_account_security_change);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //打开共享参数编辑器
        mShared=getSharedPreferences("share_login",MODE_PRIVATE);
        //获得登录用户的手机号，它的设置相关代码可以在LoginActivity中找到
        phone=mShared.getString("user_phone","");
        //打开数据库链接,并获得实例
        mHelper=UserDBHelper.getInstance(this,2);
        mHelper.openWriteLink();
        //根据登录用户手机号，从数据库中获得该用户类
        info=mHelper.queryByPhone(phone);
        iniData();
    }

    private void iniData(){
        //修改标题栏相关信息
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(Choose.equals("password")){
            setupBackAdUp("修改密码");
        }else if(Choose.equals("phone")){

        }

        save_button=findViewById(R.id.save_button);                     //得到保存按钮实例
        //为保存按钮设计点击事件
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Choose.equals("password")){
                    editText1=findViewById(R.id.original_password);
                    editText2=findViewById(R.id.new_password);
                    editText3=findViewById(R.id.new_password_check);
                    //检查原密码输入是否正确
                    if(!info.getPassword().equals(editText1.getText().toString())){
                        new AlertDialog.Builder(Account_Security_change.this)
                                .setTitle("错误")
                                .setMessage("原密码错误")
                                .setPositiveButton("确定", null)
                                .show();
                        return ;
                    }
                    //确认新密码与确认新密码是否一致
                    if(!editText2.getText().toString().equals(editText3.getText().toString())){
                        new AlertDialog.Builder(Account_Security_change.this)
                                .setTitle("错误")
                                .setMessage("新密码输入不一致")
                                .setPositiveButton("确定", null)
                                .show();
                        return ;
                    }
                    //新密码不能位空
                    if(editText2.getText().toString().equals("")){
                        new AlertDialog.Builder(Account_Security_change.this)
                                .setTitle("错误")
                                .setMessage("新密码不能为空")
                                .setPositiveButton("确定", null)
                                .show();
                        return ;
                    }
                    info.setPassword(editText2.getText().toString());
                    Log.d(TAG, "成功!!!!");
                    //修改成功，并提示
                    new AlertDialog.Builder(Account_Security_change.this)
                            .setTitle("成功")
                            .setMessage("密码修改成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }else if(Choose.equals("phone")){

                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
