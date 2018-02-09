package com.fms.simbyos.freemessagesender;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
 class Helper {
    public static void getListViewSize(ListView myListView) {
        ContactAdapter myListAdapter =(ContactAdapter) myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
}
public class FavoriteContacts extends AppCompatActivity {
    public ArrayList<Contact> favcontactsList;
    public ListView contactListView;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Избранные контакты");
        contactListView = (ListView) findViewById(R.id.favContactsListView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabf);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        fg();
        this.contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                Contact temp = (Contact) adapterView.getItemAtPosition(i);
                String cleanphone = temp.ContactPhone;
                cleanphone = cleanphone.replace("+380", "0");
                if (cleanphone.startsWith("380")) {
                    cleanphone = cleanphone.substring(3, cleanphone.length() - 1);
                }
                if (cleanphone.startsWith("80")) {
                    cleanphone = cleanphone.substring(2, cleanphone.length() - 1);
                }
                cleanphone = cleanphone.replace(" ", "");
                cleanphone = cleanphone.replace("-", "");
                cleanphone = cleanphone.replace("(", "");
                cleanphone = cleanphone.replace(")", "");
                sPref = getSharedPreferences("SettingsActivity", MODE_PRIVATE);
                String ussd = sPref.getString("ussd", "");
                if (ussd == "") {
                    Toast.makeText(view.getContext(), "Введите USSD запрос вашего оператора в настройках!",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                    startActivity(intent);
                    return;
                }
                cleanphone = ussd + cleanphone;
                final Intent intent2 = new Intent(Intent.ACTION_CALL);
                intent2.setData(Uri.parse("tel:" + cleanphone + Uri.encode("#")));

                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                }

                //
                String title = "Отправим ...";
                String message = "Отправим бесплатку на номер " + temp.ContactPhone + "?";
                String button1String = "Да";
                String button2String = "Нет";


                AlertDialog.Builder ad;
                ad = new AlertDialog.Builder(view.getContext());
                ad.setTitle(title);  // заголовок
                ad.setMessage(message); // сообщение
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent2);
                    }
                });
                ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(view.getContext(), "Ну , нет так нет.", Toast.LENGTH_LONG)
                                .show();
                    }
                }).show();
            }
        });




    }

    public void fg() {
        favcontactsList = new ArrayList<Contact>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();

        Cursor c = db.query("favcont", null, null, null, null, null, null);
        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int phoneColIndex = c.getColumnIndex("phone");

            do {
                Contact cont = new Contact(c.getInt(idColIndex), c.getString(nameColIndex), c.getString(phoneColIndex));
                this.favcontactsList.add(cont);
            } while (c.moveToNext());
        } else {

        }
        c.close();
        ContactAdapter adapter = new ContactAdapter(this, this.favcontactsList);
        this.contactListView.setAdapter(adapter);
        Helper.getListViewSize(this.contactListView);
        registerForContextMenu(this.contactListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.favContactsListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.fav_context_menu, menu);

        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.deletefromfav: {
            Contact temp   =  (Contact)this.contactListView.getAdapter().getItem(info.position);
                SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
                db.delete("favcont","id=" + temp.id,null);
                db.close();
                fg();

            }
        }
return true;
    }
}