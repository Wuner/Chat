package heath.com.chat.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import heath.com.chat.R;

public class MessageFragment extends Fragment implements View.OnClickListener {
    /**
     * 发消息
     */
    private Button mBtnSendText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container,
                false);
        initView(view);
        // 绑定服务
        return view;
    }

    private void initView(View view) {
        mBtnSendText = view.findViewById(R.id.btn_send_text);
        mBtnSendText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_send_text:
                startActivity(new Intent(getActivity(),SendMessageActivity.class));
                break;
        }
    }

}
