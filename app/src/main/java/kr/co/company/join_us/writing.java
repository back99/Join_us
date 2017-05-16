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
import android.widget.TextView;
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
 * Created by tofot_000 on 2016-11-10.
 */

public class writing extends AppCompatActivity implements View.OnClickListener {

    EditText editTexttitle;
    EditText editTextcontent;
    Button buttonyes;
    Button buttonno;
    String id;
    String sportlist;
    private ProgressDialog ppDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writing);
        editTexttitle = (EditText) findViewById(R.id.editTexttitle);
        editTextcontent = (EditText) findViewById(R.id.editTextcontent);
        buttonno=(Button)findViewById(R.id.button6);
        buttonyes=(Button)findViewById(R.id.button7);

        ppDialog = new ProgressDialog(this);
        ppDialog.setCancelable(false);

        buttonno.setOnClickListener(this);
        buttonyes.setOnClickListener(this);

        id=GlobalApplication.getString(getApplicationContext(),"userid").toString();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            sportlist = bundle.getString("kind");

        }

    }

    private void storedocument(final String id, final String title, final String content, final String sportlist) {
        String tag_string_req = "req_writing";

        ppDialog.setMessage("Ready...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, "http://202.30.23.51/~sap16t10/index.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {


                       /* Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();*/

                        String regMsg = jObj.getString("reg_msg");
                        Toast.makeText(getApplicationContext(),
                                regMsg, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                writing.this,
                                board_pan.class);
                        intent.putExtra("종목",sportlist);
                        startActivity(intent);
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
                params.put("tag", "write");
                params.put("id", id);
                params.put("title", title);
                params.put("content", content);
                params.put("sportlist", sportlist);

                return params;
            }

        };

        // Adding request to request queue
        GlobalApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!ppDialog.isShowing())
            ppDialog.show();
    }

    private void hideDialog() {
        if (ppDialog.isShowing())
            ppDialog.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)

    public void onBackPressed() {
        Intent bord2 = new Intent(writing.this, board_pan.class);
        bord2.putExtra("종목",sportlist);
        startActivity(bord2);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button7:

                String title=editTexttitle.getText().toString();
                String content=editTextcontent.getText().toString();
                if (!title.isEmpty() && !content.isEmpty() ) {
                    storedocument(id,title,content,sportlist);
                } else {
                    Toast.makeText(writing.this, "내용을 입력해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button6:
                editTextcontent.setText("");
                editTexttitle.setText("");
                break;
        }
    }
}
