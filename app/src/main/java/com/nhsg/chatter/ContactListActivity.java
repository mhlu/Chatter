package com.nhsg.chatter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListActivity extends AppCompatActivity {
    private Button addContactBtn;
    private EditText addContactField;
    private ContactListCallback contactListCallback;
    private String user_id;
    private String token;
    Map<String, String> getID = new HashMap<String, String>();
    List<String> contactList = new ArrayList<String>();

    private class ContactListCallback implements Callback {
        public void call(Object... objs) {
            JSONObject response = (JSONObject) objs[0];

            try {
//                if (response.get("status").toString().equals("success")) {
                    JSONArray friends =  (JSONArray)response.get("friends");
                    for(int i = 0; i < friends.length(); i++){
                        JSONObject friend = ((JSONObject)friends.get(i));
                        String username = friend.getString("username");
                        String userid  = friend.getString("id");
                        contactList.add(username);
                        getID.put(username, userid);
                    }
                    refreshContactList();
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void refreshContactList() {
        ListAdapter theAdapter = new ArrayAdapter<String>(ContactListActivity.this, android.R.layout.simple_list_item_1,
                contactList);
        ListView theListView = (ListView) findViewById(R.id.contactsListView);
        theListView.setAdapter(theAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        token = extras.get("token").toString();
        user_id = extras.get("user_id").toString();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactListCallback = new ContactListCallback();

        addContactBtn = (Button) findViewById(R.id.addContactBtn);
        addContactField = (EditText) findViewById(R.id.addContactInput);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContact = addContactField.getText().toString();

                String urlString = "http://messengerproject-dev.elasticbeanstalk.com/messaging/addfriend/";
                PostTask addFriendTask = new PostTask(urlString);
                addFriendTask.callback = contactListCallback;
                JSONObject addfriendRequest = new JSONObject();
                try {
                    addfriendRequest.put("token", token);
                    addfriendRequest.put("user_id", user_id);
                    addfriendRequest.put("target_name", newContact);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.toString());
                }
                addFriendTask.execute(addfriendRequest);

                if (newContact.length() > 0 && !contactList.contains(newContact)) {
                    contactList.add(addContactField.getText().toString());
                    addContactField.setText("");
                }
                refreshContactList();
                clearFocus();

            }
        });

        ListAdapter theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                contactList);
        ListView theListView = (ListView) findViewById(R.id.contactsListView);
        theListView.setAdapter(theAdapter);
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: open messages with selected contact
                Intent chatIntent = new Intent(ContactListActivity.this, ChatRoom.class);
                String contactName = (String) ((ListView) findViewById(R.id.contactsListView)).getItemAtPosition(position);
                chatIntent.putExtra("contact_name", contactName);
                chatIntent.putExtra("user_id", user_id);
                chatIntent.putExtra("token", token);
                chatIntent.putExtra("target_id", getID.get(contactName));
                startActivity(chatIntent);
            }
        });


    }



    @Override
    public void onStart() {
        super.onStart();

        contactList.clear();

        Bundle extras = getIntent().getExtras();
        String token = extras.get("token").toString();
        String user_id = extras.get("user_id").toString();

        String urlString = "http://messengerproject-dev.elasticbeanstalk.com/messaging/getfriendlist/";
        PostTask fetchContactsTask = new PostTask(urlString);
        fetchContactsTask.callback = contactListCallback;
        JSONObject contactListRequest = new JSONObject();
        try {
            contactListRequest.put("token", token);
            contactListRequest.put("user_id", user_id);
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        fetchContactsTask.execute(contactListRequest);
        refreshContactList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
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

    public void clearFocus() {
        View view = ContactListActivity.this.getCurrentFocus();
        if ( view != null ) {
            InputMethodManager imm =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
