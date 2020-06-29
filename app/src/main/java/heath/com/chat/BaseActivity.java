package heath.com.chat;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import heath.com.chat.utils.ACache;

public class BaseActivity extends AppCompatActivity{

    // 实现列表
    public static List<AppCompatActivity> activityList = new ArrayList<AppCompatActivity>();
    public static ACache aCache;
    public static File cache;
    public static File original_drawing;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //创建缓存目录，系统一运行就得创建缓存目录的，
        init();
    }

    private void init() {
        aCache = ACache.get(this);
        cache = new File(Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim", "cache");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        original_drawing = new File(Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim/cache", "original_drawing");
        if (!original_drawing.exists()) {
            original_drawing.mkdirs();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        activityList.remove(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }


}
