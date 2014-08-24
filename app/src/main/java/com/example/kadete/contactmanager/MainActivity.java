package com.example.kadete.contactmanager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final int EDIT = 0, DELETE = 1;
    EditText nameTxt, phoneTxt, emailTxt, addressTxt;
//   ImageView contactImgAdd , contactImgEdit, contactImgDelete;
    ImageView contactImgAdd;
    List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;
    Uri imgUri = Uri.parse("android.resource://com.example.kadete.contactmanager/drawable/user_icon.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contact> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);

        contactImgAdd = (ImageView) findViewById(R.id.imgContactAdd);
//        contactImgEdit = (ImageView) findViewById(R.id.imgContactEdit);
//        contactImgDelete = (ImageView) findViewById(R.id.imgContactDelete);

        contactListView = (ListView) findViewById(R.id.listView);

        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = new Contact(dbHandler.getContactCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()), imgUri);
                if (contactsExists(contact)) {
                    dbHandler.createContact(contact);
                    Contacts.add(contact);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " has been added to your contacts!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists! Please use another name!", Toast.LENGTH_SHORT).show();
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addBtn.setEnabled(!String.valueOf(nameTxt.getText()).isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        contactImgAdd.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent();
                  intent.setType("image/*");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
              }
          });
//
//        contactImgEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        contactImgDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        
        if(dbHandler.getContactCount() != 0)
            Contacts.addAll(dbHandler.getAllContacts());
        populateList();
    }

    private boolean contactsExists(Contact contact){
        String name = contact.getName();
        int contactsCount = Contacts.size();
        for(int i = 0; i < contactsCount; i++){
            if(name.compareToIgnoreCase(Contacts.get(i).getName()) == 0){
                return true;
            }
        }
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,view,menuInfo);
        menu.setHeaderIcon(R.drawable.edit_user_icon);
        menu.setHeaderTitle("Contact Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact");
    }

    public boolean onContextItemSelected (MenuItem item){
        switch (item.getItemId()){
            case EDIT:
                dbHandler.updateContact(Contacts.get(longClickedItemIndex));
                break;
            case DELETE:
                dbHandler.deleteContact(Contacts.get(longClickedItemIndex));
                Contacts.remove(longClickedItemIndex);
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK) {
            if(reqCode == 1){
                contactImgAdd.setImageURI(data.getData());
                imgUri = data.getData();
            }
        }
    }

    private void populateList(){
        adapter = new ContactListAdapter();
        contactListView.setAdapter(adapter);
    }

    private class ContactListAdapter extends ArrayAdapter<Contact> {

        public ContactListAdapter() {
            super(MainActivity.this, R.layout.liistview_item, Contacts);
        }

        @Override
        public View getView(int position,  View view, ViewGroup parent){
            if(view == null)
                view = getLayoutInflater().inflate(R.layout.liistview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            ImageView contactImgPhoto = (ImageView) view.findViewById(R.id.imgContactPhoto);
            contactImgPhoto.setImageURI(currentContact.getImgUri());

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.getAddress());

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
