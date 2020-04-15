package com.example.junior.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.junior.R;
import com.example.junior.activity.LaunchSimpleActivity;
import com.example.junior.activity.TabHostActivity;
import com.example.junior.bean.UserInfo;
import com.example.junior.database.DbConnector;
import com.example.junior.database.UserDBHelper;
import com.example.junior.util.DateUtil;
import com.example.junior.util.MenuUtil;

import java.sql.Connection;

public class MainFrameActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {
    private static final String TAG = "MainFrameActivity";
    private String temp="";                        //暂存字符串,暂时保存输入框的内容
    private Button login_click;
    private Button regist_click;
    private Toolbar tl_head;

    private Button get_verifycode;  //获取验证码按钮
    private RadioGroup rg_login; // 声明一个单选组对象
    private RadioButton rb_password; // 声明一个单选按钮对象
    private RadioButton rb_verifycode; // 声明一个单选按钮对象
    private EditText et_phone; // 声明一个编辑框对象
 //   private TextView tv_password; // 声明一个文本视图对象
    private EditText et_password; // 声明一个编辑框对象
    private Button btn_forget; // 声明一个按钮控件对象
    private CheckBox ck_remember; // 声明一个复选框对象
    private Button btn_cancle_login;   //声明一个按钮控件对象
    private UserDBHelper mHelper;   //声明一个用户数据库帮助器对象
    private SharedPreferences mShared;  //声明一个共享参数对象（用于记录用户是否第一次使用该APP） 记录用户手机号
    private SharedPreferences.Editor editor;    //共享参数编辑器对象

    private int mRequestCode = 0; // 跳转页面时的请求代码
    private boolean bRemember = false; // 是否记住密码，默认为不记住
    private boolean isFirstLogin=true;  //是否为第一次成功登录该APP，默认为是
    private String mPassword="111111";  //默认密码是111111
    private String mVerifyCode="111111"; // 验证码
    private String last_login_phone;        //最后一个登录的用户的手机号码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainframe);
        //调用findViewSetListener()设置按钮控件
        findViewSetListener();
        //初始化参数函数
        initData();
        //调用setToolbar()设置工具栏
 //       setToolbar();
    }

    private void initData(){
        mHelper=UserDBHelper.getInstance(this,2);                   //获得用户数据库帮助器的一个实例
        mHelper.openWriteLink();                                                    //恢复页面，则打开数据库连接
        mShared=getSharedPreferences("share_login",MODE_PRIVATE);            //从share_login.xml中获取共享参数对象
        editor=mShared.edit();                                                      //获取共享参数中保存的登录成功信息
        isFirstLogin=mShared.getBoolean("first_login",true);        //获取共享参数中是否保存密码
        bRemember=mShared.getBoolean("bRemember",false);            //读取，如果用户先前选择了记住密码，则打勾
        last_login_phone=mShared.getString("last_login_phone","");  //读取本机最后一位登录的用户的手机号码
        get_verifycode.setVisibility(View.INVISIBLE);                               //默认是密码登录，获取验证码按钮不显示
        if(!last_login_phone.equals("")){                                           //如果非空,则自动填入手机号码
            et_phone.setText(last_login_phone);
        }
        if(bRemember){                                                              //如果之前已经有选择过记录密码，则记住密码复选框打勾,并且如果记住最后一次登录用户的密码
            ck_remember.setChecked(true);
            String phone = et_phone.getText().toString();                           //获得手机号码
            Log.d(TAG, "initData: "+phone);
            UserInfo info=mHelper.queryByPhone(phone);                              //获取用户
            if(info!=null){                                                         //如何非空
                et_password.setText(info.getPassword());                            //自动填入密码
                temp=et_password.getText().toString().trim();                       //暂存字符
            }
        }else{
            ck_remember.setChecked(false);
        }
    }

    private void findViewSetListener(){
        get_verifycode=findViewById(R.id.get_verifycode);
        //从名叫mainframe的XML布局文件中获取名叫login_click的按钮控件
        login_click = findViewById(R.id.login_click);
        //从名叫mainframe的XML布局文件中获取名叫regist_click的按钮控件
        regist_click = findViewById(R.id.regist_click);
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
        //从名叫login的XML布局文件中获取名叫btn_cancle_login的按钮控件
        btn_cancle_login=findViewById(R.id.btn_cancel_login);
        //从名叫login的XML布局文件中获取名叫et_phone的编辑框控件
        et_phone=findViewById(R.id.et_phone);
        //从名叫login的XML布局文件中获取名叫tv_password的文本框控件
    //    tv_password=findViewById(R.id.tv_password);
        //从名叫login的XML布局文件中获取名叫et_password的编辑框控件
        et_password=findViewById(R.id.et_password);


        // 给rg_login设置单选监听器
        rg_login.setOnCheckedChangeListener(new MainFrameActivity.RadioListener());
        // 给ck_remember设置勾选监听器
        ck_remember.setOnCheckedChangeListener(new MainFrameActivity.CheckListener());
        // 给获取验证码按钮设置监听器
        get_verifycode.setOnClickListener(this);
        //给btn_forget添加点击监听器
        btn_forget.setOnClickListener(this);
        //设置下划线
        btn_forget.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        //给密码编辑框注册一个焦点变化监听器，一旦焦点发生变化，就触发监听器的onFocusChange方法
    //    et_password.setOnFocusChangeListener(this);


        //给btn_click设置长按监听器，一旦用户长按按钮，就触发监听器的onLongClick方法
        //btn_click.setOnLongClickListener(new MyOnLongClickListener());

        //给login_click设置点击监听器，一旦用户点击按钮，就触发监听器的onClick方法
        login_click.setOnClickListener(this);
        //给regist_click设置点击监听器，一旦用户点击按钮，就触发监听器的onClick方法
        regist_click.setOnClickListener(this);
    }

    // 定义登录方式的单选监听器
    private class RadioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_password) { // 选择了密码登录
            //    tv_password.setText("登录密码：");
                et_password.setText(temp);
                et_password.setHint("请输入密码");
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            //    btn_forget.setText("忘记密码");
            //    btn_forget.setTextSize(14);
                ck_remember.setVisibility(View.VISIBLE);
                btn_forget.setVisibility(View.VISIBLE);
                get_verifycode.setVisibility(View.INVISIBLE);       //隐藏获取验证码按钮
            } else if (checkedId == R.id.rb_verifycode) {           // 选择了验证码登录
            //    temp=et_password.getText().toString().trim();
            //    tv_password.setText("　验证码：");
                et_password.setHint("请输入验证码");
                et_password.setInputType(InputType.TYPE_CLASS_NUMBER);
                et_password.setText("");
            //    btn_forget.setText("获取验证码");
            //    btn_forget.setTextSize(12);
                get_verifycode.setVisibility(View.VISIBLE);         //显示获取验证码按钮
                ck_remember.setVisibility(View.INVISIBLE);          //隐藏记住密码复选框
                btn_forget.setVisibility(View.INVISIBLE);           //隐藏忘记密码复选框
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

    private void setToolbar(){
        //从布局文件中获取名叫tl_head的工具栏
    //    tl_head=findViewById(R.id.tl_head);                                   //!!!!!!!!!!!!!!!!!!!!!!
        /*既可以在.java文件中设置，又可以在.xml布局文件中设置
        // 设置工具栏左边的导航图标
        tl_head.setNavigationIcon(R.drawable.ic_back);
        // 设置工具栏的标题文本
        tl_head.setTitle("轻舟");
        // 设置工具栏的标题文字颜色
        tl_head.setTitleTextColor(Color.BLACK);

        // 设置工具栏的标志图片
        tl_head.setLogo(R.drawable.ic_app);
        // 设置工具栏的背景
        tl_head.setBackgroundResource(R.color.colorPrimary);*/
        // 使用tl_head替换系统自带的ActionBar
    //    setSupportActionBar(tl_head);                                         //!!!!!!!!!!!!!!!!!!!!!!
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
/*        tl_head.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 结束当前页面
            }
        });*/
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
        else if (v.getId() == R.id.login_click) { // 点击了“登录”按钮
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
        }   //点击注册按钮
        else if (v.getId() == R.id.regist_click) {   //判断是否为注册按钮被点击
            Intent intent_main_to_regist = new Intent(MainFrameActivity.this, RegistActivity.class);
            startActivity(intent_main_to_regist);
        }   //获取验证码
        else if(v.getId() == R.id.get_verifycode){
            Toast.makeText(this,"getVerifycode",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String phone=et_phone.getText().toString();
        //判断是否是密码编辑框发生焦点变化
        if(v.getId()==R.id.et_password){
            //用户已输入手机号码，且密码框获得焦点，单选按钮选择的是以密码方式登录
            if(phone.length()>0&&phone.length()==11&&hasFocus&&rb_password.isChecked()){
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
        //如果此时记住密码勾选，则记录
        if(ck_remember.isChecked()){
            editor.putBoolean("bRemember",true);
        }else{
            editor.putBoolean("bRemember",false);
        }
        //记录这个手机号码为本机最后一个登录的用户的手机号码
        editor.putString("last_login_phone",et_phone.getText().toString().trim());
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
        editor.putBoolean("first_login",false); //添加名叫first_login的布尔值
        editor.commit();    //提交编辑器的修改
        //控制显示引导页
        showLaunchPage();
    }

    //负责控制是否显示第一次成功登录的引导页
    private void showLaunchPage(){
        //能到达这里说明这个手机号是存在的
        //将手机号储存起来
        editor.putString("user_phone", et_phone.getText().toString()); //添加登录用户的手机号
        editor.commit();    //提交编辑器的修改

        if(isFirstLogin==true){
            //如果是首次成功登录，就前往引导页面
            Intent login_to_launch=new Intent(MainFrameActivity.this, LaunchSimpleActivity.class);
            startActivity(login_to_launch);
        }
        else {
            //非首次登录用户将会去到功能页面
            Intent login_to_tabhost=new Intent(MainFrameActivity.this, TabHostActivity.class);
            startActivity(login_to_tabhost);
        }
    }

//    //定义一个点击监听器，它实现了接口View.OnClickListener
//    class MyOnClickListener_MainFrame implements View.OnClickListener {
//        public void onClick(View v) {    //点击事件的处理办法
//            if (v.getId() == R.id.login_click) {  //判断是否为登录按钮被点击
//                Intent intent_main_to_login = new Intent(MainFrameActivity.this, LoginActivity.class);
//                startActivity(intent_main_to_login);
//            } else if (v.getId() == R.id.regist_click) {   //判断是否为注册按钮被点击
//                Intent intent_main_to_regist = new Intent(MainFrameActivity.this, RegistActivity.class);
//                startActivity(intent_main_to_regist);
//            }
//        }
//    }

    //操作溢出菜单栏
    public boolean onMenuOpened(int featureId, Menu menu){
        //显示菜单栏左侧的图标
        MenuUtil.setOverflowIconVisible(featureId,menu);
        return super.onMenuOpened(featureId, menu);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        //从menu_overflow.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_overflow,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.menu_refresh){  //点击了刷新图标
            onRestart();    //重新加载内存中该页面的数据
        }
        else if(id==R.id.menu_about){   //点击了“关于”菜单项

        }
        else if(id==R.id.menu_share){   //点击了“分享”菜单项

        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume(){
        super.onResume();
//        //获得用户数据库帮助器的一个实例
//        mHelper=UserDBHelper.getInstance(this,2);
//        //恢复页面，则打开数据库连接
//        mHelper.openWriteLink();
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
}
    /*//定义一个长按监听器，它实现了接口View.OnLongClickListener
    class MyOnLongClickListener implements View.OnLongClickListener{
        public boolean onLongClick(View v){    //点击事件的处理办法
            if(v.getId()==R.id.regist_click){  //判断是否为注册按钮被点击
                Toast.makeText(ClickActivity.this,"您长按了控件："+((TextView)v).getText(),
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }*/
