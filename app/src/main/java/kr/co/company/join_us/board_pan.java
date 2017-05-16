package kr.co.company.join_us;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by tofot_000 on 2016-11-07.
 */

public class board_pan extends AppCompatActivity implements View.OnClickListener {
    Button button4;
    Button button5;
    private ListView mainlistview;
    String kind;
    private List<global> Uploadlist;

    TextView emptyview;
    UploadListAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_pan);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);

        emptyview = (TextView) findViewById(R.id.empty_view);
        mainlistview = (ListView) findViewById(R.id.mylist);

        TextView kinds = (TextView) findViewById(R.id.kind);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            kind = bundle.getString("종목");
            kinds.setText(kind);
         }
        if(kind.equals("전체")){

        new JSONTask().execute("http://202.30.23.51/~sap16t10/register.php");//전체를 클릭했을시 서버에 접속하는것과
        }
            else {

                new JSONTask().execute("http://202.30.23.51/~sap16t10/select.php");//운동 종목을 딱 하나만 클릭했을시 다름!
            }

    }

    public void onBackPressed() {
        Intent bord2 = new Intent(board_pan.this, menu.class);
        startActivity(bord2);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:
                if(kind.equals("전체")){
                    Toast.makeText(this, "전체 목록에서는 글을 작성하실수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent writ = new Intent(board_pan.this, writing.class);
                    writ.putExtra("kind", kind);
                    startActivity(writ);
                    finish();
                }
                break;

            case R.id.button5:
                Intent bord3 = new Intent(board_pan.this, menu.class);
                startActivity(bord3);
                finish();
                break;
        }
    }
    public class JSONTask extends AsyncTask<String, String, List<global>> {

        @Override
        protected List<global> doInBackground(String... params) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("select", kind));



            try {
                httppost.setEntity(new UrlEncodedFormEntity(parameter, "UTF-8"));
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try{
                HttpResponse response = httpclient.execute(httppost);

                if (response != null) {
                    InputStream is = response.getEntity().getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJson = buffer.toString();

                    JSONObject parentObject = new JSONObject(finalJson);
                    JSONArray parentArray = parentObject.getJSONArray("docu");

                    List<global> uploadList = new ArrayList<>();

                    Gson gson = new Gson();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        global menulist = gson.fromJson(finalObject.toString(), global.class);

                        uploadList.add(menulist);
                    }
                    return uploadList;

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(final List<global> result) {
            super.onPostExecute(result);

            if(!result.isEmpty()) {

                adapter = new board_pan.UploadListAdapter(getApplicationContext(), R.layout.detail, result);
                mainlistview.setAdapter(adapter);
                mainlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            Intent intentdocu=new Intent(board_pan.this,document.class);
                                                            intentdocu.putExtra("번호",Uploadlist.get(position).getNum());
                                                            intentdocu.putExtra("kind",kind);
                                                            //해당 글 클릭시 번호와 종류를 가지고 넘어감!

                                                            startActivity(intentdocu);
                                                            finish();
                                                        }
                                                    }
                );
            } else {

                mainlistview.setEmptyView(emptyview);
            }
        }
    }

    //@TargetApi(Build.VERSION_CODES.CUPCAKE)


    public class UploadListAdapter extends ArrayAdapter {

        private int resource;
        private LayoutInflater inflater;


        public UploadListAdapter(Context context, int resource, List<global> objects) {
            super(context, resource, objects);
            Uploadlist = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){//받아온 배열별로 리스트뷰에 저장 시키기
            ViewHolder holder=null;

            if(convertView==null) {
                holder = new ViewHolder();

                convertView = inflater.inflate(resource, null);

                holder.pan_id = (TextView) convertView.findViewById(R.id.pan_id);
                holder.pan_title = (TextView) convertView.findViewById(R.id.pan_title);
                holder.pan_sportlist = (TextView) convertView.findViewById(R.id.pan_sportmenu);
                holder.pan_date = (TextView) convertView.findViewById(R.id.pan_date);
                holder.pan_num = (TextView) convertView.findViewById(R.id.pan_num);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder  )convertView.getTag();
            }

            holder.pan_id.setText(Uploadlist.get(position).getId());
            holder.pan_title.setText(Uploadlist.get(position).getTitle());
            holder.pan_sportlist.setText(Uploadlist.get(position).getSportlist());
            holder.pan_date.setText(Uploadlist.get(position).getDate());
            holder.pan_num.setText(Uploadlist.get(position).getNum());

            return convertView;
        }
    }
    class ViewHolder{

        private TextView pan_id;
        private TextView pan_title;
        private TextView pan_sportlist;
        private TextView pan_date;
        private TextView pan_num;
    }
}