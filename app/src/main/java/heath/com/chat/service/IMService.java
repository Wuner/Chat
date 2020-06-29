package heath.com.chat.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.HashMap;
import java.util.List;

import heath.com.chat.message.MessageFragment;
import heath.com.chat.message.SendMessageActivity;
import heath.com.chat.utils.ACache;
import heath.com.chat.utils.Common;

public class IMService extends Service {

    private static ACache aCache;
    private Gson gson;

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        // 返回server的实例
        public IMService getService() {
            return IMService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        aCache = ACache.get(this);
        gson = new Gson();
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, false);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, false);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, false);
    }

    Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage systemMessage) {
            if (systemMessage.getType() == SystemMessageType.AddFriend) {
                AddFriendNotify attachData = (AddFriendNotify) systemMessage.getAttachObject();
                if (attachData != null) {
                    // 针对不同的事件做处理
                    if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                        // 对方直接添加你为好友
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                        // 对方通过了你的好友验证请求
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                        // 对方拒绝了你的好友验证请求
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                        // 通过message.getContent()获取好友验证请求的附言
                    }
                }
            }
        }
    };
    private Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for (IMMessage message : messages) {
                        Log.i("message", "onEvent===========: " + message.getContent());
                        if (message.getMsgType() == MsgTypeEnum.text) {
                            SendMessageActivity.updateData(message.getContent());
                        } else if (message.getMsgType() == MsgTypeEnum.image) {
                            //接收图片消息处理，因为这里有可能图片还未下载完，所以需要进行二步处理
                            Log.e("图片路径打印", "onResult: " + ((ImageAttachment) message.getAttachment()).getThumbPath() + "---=====-----");
                            Log.e("Tag", "onEvent: " + message.getContent() + "消息==========================");
                        } else if (message.getMsgType() == MsgTypeEnum.video) {
                            //接收视频消息处理，因为这里有可能视频还未下载完，所以需要进行二步处理
                            Log.e("视频路径打印", "onResult: " + ((VideoAttachment) message.getAttachment()).getPath() + "---=====-----" + ((VideoAttachment) message.getAttachment()).getDuration());
                        }
                    }

                }
            };

    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (message.getDirect() == MsgDirectionEnum.In) {
                if (message.getAttachStatus() == AttachStatusEnum.transferred) {
                    if (message.getMsgType() == MsgTypeEnum.image) {
                        //这里是图片下载成功
                        SendMessageActivity.updateData1(((ImageAttachment) message.getAttachment()).getThumbPath());
                    }else if (message.getMsgType() == MsgTypeEnum.video) {
                        String path = ((VideoAttachment) message.getAttachment()).getUrl();
                        SendMessageActivity.updateData2(path);
                    }
                }
            }
        }
    };

}
