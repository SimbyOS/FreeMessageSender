package com.fms.simbyos.freemessagesender;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class operatorselector extends AppCompatActivity {
    public ListView operatorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operatorselector);
        setTitle("Выбор оператора");
        this.operatorList = (ListView) findViewById(R.id.operatorList);
        String[] names = {"Vodafone (Украина)", "Lifecell", "МТС Россия", "Beeline", "Киевстар", "Мегафон",
                "ТЕЛЕ2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        this.operatorList.setAdapter(adapter);

        this.operatorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*104*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*124*3*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*110*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    case 3: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*144*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    case 4: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*130*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    case 5: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*144*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                    case 6: {
                        Intent intent = new Intent();
                        intent.putExtra("ussd", "*118*");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                }
            }
        });

    }
}
