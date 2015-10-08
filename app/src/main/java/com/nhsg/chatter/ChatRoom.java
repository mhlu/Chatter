package com.nhsg.chatter;

import android.content.Context;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatRoom extends AppCompatActivity {

    private ChatArrayAdapter messageAdp;
    private ListView previousMessages;
    private EditText chatText;
    private Button sendBtn;
    File chatLog;
    FileWriter chatLogWriter;
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
                try{
                    chatLogWriter = new FileWriter(chatLog, true);
                    chatLogWriter.append(chatMessage + "\n");
                    chatLogWriter.close();
                }catch (java.io.IOException e) {}
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

//        sendChatMessage("Chatting with " + contactName);
//        String string = "hello world!";

        try {
            chatLog = new File("data/data/com.nhsg.chatter/" + contactName);
            if (!chatLog.exists()) {
                chatLog.createNewFile();
            } else {
                Scanner scan = new Scanner(chatLog);
                while (scan.hasNextLine()) {
                    String line = scan.nextLine();
                    sendChatMessage(line);
                    //Here you can manipulate the string the way you want
                }
            }
        } catch (java.io.IOException e) {}
    }

    private boolean sendChatMessage(String message) {
        messageAdp.add(new ChatMessage(side, message, DateFormat.getDateTimeInstance().format(new Date())));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
