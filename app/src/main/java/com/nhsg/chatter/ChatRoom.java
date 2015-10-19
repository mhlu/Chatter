package com.nhsg.chatter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class ChatRoom extends AppCompatActivity {

    private ChatArrayAdapter messageAdp;
    private ListView previousMessages;
    private EditText chatText;
    private Button sendBtn;
    File chatLog;

    Intent in;
    private boolean side = false;

    private Timer message_poller;
    private int poll_period_ms = 1000;

    private String contact_name;
    private String user_id;
    private String token;
    private String target_id;
    private Long last_poll_time;

    private Set<Integer> message_set;

    final Semaphore write_semaphore = new Semaphore();
    final Semaphore poll_semaphore = new Semaphore();

    private class ReceiveMessageCallback implements Callback {
        public void call(Object... objs) {
            JSONObject response = (JSONObject) objs[0];

            try {
                JSONObject messages = (JSONObject) response.get("messages");
                write_semaphore.take();
                Iterator<String> key_it = messages.keys();
                while (key_it.hasNext()) {
                    String friend_id = key_it.next();
                    JSONArray new_messages = (JSONArray) messages.get(friend_id);
                    for (int i = 0; i < new_messages.length(); i++) {

                        JSONObject message = (JSONObject) new_messages.get(i);

                        int id = message.getInt("id");
                        if (message_set.contains(id)) {
                            continue;
                        }
                        message_set.add(id);
                        String content = message.getString("content");
                        String sender = message.getString("sender");
                        Long sent_time = message.getLong("sent_time");
                        Date timestamp = new java.util.Date((long)sent_time*1000);
                        if (sender.equals(target_id)) {
                            sendChatMessage(content, timestamp.toString(), false);
                        }
                        File cur_log = new File("data/data/com.nhsg.chatter/chatlog_" + user_id + "_" + sender);
                        try{
                            FileWriter chatLogWriter = new FileWriter(cur_log, true);
                            chatLogWriter.write("not me\n");
                            chatLogWriter.write(timestamp + "\n");
                            chatLogWriter.write(content + "\n");
                            chatLogWriter.close();
                        } catch (java.io.IOException e) {}

                    }
                }
                write_semaphore.release();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private class SendMessageCallback implements Callback {
        private String message;
        SendMessageCallback(String msg) {
            this.message = msg;
        }
        public void call(Object... objs) {
            String timestamp = DateFormat.getDateTimeInstance().format(new Date());
            sendChatMessage(message, timestamp, true);
            try{
                write_semaphore.take();
                FileWriter chatLogWriter = new FileWriter(chatLog, true);
                chatLogWriter.write("me\n");
                chatLogWriter.write(timestamp + "\n");
                chatLogWriter.write(message + "\n");
                chatLogWriter.close();
                write_semaphore.release();
            }catch (java.io.IOException e) {} catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        message_set = new HashSet<Integer>();

        Intent intent = getIntent();
        contact_name = intent.getStringExtra("contact_name");
        user_id = intent.getStringExtra("user_id");
        token = intent.getStringExtra("token");
        target_id = intent.getStringExtra("target_id");


        sendBtn = (Button)findViewById(R.id.sendBtn);
        previousMessages = (ListView)findViewById(R.id.previousMessages);
        messageAdp = new ChatArrayAdapter(getApplicationContext(), R.layout.chat, new ArrayList<ChatMessage>());
        chatText = (EditText)findViewById(R.id.textInput);



        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String chatMessage = chatText.getText().toString();

                String urlString = "http://messengerproject-dev.elasticbeanstalk.com/messaging/sendmessage/";
                JSONObject sendMsgRequest = new JSONObject();
                try {
                    sendMsgRequest.put("token", token);
                    sendMsgRequest.put("user_id", user_id);
                    sendMsgRequest.put("receiver", target_id);
                    sendMsgRequest.put("message", chatMessage);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.toString());
                }
                PostTask sendMessageTask = new PostTask(urlString);
                sendMessageTask.callback = new SendMessageCallback(chatMessage);
                sendMessageTask.execute(sendMsgRequest);
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

        try {
            chatLog = new File("data/data/com.nhsg.chatter/chatlog_" + user_id + "_" + target_id);
            if (!chatLog.exists()) {
                chatLog.createNewFile();
            } else {
                Scanner scan = new Scanner(chatLog);
                write_semaphore.take();
                while (scan.hasNextLine()) {
                    String who = scan.nextLine();
                    String timestamp = scan.nextLine();
                    String message = scan.nextLine();
                    sendChatMessage(message, timestamp, who.equals("me"));
                }
                write_semaphore.release();
            }
        } catch (java.io.IOException e) {} catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            poll_semaphore.take();
                            String urlString = "http://messengerproject-dev.elasticbeanstalk.com/messaging/poll/";
                            PostTask pollTask = new PostTask(urlString);
                            pollTask.callback = new ReceiveMessageCallback();
                            JSONObject pollRequest = new JSONObject();
                            try {
                                pollRequest.put("token", token);
                                pollRequest.put("user_id", user_id);
                                Long last_poll_time = ((State) getApplication()).getLastPollTime();
                                //System.out.println(last_poll_time);
                                pollRequest.put("time", ((State) getApplication()).getLastPollTime());
                                ((State)getApplication()).setLastPollTime(new Date().getTime() / 1000L);
                            } catch (Exception e) {
                                System.out.println("Exception: " + e.toString());
                            }
                            pollTask.execute(pollRequest);
                            poll_semaphore.release();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, poll_period_ms); //execute in every 50000 ms
    }

    private boolean sendChatMessage(String message, String timestamp, boolean me) {
        side = !me;
        messageAdp.add(new ChatMessage(side, message, timestamp));
        chatText.setText("");
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
