package heath.com.chat.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import heath.com.chat.BaseActivity;
import heath.com.chat.LoginActivity;
import heath.com.chat.OKhttp.IHttpClient;
import heath.com.chat.OKhttp.IRequest;
import heath.com.chat.OKhttp.IResponse;
import heath.com.chat.OKhttp.impl.OkHttpClientImpl;
import heath.com.chat.OKhttp.impl.RequestImpl;
import heath.com.chat.R;
import heath.com.chat.utils.Common;
import heath.com.chat.utils.LoadingUtils;
import heath.com.chat.utils.ThreadUtils;

public class FriendInfoActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mLlReturn;
    private ImageView mIvHeadPhoto;
    private TextView mTvNickname;
    private ImageView mIvSex;
    private TextView mTvAddress;
    private TextView mTvSign;
    private Button mBtnAddFriend;
    private Button mBtnSendMessage;

    private NimUserInfo nimUserInfo;
    protected LoadingUtils loadingUtils;
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    private LoginInfo loginInfo;

    private String TAG = "friend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        initView();
        initListener();
        init();
    }

    private void initView() {
        mLlReturn = this.findViewById(R.id.ll_return);
        mIvHeadPhoto = this.findViewById(R.id.iv_head_photo);
        mTvNickname = this.findViewById(R.id.tv_nickname);
        mIvSex = this.findViewById(R.id.iv_sex);
        mTvAddress = this.findViewById(R.id.tv_address);
        mTvSign = this.findViewById(R.id.tv_sign);
        mBtnAddFriend = this.findViewById(R.id.btn_add_friend);
        mBtnSendMessage = this.findViewById(R.id.btn_send_message);
    }

    private void initListener() {
        mBtnAddFriend.setOnClickListener(this);
        mBtnSendMessage.setOnClickListener(this);
        mLlReturn.setOnClickListener(this);
    }

    private void init() {
        Intent intent = getIntent();
        nimUserInfo = (NimUserInfo) intent.getSerializableExtra("nimUserInfo");
        loginInfo = (LoginInfo) aCache.getAsObject("loginInfo");
        loadingUtils = new LoadingUtils(FriendInfoActivity.this, "添加好友...");
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
        loadingUtils.creat();
       if (nimUserInfo.getAccount().equals(loginInfo.getAccount())){
           mBtnAddFriend.setVisibility(View.GONE);
           mBtnSendMessage.setVisibility(View.GONE);
       }else{
           boolean isMyFriend = NIMClient.getService(FriendService.class).isMyFriend(nimUserInfo.getAccount());
           Log.i(TAG, "isMyFriend: "+isMyFriend);
           if (isMyFriend) {
               mBtnAddFriend.setVisibility(View.GONE);
               mBtnSendMessage.setVisibility(View.VISIBLE);
           }else {
               mBtnAddFriend.setVisibility(View.VISIBLE);
               mBtnSendMessage.setVisibility(View.GONE);
           }
       }
        if (nimUserInfo.getName() != null) {
            mTvNickname.setText(nimUserInfo.getName());
        }
        if (nimUserInfo.getSignature() != null) {
            mTvSign.setText(nimUserInfo.getSignature());
        }
        GenderEnum genderEnum = nimUserInfo.getGenderEnum();
        switch (genderEnum) {
            case MALE:
                mIvSex.setImageResource(R.drawable.boy);
                break;
            case FEMALE:
                mIvSex.setImageResource(R.drawable.girl);
                break;
            case UNKNOWN:
                mIvSex.setImageResource(R.drawable.unknown);
                break;
            default:
                mIvSex.setImageResource(R.drawable.unknown);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_add_friend:
                loadingUtils.show();
                addFriend();
                break;
            case R.id.btn_send_message:
                break;
            case R.id.ll_return:
                finish();
                break;
        }
    }

    private void addFriend() {
        final VerifyType verifyType = VerifyType.DIRECT_ADD; // DIRECT_ADD 直接加对方为好友;VERIFY_REQUEST 发起好友验证请求
        String msg = "好友请求附言";
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(nimUserInfo.getAccount(), verifyType, msg))
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        loadingUtils.dismiss();
                        NormalDialogOneBtn("添加好友成功", 200);
                    }

                    @Override
                    public void onFailed(int code) {
                        loadingUtils.dismiss();
                        NormalDialogOneBtn("添加好友失敗", 414);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        loadingUtils.dismiss();
                        NormalDialogOneBtn("添加好友失敗", 500);
                    }
                });
    }

    private void NormalDialogOneBtn(String msg, int code) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content(msg)//
                .btnNum(1)
                .btnText("确定")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                if (code == 200) {
                    finish();
                }
            }
        });
    }
}
