package heath.com.chat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import heath.com.chat.OKhttp.IHttpClient;
import heath.com.chat.OKhttp.IRequest;
import heath.com.chat.OKhttp.IResponse;
import heath.com.chat.OKhttp.impl.OkHttpClientImpl;
import heath.com.chat.OKhttp.impl.RequestImpl;
import heath.com.chat.utils.Common;
import heath.com.chat.utils.LoadingUtils;
import heath.com.chat.utils.ThreadUtils;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mLlReturn;
    private Button mBtnRegister;
    private EditText mEtAccount;
    private EditText mEtpassword;
    private LoadingUtils loadingUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        init();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mLlReturn = this.findViewById(R.id.ll_return);
        mBtnRegister = this.findViewById(R.id.btn_register);
        mEtAccount = this.findViewById(R.id.et_account);
        mEtpassword = this.findViewById(R.id.et_passwords);
        loadingUtils = new LoadingUtils(RegisterActivity.this, "注册中");

    }

    private void init() {
        loadingUtils.creat();
    }

    private void initListener() {
        mLlReturn.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String account = mEtAccount.getText().toString();
        final String password = mEtpassword.getText().toString();
        switch (view.getId()) {
            case R.id.ll_return:
                finish();
                break;
            case R.id.btn_register:
                if (password.length() >= 6) {
                    loadingUtils.show();
                    register(account, password);
                } else {
                    Toast.makeText(this, "密码少于6位", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void register(final String account, final String password) {
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle data = new Bundle();
                try {
                    Map<String,Object> parameter = new HashMap<>();
                    parameter.put("accid", account);
                    parameter.put("name", account);
                    parameter.put("token", password);
                    Map<String,String> head = Common.getHead();
                    IRequest request = new RequestImpl("https://api.netease.im/nimserver/user/create.action");
                    //设置请求头，这里要几个就setHeader几次
                    request.setHeader(head);
                    // 设置请求体 同上
                    request.setBody(parameter);
                    // 获取一个okhttpclient实例
                    IHttpClient mHttpClient = new OkHttpClientImpl();
                    // 得到服务器端返回的结果
                    IResponse response = mHttpClient.post(request);
                    JSONObject returnObj = new JSONObject(response.getData());
                    Log.d("console", "回调: " + returnObj.toString());
                    int code = (int) returnObj.get("code");
                    switch (code) {
                        case 200:
                            message.what = 200;
                            data.putSerializable("Msg", Common.MSG_REGISTER_SUCCESS);
                            break;
                        case 414:
                            message.what = 414;
                            data.putSerializable("Msg", Common.MSG_REGISTER_HAVE_PHONE);
                            break;
                        default:
                            message.what = 1000;
                            data.putSerializable("Msg", Common.MSG_REGISTER_ERROR);
                            break;
                    }
                    message.setData(data);
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    data.putSerializable("Msg",
                            Common.MSG_REGISTER_ERROR);
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private static class IHandler extends Handler {

        private final WeakReference<Activity> mActivity;

        public IHandler(RegisterActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            ((RegisterActivity) mActivity.get()).loadingUtils.dismiss();
            int flag = msg.what;
            String Msg = (String) msg.getData().getSerializable(
                    "Msg");
            switch (flag) {
                case 0:
                    ((RegisterActivity) mActivity.get()).showTip(Msg);
                    break;
                case 200:
                    ((RegisterActivity) mActivity.get())
                            .showTip(Msg);
                    ((RegisterActivity) mActivity.get()).finish();
                    break;
                case 414:
                    ((RegisterActivity) mActivity.get()).showTip(Msg);
                    break;
                case 1000:
                    ((RegisterActivity) mActivity.get()).showTip(Msg);
                    break;

                default:
                    break;
            }

        }
    }

    private IHandler handler = new IHandler(this);

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
