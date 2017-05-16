package kr.co.company.join_us;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tofot_000 on 2016-11-26.
 */

public class reply extends AppCompatActivity implements  View.OnClickListener{

    Button btnrply;
    Button btncan;
    String id;
    String reply;
    EditText reply_content;
    String kind;
    String number;

    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btncan=(Button)findViewById(R.id.button10);
        btnrply=(Button)findViewById(R.id.buttonrply);
        btncan.setOnClickListener(this);
        btnrply.setOnClickListener(this);
        reply_content = (EditText)findViewById(R.id.reply_content);
        id=GlobalApplication.getString(getApplicationContext(),"userid").toString();



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            number=bundle.getString("num");
            kind=bundle.getString("종목");

          //  Toast.makeText(this,kind, Toast.LENGTH_SHORT).show();
        }

    }

    private void replyStore(final String id, final String reply,final String number) {
        String tag_string_req = "comment_writing";

        pDialog.setMessage("Ready...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, "http://202.30.23.51/~sap16t10/index.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                       String regMsg = jObj.getString("reg_msg");
                        Toast.makeText(getApplicationContext(),
                                regMsg, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intentrply = new Intent(
                                reply.this,
                                document.class);
                        intentrply.putExtra("kind",kind);
                        intentrply.putExtra("번호",number.toString());
                        //다시 뒤로 돌아갈때도 글번호 가지고 가야 하고, 또한 스포츠 종목도 가지고 가야함!!!
                        startActivity(intentrply);
                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "reple");
                params.put("id", id);
                params.put("reply", reply);
                params.put("number",number);

                return params;
            }
        };

        // Adding request to request queue
        GlobalApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonrply:
                reply=reply_content.getText().toString();
                //아이디 저장해둬야함 위에서 해줬음!!!
                if(!reply.isEmpty()){
                    replyStore(id,reply,number);
                }
                else{

                    Toast.makeText(reply.this, "댓글을 작성해 주십시오.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button10:
                reply_content.setText("");
                break;
        }
    }
    public void onBackPressed(){
        Intent intent=new Intent(reply.this,document.class);
        intent.putExtra("kind",kind);
        intent.putExtra("번호",number);
        startActivity(intent);
        finish();
    }

}
