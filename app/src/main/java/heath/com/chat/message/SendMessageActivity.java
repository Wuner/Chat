package heath.com.chat.message;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;

import heath.com.chat.R;
import heath.com.chat.service.IMService;
import heath.com.chat.utils.RealPathFromUriUtils;
import heath.com.chat.utils.ToastUtil;

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static IMService mImService;
    private LinearLayout mLlReturn;
    private EditText mEdSendText;
    //调用系统相册-选择图片
    private static final int IMAGE = 1;
    /**
     * 发消息
     */
    private Button mBtnSendText;
    private static TextView mTvReceiveMessage;
    private Button mBtnAlbum;
    private Button mBtnVideo;
    private static ImageView mIvReceiveMessage;
    private boolean video = false;//false为图片，true为视频
    private static StandardGSYVideoPlayer videoPlayer;
    private static OrientationUtils orientationUtils;
    private static Context context;
    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        initView();
        init();
    }

    private void init() {
        // 绑定服务
        Intent service = new Intent(SendMessageActivity.this, IMService.class);
        bindService(service, mMyServiceConnection, BIND_AUTO_CREATE);
        context = this;
        activity = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解绑服务
        if (mMyServiceConnection != null) {
            unbindService(mMyServiceConnection);
        }
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    MyServiceConnection mMyServiceConnection = new MyServiceConnection();

    private void initView() {
        mLlReturn = (LinearLayout) findViewById(R.id.ll_return);
        mEdSendText = (EditText) findViewById(R.id.ed_send_text);
        mBtnSendText = (Button) findViewById(R.id.btn_send_text);
        mBtnSendText.setOnClickListener(this);
        mTvReceiveMessage = (TextView) findViewById(R.id.tv_receive_message);
        mBtnAlbum = (Button) findViewById(R.id.btn_album);
        mBtnVideo = (Button) findViewById(R.id.btn_video);
        mIvReceiveMessage = (ImageView) findViewById(R.id.iv_receive_message);
        videoPlayer = this.findViewById(R.id.video_player);
        mBtnAlbum.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mIvReceiveMessage.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            default:
                break;
            case R.id.ll_return:
                finish();
                break;
            case R.id.btn_album:
                intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                video = false;
                startActivityForResult(intent, IMAGE);
                break;
            case R.id.btn_video:
                intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                video = true;
                startActivityForResult(intent, IMAGE);
                break;
            case R.id.btn_send_text:
                final String content = mEdSendText.getText().toString();//消息文本
                String account = "1";//目前这里是写死的账号
                SessionTypeEnum type = SessionTypeEnum.P2P;//会话类型
                final IMMessage textMessage = MessageBuilder.createTextMessage(account, type, content);
                NIMClient.getService(MsgService.class).sendMessage(textMessage, false).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        ToastUtil.toastOnUiThread(SendMessageActivity.this, "发送成功");
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.e("文本发送失败", "onEvent: " + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.e("文本发送异常", "onEvent: " + exception);
                    }
                });
                mEdSendText.setText("");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            //将uri转换为路径
            String path = RealPathFromUriUtils.getRealPathFromUri(this, selectedImage);
            File file = new File(path);
            String account = "1";//目前这里是写死的账号
            IMMessage message;
            if (video) {
                MediaPlayer mediaPlayer = null;
                try {
                    mediaPlayer = MediaPlayer.create(this, Uri.fromFile(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 视频文件持续时间
                final long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
                // 视频高度
                final int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
                // 视频宽度
                final int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
                message = MessageBuilder.createVideoMessage(account, SessionTypeEnum.P2P, file, duration, width, height, null);
            } else {
                message = MessageBuilder.createImageMessage(account, SessionTypeEnum.P2P, file, file.getName());
            }
            NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    try {
                        ToastUtil.toastOnUiThread(SendMessageActivity.this, "发送成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int code) {
                    Log.e("图片发送失败", "onEvent: " + code);
                }

                @Override
                public void onException(Throwable exception) {
                    exception.printStackTrace();
                    Log.e("图片发送异常", "onEvent: " + exception);
                }
            });
        }
    }

    //收到文本消息更新界面
    public static void updateData(String message) {
        mTvReceiveMessage.setText(message);
    }

    //收到图片消息更新界面
    public static void updateData1(String message) {
        mIvReceiveMessage.setImageURI(Uri.parse(message));
        mIvReceiveMessage.setVisibility(View.VISIBLE);
    }

    //收到视频消息更新界面
    public static void updateData2(String message) {
        videoPlayer.setVisibility(View.VISIBLE);
        videoPlayer.setUp(message, true, "测试视频");

        //增加封面
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(activity, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    videoPlayer.getFullscreenButton().performClick();
                    return;
                }
                //释放所有
                videoPlayer.setVideoAllCallBack(null);
            }
        });
        videoPlayer.startPlayLogic();
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out
                    .println("--------------onServiceConnected--------------");
            IMService.MyBinder binder = (IMService.MyBinder) service;
            mImService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out
                    .println("--------------onServiceDisconnected--------------");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }

}
