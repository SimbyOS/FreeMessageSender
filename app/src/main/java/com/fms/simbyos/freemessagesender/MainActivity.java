package com.fms.simbyos.freemessagesender;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 2354;
    public ProgressBar progressBar;
    public SharedPreferences sPref;
    public boolean isEndCalFlag = false;
    public boolean recall = false;
    ListView contactList;
    LinearLayout linearLayout;
    private ContactAdapter contactAdapter;
    InterstitialAd mInterstitialAd;
    public void LoadAdd(){
        MobileAds.initialize(this, "ca-app-pub-1907837526867283~2333052476");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1907837526867283/4268950670");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (!mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading()) {
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("623B1B7759D51209294A77125459D9B7").addTestDevice("C07AF1687B80C3A74C718498EF9B938A").build());
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.e("mInterstitialAd", String.valueOf(i));
            }

            @Override
            public void onAdLoaded() {
                Log.e("mInterstitialAd", "Loaded");
            }

        });
    }

    private ArrayList<Contact> getContactList(Context ctx) {

        ContentResolver cr = ctx.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        ArrayList<Contact> objects = new ArrayList<Contact>();
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        objects.add(new Contact(name, phoneNo));

                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return objects;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2354: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    AlertDialog.Builder d = new AlertDialog.Builder(this);
                    d.setMessage("Ошибка получения доступа к контактам! Предоставте разрешения в настройках!");
                    d.setTitle("Ошибка");
                    d.setPositiveButton("Выход", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    d.show();
                    finish();

                }
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.contactList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.addfav:
            {
                Contact cont = (Contact) contactList.getAdapter().getItem(info.position);
                String cleanphone = cont.ContactPhone;
                DBHelper dbHelper = new DBHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("name",cont.ContactName);
                cv.put("phone",cont.ContactPhone);
                db.insert("favcont",null,cv);

                return true;
            }

            case R.id.add:

                if (ShortcutManagerCompat.isRequestPinShortcutSupported(getBaseContext())) {

                    Contact cont = (Contact) contactList.getAdapter().getItem(info.position);
                    String cleanphone = cont.ContactPhone;
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
                        Toast.makeText(getBaseContext(), "Введите USSD запрос вашего оператора в настройках!",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    cleanphone = ussd + cleanphone;
                    final Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + cleanphone + Uri.encode("#")));
                    final ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(getApplicationContext(), UUID.randomUUID().toString())
                            .setShortLabel(cont.ContactName)
                            .setLongLabel(cont.ContactName)
                            .setIntent(intent)
                            .build();
                    ShortcutManagerCompat.requestPinShortcut(getApplicationContext(), shortcut, null);

                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.linearLayout = (LinearLayout) findViewById(R.id.linearL);
        setSupportActionBar(toolbar);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE,
                    },
                    PERMISSION_REQUEST_CODE);
            do {
            }
            while ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED));
        }


        this.contactList = (ListView) findViewById(R.id.contactList);
        registerForContextMenu(contactList);
        ContactsLoadTask task = new ContactsLoadTask();
        task.execute();
        LoadAdd();
        this.contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                                                    Snackbar.make(view, "Запрос выполнен.", 3000)
                                                                            .show();
                                                                    if (mInterstitialAd.isLoaded()) {
                                                                        mInterstitialAd.show();
                                                                    } else {
                                                                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                                                                    }
                                                                }
                                                            });
                                                            ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int arg1) {
                                                                    Snackbar.make(view, "Ну, нет так нет.", 3000)
                                                                            .show();
                                                                    if (mInterstitialAd.isLoaded()) {
                                                                        mInterstitialAd.show();
                                                                    } else {
                                                                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                                                                    }
                                                                }
                                                            }).show();



                                                    }
                                                }

        );
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                contactAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                contactAdapter.getFilter().filter(s);
                return false;
            }
        });
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

        }
        if (id == R.id.exitItem) {
            finish();
        }

        if(id == R.id.favcont){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
      Intent intent = new Intent(this,FavoriteContacts.class);
      startActivity(intent);
        }

        if (id == R.id.settingsitem) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class ContactsLoadTask extends AsyncTask<Void, Void, Void> {

        public ArrayList<Contact> temp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            contactList.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            temp = getContactList(getBaseContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            contactAdapter = new ContactAdapter(getBaseContext(), temp);
            contactList.setAdapter(contactAdapter);
            contactList.setVisibility(View.VISIBLE);
            linearLayout.removeView(progressBar);
        }
    }
}
