package com.nhsg.chatter;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatRoom extends AppCompatActivity {

    private ChatArrayAdapter messageAdp;
    private ListView previousMessages;
    private EditText chatText;
    private Button sendBtn;
    Intent in;
    private boolean side = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Intent intent = this.getIntent();
        String contactName = intent.getStringExtra("contact_name");

        sendBtn = (Button)findViewById(R.id.sendBtn);
        previousMessages = (ListView)findViewById(R.id.previousMessages);
        messageAdp = new ChatArrayAdapter(getApplicationContext(), R.layout.chat, new ArrayList<ChatMessage>());
        chatText = (EditText)findViewById(R.id.textInput);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String chatMessage = chatText.getText().toString();
                sendChatMessage(chatMessage);
            }
        });

        previousMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        previousMessages.setAdapter(messageAdp);

        messageAdp.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                previousMessages.setSelection(messageAdp.getCount() - 1);
            }
        });

        sendChatMessage("Chatting with " + contactName);
    }

    private boolean sendChatMessage(String message) {
        messageAdp.add(new ChatMessage(side, message));
        chatText.setText("");
        side = !side;

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
