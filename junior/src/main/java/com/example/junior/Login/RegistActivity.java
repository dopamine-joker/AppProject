package com.example.junior.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.junior.R;
import com.example.junior.database.UserDBHelper;
import com.example.junior.bean.UserInfo;

public class RegistActivity extends AppCompatActivity {
    private EditText et_enternewnum;    //手机号码编辑
    private EditText et_enternewname;   //用户名编辑
    private EditText et_enternewpsd;    //密码编辑
    private EditText et_enterconfirmpsd;//再次输入密码编辑
    private Button btn_confirm_regist;  //“确定”按钮
    private Button btn_cancel_regist;   //“取消”按钮
    private UserDBHelper mUserDBHelper; //用户数据管理类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);

        et_enternewnum = findViewById(R.id.et_enternewnum);
        et_enternewname = findViewById(R.id.et_enternewname);
        et_enternewpsd = findViewById(R.id.et_enternewpsd);
        et_enterconfirmpsd = findViewById(R.id.et_enterconfirmpsd);
        btn_confirm_regist = findViewById(R.id.btn_confirm_regist);
        btn_cancel_regist = findViewById(R.id.btn_cancel_regist);

        //注册界面两个按钮的监听事件
        btn_confirm_regist.setOnClickListener(new MyOnClickListener_Regist());
        btn_cancel_regist.setOnClickListener(new MyOnClickListener_Regist());

        //建立本地数据库
        if (mUserDBHelper == null) {
            mUserDBHelper = UserDBHelper.getInstance(this, 2);
            mUserDBHelper.openReadLink();
        }
    }

    class MyOnClickListener_Regist implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_confirm_regist) { //确认注册按钮的监听事件
                register_check();
            }
            if (v.getId()==R.id.btn_cancel_regist){    //取消注册按钮的监听事件
                //创建提醒对话框的建造器
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistActivity.this);
                //给建造器设置对话框的标题文本
                builder.setTitle("请稍等");
                //给建造器设置对话框的信息文本
                builder.setMessage("你真的要取消注册吗？");
                //给建造器设置对话框的肯定按钮文本及其点击监听器
                builder.setPositiveButton("去意已决", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent regist_to_mainframe=new Intent(RegistActivity.this, MainFrameActivity.class);
                        startActivity(regist_to_mainframe);
                    }
                });
                builder.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRestart();
                    }
                });
                //根据建造器完成提醒对话框对象的构建
                AlertDialog alert=builder.create();
                //在界面上显示对话框
                alert.show();
            }
        }
    }

    //确认按钮的监听事件
    public void register_check() {
        if (isUserNameAndPwdValid()) {  //检查各项是否被填写好
            String phone = et_enternewnum.getText().toString().trim();
            String userName = et_enternewname.getText().toString().trim();
            String userPwd = et_enternewpsd.getText().toString().trim();
            String userPwdCheck = et_enterconfirmpsd.getText().toString().trim();
            //通过手机号检查用户是否存在
            UserInfo info = mUserDBHelper.queryByPhone(phone);
            //用户已经存在时返回，给出提示文字
            if (info != null) {
                Toast.makeText(this, "该用户已经存在", Toast.LENGTH_SHORT).show();
                return;
            }
            //用户还不存在，新建用户
            //假如输入的手机号码不够11位
            if(phone.length()!=11){
                Toast.makeText(this, "输入的手机号码非11位，请重新输入", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userPwd.equals(userPwdCheck) == false) {     //两次密码输入不一样
                Toast.makeText(this, "两次输入的密码不一样，请重新确认密码", Toast.LENGTH_SHORT).show();
                return;
            }
            //新建用户，两次输入的密码相同
            else {
                UserInfo mUser = new UserInfo(phone, userName, userPwd);
                mUserDBHelper.openWriteLink();
                long flag = mUserDBHelper.insertUserData(mUser); //新建用户信息
                if (flag == -1) {
                    Toast.makeText(this, "注册失败，请再次检查所填信息", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
                    Intent intent_Register_to_Login = new Intent(RegistActivity.this, MainFrameActivity.class);    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                }
            }
        }
    }
    public boolean isUserNameAndPwdValid() {
        if (et_enternewnum.getText().toString().trim().equals("")) {
            Toast.makeText(this,"手机号码不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_enternewpsd.getText().toString().trim().equals("")) {
            Toast.makeText(this,"密码不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(et_enterconfirmpsd.getText().toString().trim().equals("")) {
            Toast.makeText(this,"必须重新输入密码",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (et_enternewname.getText().toString().trim().equals("")) {
            Toast.makeText(this,"用户名不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

