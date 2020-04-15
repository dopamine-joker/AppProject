package com.example.junior.Me;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junior.Base.BaseActivity;
import com.example.junior.R;
import com.example.junior.bean.UserInfo;
import com.example.junior.database.UserDBHelper;

public class Info_Change_Activity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Info_Change_Activity";


    private UserInfo info;                      //得到该用户的用户类
    private UserDBHelper mHelper;               //声明一个用户数据库帮助器对象
    private SharedPreferences mShared;          //声明一个共享参数对象  记录用户手机号

    private ImageView user_img_edit;
    private EditText user_name_edit;            //用户名编辑框
    private TextView user_gender;               //性别选择框
    private Button save_button;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_change);
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
        setupBackAdUp("修改信息");
        user_name_edit=findViewById(R.id.user_name_edit);
        user_gender=findViewById(R.id.user_gender_edit);
        user_img_edit=findViewById(R.id.user_img_edit);
        save_button=findViewById(R.id.save_button);
        user_gender.setOnClickListener(this);                       //性别设置点击监听
        user_img_edit.setOnClickListener(this);                     //头像设置点击监听
        save_button.setOnClickListener(this);                       //保存按钮设置监听
        //初始化信息
        user_name_edit.setText(info.getUserName());
        user_gender.setText("男");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //修改头像
            case R.id.user_img_edit:
                Toast.makeText(Info_Change_Activity.this,"You want to change your profile photo",Toast.LENGTH_SHORT).show();
                break;
            //昵称本身就是一个EditView，不用设置点击监听
            //点击性别弹出选项框
            case R.id.user_gender_edit:
                new AlertDialog.Builder(this)
                        .setTitle("性别")
                        .setSingleChoiceItems(new String[]{"男", "女"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        user_gender.setText("男");
                                        info.setGender("男");
                                        break;
                                    case 1:
                                        user_gender.setText("女");
                                        info.setGender("女");
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
                break;
                //修改确定按钮
            case R.id.save_button:
                info.setUserName(user_name_edit.getText().toString());
                Toast.makeText(Info_Change_Activity.this,"SETBUTTON_Onclick",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
