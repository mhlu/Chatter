package com.nhsg.chatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2015-10-03.
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private List<ChatMessage> messageList = new ArrayList<ChatMessage>();
    private LinearLayout layout;

    public ChatArrayAdapter(Context context, int textViewResourceId,
                            List<ChatMessage> objects) {
        super(context, textViewResourceId, objects);

    }

    public void add(ChatMessage object) {
        messageList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.messageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.messageList.get(index);
    }

    public View getView(int position, View ConvertView, ViewGroup parent) {
        View v = ConvertView;
        if ( v == null ) {
            LayoutInflater inflater = (LayoutInflater)this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.chat, parent, false);
        }

        layout = (LinearLayout)v.findViewById(R.id.singleMessage);
        ChatMessage messageobj = getItem(position);

        TextView chatText = (TextView)v.findViewById(R.id.messageText);
        chatText.setText(messageobj.message);
        chatText.setBackgroundResource(
                messageobj.left ? R.drawable.out_message_bg : R.drawable.in_message_bg);

        TextView chatTime = (TextView)v.findViewById(R.id.messageTime);
        chatTime.setText(messageobj.dateTime);

        layout.setGravity(messageobj.left ? Gravity.LEFT : Gravity.RIGHT );
        return v;
    }

    public Bitmap decodeToBitmap(byte[] decodeByte) {
        return BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
    }
}
