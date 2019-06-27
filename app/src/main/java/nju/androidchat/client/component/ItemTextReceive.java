package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.util.UUID;

import nju.androidchat.client.R;

public class ItemTextReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private TextView textView;
    private ImageView picView=null;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;


    public ItemTextReceive(Context context, String text, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_receive, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.messageId = messageId;
        setText(text);
    }

    public ItemTextReceive(Context context, Bitmap bitmap, String text, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_send, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.picView=findViewById(R.id.image_content);
        this.messageId = messageId;

        this.picView.setVisibility(View.VISIBLE);
        this.textView.setVisibility(View.GONE);
        setText(text);
        setPic(bitmap);
    }

    public void init(Context context) {

    }

    public void setPic(Bitmap bitmap){
        if(this.picView!=null) picView.setImageBitmap(bitmap);
    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
