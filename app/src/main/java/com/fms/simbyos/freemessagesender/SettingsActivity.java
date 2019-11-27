package com.fms.simbyos.freemessagesender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    public SharedPreferences sPref;
    ListView settingsListView;
    AdapterView.OnItemClickListener listener;
    private String m_Text = "";

    {
        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                switch (i) {
                    case 0: {
                        Intent intent = new Intent(getBaseContext(), operatorselector.class);
                        startActivityForResult(intent, 1);
                        break;
                    }
                    case 1: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("USSD");
                        builder.setMessage("Введите начало USSD запроса , например *104*.");
// Set up the input
                        final EditText input = new EditText(view.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        sPref = getPreferences(MODE_PRIVATE);
                        input.setText(sPref.getString("ussd", ""));
                        builder.setView(input);

// Set up the buttons
                        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString();
                                sPref = getPreferences(MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString("ussd", m_Text);
                                ed.commit();
                                Toast.makeText(view.getContext(), "Сохранено!", Toast.LENGTH_SHORT).show();


                            }
                        });
                        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();


                        break;
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String ussd = data.getStringExtra("ussd");
        m_Text = ussd;
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("ussd", m_Text);
        ed.commit();
      //  Snackbar.make(this.getCurrentFocus(),"Сохранено! USSD : " + ussd,Snackbar.LENGTH_SHORT);
       Toast.makeText(this, "Сохранено! USSD : " + ussd, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.settingsListView = (ListView) findViewById(R.id.settingsList);
        ArrayList<String> settingsHeader = new ArrayList<String>();
        settingsHeader.add("Выбрать оператора");
        settingsHeader.add("Ввести вручную USSD код");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, settingsHeader);
        this.settingsListView.setAdapter(adapter);

        this.settingsListView.setOnItemClickListener(this.listener);


    }
}
