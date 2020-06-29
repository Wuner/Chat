package heath.com.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.io.Serializable;

import heath.com.chat.service.IMService;
import heath.com.chat.utils.LoadingUtils;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtAccount;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private TextView mTvRegister;
    protected LoadingUtils loadingUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void initView() {
        mEtAccount = this.findViewById(R.id.et_account);
        mEtPassword = this.findViewById(R.id.et_passwords);
        mBtnLogin = this.findViewById(R.id.btn_login);
        mTvRegister = this.findViewById(R.id.register);
        loadingUtils = new LoadingUtils(LoginActivity.this, "登录中");
        loadingUtils.creat();
    }

    private void initListener() {
        mBtnLogin.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
    }

    private void loginIM(String account, String token) {
        LoginInfo info = new LoginInfo(account, token); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {

                    @Override
                    public void onException(Throwable arg0) {
                        System.out.println("--------------------------------");
                        System.out.println(arg0);
                    }

                    @Override
                    public void onFailed(int code) {
                        loadingUtils.dismiss();
                        if (code == 302) {
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        } else if (code == 408) {
                            Toast.makeText(LoginActivity.this, "登录超时", Toast.LENGTH_SHORT).show();
                        } else if (code == 415) {
                            Toast.makeText(LoginActivity.this, "未开网络", Toast.LENGTH_SHORT).show();
                        } else if (code == 416) {
                            Toast.makeText(LoginActivity.this, "连接有误，请稍后重试", Toast.LENGTH_SHORT).show();
                        } else if (code == 417) {
                            Toast.makeText(LoginActivity.this, "该账号已在另一端登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "未知错误，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        Log.e("TAG", "onSuccess: " + loginInfo + "======================================================");
                        aCache.put("loginInfo", loginInfo);
                        Intent server = new Intent(LoginActivity.this,
                                IMService.class);
                        startService(server);
                        startActivity(new Intent(LoginActivity.this, TabHostActivity.class));
                        finish();
                    }
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String account = mEtAccount.getText().toString().toLowerCase();
                String token = mEtPassword.getText().toString();
                loadingUtils.show();
                loginIM(account, token);
                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }
}
