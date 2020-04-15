package com.example.junior.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;

import com.example.junior.activity.LaunchSimpleActivity;
import com.example.junior.activity.TabHostActivity;
import com.example.junior.util.DateUtil;
import com.example.junior.R;
import com.example.junior.database.UserDBHelper;
import com.example.junior.bean.UserInfo;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {
    private RadioGroup rg_login; // 声明一个单选组对象
    private RadioButton rb_password; // 声明一个单选按钮对象
    private RadioButton rb_verifycode; // 声明一个单选按钮对象
    private EditText et_phone; // 声明一个编辑框对象
    private TextView tv_password; // 声明一个文本视图对象
    private EditText et_password; // 声明一个编辑框对象
    private Button btn_forget; // 声明一个按钮控件对象
    private CheckBox ck_remember; // 声明一个复选框对象
    private Button btn_confirm_login;   //声明一个按钮控件对象
    private Button btn_cancle_login;   //声明一个按钮控件对象
    private UserDBHelper mHelper;   //声明一个用户数据库帮助器对象
    private SharedPreferences mShared;  //声明一个共享参数对象（用于记录用户是否第一次使用该APP） 记录用户手机号


    private int mRequestCode = 0; // 跳转页面时的请求代码
    private boolean bRemember = false; // 是否记住密码，默认为不记住
    private boolean isFirstLogin=true;  //是否为第一次成功登录该APP，默认为是
    private String mPassword="111111";  //默认密码是111111
    private String mVerifyCode; // 验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findIdSetListener();    //初始化控件，注册监听器
        //从share_login.xml中获取共享参数对象
        mShared=getSharedPreferences("share_login",MODE_PRIVATE);
        //获取共享参数中保存的登录成功信息
        isFirstLogin=mShared.getBoolean("first_login",true);

        /*final Context context = LoginActivity.this;            // context
        final String AppKey = "你的 AppKey";            // AppKey
        final String AppSecret = "你的 AppSecret"; // AppSecret

        EventHandler eventHandler = new EventHandler(){    // 操作回调
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);   // 注册回调接口*/
    }

    private void findIdSetListener(){
        //从名叫login的XML布局文件中获取名叫rg_login的单选组控件
        rg_login = findViewById(R.id.rg_login);
        //从名叫login的XML布局文件中获取名叫rb_password的单选按钮控件
        rb_password=findViewById(R.id.rb_password);
        //从名叫login的XML布局文件中获取名叫rb_verifycode的单选按钮控件
        rb_verifycode=findViewById(R.id.rb_verifycode);
        //从名叫login的XML布局文件中获取名叫ck_remember的复选框控件
        ck_remember=findViewById(R.id.ck_remember);
        //从名叫login的XML布局文件中获取名叫btn_forget的按钮控件
        btn_forget=findViewById(R.id.btn_forget);
        //从名叫login的XML布局文件中获取名叫btn_confirm_login的按钮控件
        btn_confirm_login=findViewById(R.id.btn_confirm_login);
        //从名叫login的XML布局文件中获取名叫btn_cancle_login的按钮控件
        btn_cancle_login=findViewById(R.id.btn_cancel_login);
        //从名叫login的XML布局文件中获取名叫et_phone的编辑框控件
        et_phone=findViewById(R.id.et_phone);
        //从名叫login的XML布局文件中获取名叫tv_password的文本框控件
        tv_password=findViewById(R.id.tv_password);
        //从名叫login的XML布局文件中获取名叫et_password的编辑框控件
        et_password=findViewById(R.id.et_password);

        // 给rg_login设置单选监听器
        rg_login.setOnCheckedChangeListener(new RadioListener());
        // 给ck_remember设置勾选监听器
        ck_remember.setOnCheckedChangeListener(new CheckListener());
        //给btn_forget添加点击监听器
        btn_forget.setOnClickListener(this);
        //给btn_confirm_login添加点击监听器
        btn_confirm_login.setOnClickListener(this);
        //给btn_cancle_login添加点击监听器
        btn_cancle_login.setOnClickListener(this);
        //给密码编辑框注册一个焦点变化监听器，一旦焦点发生变化，就触发监听器的onFocusChange方法
        et_password.setOnFocusChangeListener(this);
    }

    protected void onResume(){
        super.onResume();
        //获得用户数据库帮助器的一个实例
        mHelper=UserDBHelper.getInstance(this,2);
        //恢复页面，则打开数据库连接
        mHelper.openWriteLink();
    }

    protected void onPause(){
        super.onPause();
        //暂停页面，则关闭数据库连接
        mHelper.closeLink();
    }

    // 从修改密码页面返回登录页面，要清空密码的输入框
    @Override
    protected void onRestart() {
        et_password.setText("");
        super.onRestart();
    }

    // 从后一个页面携带参数返回当前页面时触发
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mRequestCode && data != null) {
            // 得到手机号码，用户密码已改为新密码，故更新密码变量
            String phone=data.getStringExtra("phone");
            mPassword = data.getStringExtra("new_password");
            et_phone.setText(phone);
            //更新数据库中该手机号的用户信息
            UserInfo info=mHelper.queryByPhone(phone);
            info.setUpdate_Time(DateUtil.getNowDateTime());
            mHelper.updateUserData(info,info.getUpdate_Time());
        }
    }

    // 定义登录方式的单选监听器
    private class RadioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_password) { // 选择了密码登录
                tv_password.setText("登录密码：");
                et_password.setHint("请输入密码");
                btn_forget.setText("忘记密码");
                btn_forget.setTextSize(14);
                ck_remember.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_verifycode) { // 选择了验证码登录
                tv_password.setText("　验证码：");
                et_password.setHint("请输入验证码");
                et_password.setInputType(InputType.TYPE_CLASS_NUMBER);
                btn_forget.setText("获取验证码");
                btn_forget.setTextSize(12);
                ck_remember.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 定义是否记住密码的勾选监听器
    private class CheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.ck_remember) {
                bRemember = isChecked;
            }
        }
    }

    @Override
    public void onClick(View v) {
        String phone = et_phone.getText().toString();
        if (v.getId() == R.id.btn_forget) { // 点击了“忘记密码”按钮
            if (phone.length() != 11) { // 手机号码不是11位
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rb_password.isChecked()) { // 选择了密码方式校验，此时要跳到找回密码页面
                Intent intent = new Intent(this, ForgetPsdActivity.class);
                // 携带手机号码跳转到找回密码页面
                intent.putExtra("phone", phone);
                startActivityForResult(intent, mRequestCode);
            }

        }
        else if (v.getId() == R.id.btn_confirm_login) { // 点击了“登录”按钮
            UserInfo info=mHelper.queryByPhone(phone);
            if(info==null){
                Toast.makeText(this,"该手机号码尚未注册",Toast.LENGTH_SHORT).show();
            }
            else if (phone.length() != 11) { // 手机号码不是11位
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (rb_password.isChecked()) { // 密码方式校验
                if (!et_password.getText().toString().equals(info.getPassword())) {
                    Toast.makeText(this, "请输入正确的密码", Toast.LENGTH_SHORT).show();
                }
                else { // 密码校验通过
                    loginSuccess(); // 提示用户登录成功
                }
            }
            else if (rb_verifycode.isChecked()) { // 验证码方式校验
                if (!et_password.getText().toString().equals(mVerifyCode)) {
                    Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                }
                else { // 验证码校验通过
                    loginSuccess(); // 提示用户登录成功
                }
            }
        }
        else if (v.getId() == R.id.btn_cancel_login){   // 点击了“取消”按钮
            //创建提醒对话框的建造器
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            //给建造器设置对话框的标题文本
            builder.setTitle("请稍等");
            //给建造器设置对话框的信息文本
            builder.setMessage("你真的要取消登录吗？");
            //给建造器设置对话框的肯定按钮文本及其点击监听器
            builder.setPositiveButton("去意已决", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent login_to_mainframe=new Intent(LoginActivity.this, MainFrameActivity.class);
                    startActivity(login_to_mainframe);
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

    //焦点变更事件的处理方法，hasFocus表示当前控件是否获得焦点
    //为什么光标进入密码框事件不选onClick？因为要点两下才会触发onClick动作（第一下是切换焦点）
    public void onFocusChange(View v,boolean hasFocus){
        String phone=et_phone.getText().toString();
        //判断是否是密码编辑框发生焦点变化
        if(v.getId()==R.id.et_password){
            //用户已输入手机号码，且密码框获得焦点，单选按钮选择的是以密码方式登录
            if(phone.length()>0 && phone.length()==11 && hasFocus && rb_password.isChecked()){
                //根据手机号码到数据库中查询用户记录
                UserInfo info=mHelper.queryByPhone(phone);
                if(info!=null){
                    //找到用户记录，则自动在密码框中填写该用户的密码
                    et_password.setText(info.getPassword());
                }
            }
        }
    }

    // 校验通过，登录成功
    private void loginSuccess() {
        //如果勾选了“记住密码”，则把手机号码和密码，还有登录时间保存为数据库的用户表记录
        if(bRemember){
            //创建一个用户信息实体类
            UserInfo info=new UserInfo();
            info.setPhone(et_phone.getText().toString());
            info.setPassword(et_password.getText().toString());
            info.setUpdate_Time(DateUtil.getNowDateTime());
            //往用户数据库添加登录成功的用户信息（包含手机号码、密码、登录时间）
            mHelper.updateUserData(info,info.getUpdate_Time());
        }
        // 弹出提醒文字浮窗，提示用户登录成功
        Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
        //更改共享参数的值
        SharedPreferences.Editor editor=mShared.edit(); //获得编辑器的对象
        editor.putBoolean("first_login",false); //添加名叫first_login的布尔值
        editor.commit();    //提交编辑器的修改
        //控制显示引导页
        showLaunchPage();
    }

    //负责控制是否显示第一次成功登录的引导页
    private void showLaunchPage(){
        //能到达这里说明这个手机号是存在的
        //将手机号储存起来
        SharedPreferences.Editor editor=mShared.edit(); //获得编辑器的对象
        editor.putString("user_phone", et_phone.getText().toString()); //添加登录用户的手机号
        editor.commit();    //提交编辑器的修改

        if(isFirstLogin==true){
            //如果是首次成功登录，就前往引导页面
            Intent login_to_launch=new Intent(LoginActivity.this, LaunchSimpleActivity.class);
            startActivity(login_to_launch);
        }
        else {
            //非首次登录用户将会去到功能页面
            Intent login_to_tabhost=new Intent(LoginActivity.this, TabHostActivity.class);
            startActivity(login_to_tabhost);
        }
    }
}
