package heath.com.chat.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.HashMap;
import java.util.List;

import heath.com.chat.R;
import heath.com.chat.adapter.FriendAdapter;

public class FriendsFragment extends Fragment {

    private RecyclerView mRvFriends;
    private FriendAdapter friendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container,
                false);
        initView(view);
        init();
        return view;
    }

    private void initView(View view) {
        mRvFriends = view.findViewById(R.id.rv_friends);
    }

    private void init(){
        List<String> friends = NIMClient.getService(FriendService.class).getFriendAccounts();
        NIMClient.getService(UserService.class).fetchUserInfo(friends)
                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> param) {
                        if (param.size()>0){
                            friendAdapter = new FriendAdapter(getActivity(), param);
                            mRvFriends.setAdapter(friendAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            mRvFriends.setLayoutManager(linearLayoutManager);
                            mRvFriends.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            friendAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    NimUserInfo nimUserInfo = param.get(position);
                                    Intent intent = new Intent(
                                            getActivity(),
                                            FriendInfoActivity.class);
                                    intent.putExtra("nimUserInfo", nimUserInfo);
                                    startActivityForResult(intent, 0);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }
}
