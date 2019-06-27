package nju.androidchat.client.hw1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Mvp0Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp0Contract.Presenter presenter;

    //url和图片
    private HashMap<String, Bitmap> picMap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);
                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        // 如果是自己发的，增加ItemTextSend

                        boolean addPic=false;
                        if(isPic(text)){
                            if(picMap.containsKey(text)) addPic=true;
                            else {
                                askPic(text);
                            }
                        }

                        if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                            if(addPic){
                                content.addView(new ItemTextSend(this,picMap.get(text) ,text, message.getMessageId(), this));
                            }else {
                                content.addView(new ItemTextSend(this, text, message.getMessageId(), this));
                            }
                        } else {
                            if (addPic) {
                                content.addView(new ItemTextReceive(this, this.picMap.get(text),text, message.getMessageId()));
                            } else {
                                content.addView(new ItemTextReceive(this, text, message.getMessageId()));
                            }
                        }
                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    private void askPic(String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(findUrl(text));
                    Bitmap pic = BitmapFactory.decodeStream(url.openStream());
                    picMap.put(text,pic);
                    showMessageList(presenter.findClientMessages());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //图片判断
    private boolean isPic(String str){
        Pattern pattern=Pattern.compile("(!\\[[\\s\\S]*]\\{)([\\s\\S]*.(png|jpg))\\}");
        Matcher matcher=pattern.matcher(str);
        return matcher.matches();
    }

    //提取图片Url
    private String findUrl(String str){
        Pattern pattern=Pattern.compile("(!\\[[\\s\\S]*]\\{)([\\s\\S]*.(png|jpg))\\}");
        Matcher matcher=pattern.matcher(str);
        matcher.matches();
        return matcher.group(2);
    }


    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
