package heath.com.chat.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import heath.com.chat.BaseActivity;
import heath.com.chat.R;
import heath.com.chat.utils.LoadingUtils;


public class AddFriendsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "friend";
    private SearchView mSvSearchFriends;
    private LinearLayout mLlReturn;
    private TextView mTvResult;

    private LoadingUtils loadingUtils;

    final ArrayList<Map<String, Object>> listdata = new ArrayList<>();
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initView();
        init();
        initListener();
    }

    private void initView() {
        mSvSearchFriends = this.findViewById(R.id.sv_search_friends);
        mLlReturn = this.findViewById(R.id.ll_return);
        mTvResult = this.findViewById(R.id.tv_result);
        gson = new Gson();
        loadingUtils = new LoadingUtils(AddFriendsActivity.this, "正在搜索");
    }

    private void init() {
        loadingUtils.creat();
    }

    private void initListener() {
        mLlReturn.setOnClickListener(this);
        mSvSearchFriends.setSubmitButtonEnabled(false);
        mSvSearchFriends.setQueryHint("查找好友");

        mSvSearchFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String account) {
                listdata.clear();
                loadingUtils.show();
                loadFriend(account);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_return:
                finish();
                break;
            default:
                break;
        }
    }

    private void loadFriend(final String account) {
        List<String> accounts = new ArrayList<>();
        accounts.add(account);
        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> param) {
                        loadingUtils.dismiss();
                        if (param.size() > 0) {
                            Log.i(TAG, "loadFriend: " + param.get(0).getAccount());
                            Intent intent = new Intent(
                                    AddFriendsActivity.this,
                                    FriendInfoActivity.class);
                            intent.putExtra("nimUserInfo", param.get(0));
                            startActivityForResult(intent, 0);
                        } else {
                            mTvResult.setText("未搜索到用户");
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.i(TAG, "code: " + code);
                        loadingUtils.dismiss();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        loadingUtils.dismiss();
                    }
                });
    }

}
