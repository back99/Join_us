package kr.co.company.join_us;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by tofot_000 on 2016-11-04.
 */

public class menu extends AppCompatActivity {

    TextView menu_view;
    Button btn9;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        btn9=(Button)findViewById(R.id.button9);

        final ListView menu_list = (ListView) findViewById(R.id.select_listView);

        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("전체");
        arrayList.add("족구");
        arrayList.add("풋살");
        arrayList.add("농구");
        arrayList.add("탁구");
        arrayList.add("테니스");
        arrayList.add("배드민턴");

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(menu.this,unique.class);
                startActivity(intent2);
                finish();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.ear,arrayList);
        menu_list.setAdapter(adapter);
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(menu.this,board_pan.class);
                intent.putExtra("종목",arrayList.get(position));
                startActivity(intent);
                finish();
            }
        }
        );
    }

    public void onBackPressed() {
        Intent main2 = new Intent(menu.this, MainActivity.class);
        startActivity(main2);
        finish();

    }
}
