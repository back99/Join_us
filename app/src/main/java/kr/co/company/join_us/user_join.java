package kr.co.company.join_us;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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

public class user_join extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    Button btn;

    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_join);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.DKGRAY);
        }

        btn = (Button) findViewById(R.id.button3);
        btn.setOnClickListener(this);

        editText1 = (EditText) findViewById(R.id.id);
        editText2 = (EditText) findViewById(R.id.password);
        editText3 = (EditText) findViewById(R.id.name);
        editText4 = (EditText) findViewById(R.id.phonenumber);
        editText5 = (EditText) findViewById(R.id.address);
        editText6 = (EditText) findViewById(R.id.password2);
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(user_join.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    private void registerUser(final String id, final String password, final String name, final String phonenumber, final String address) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Ready...");
        showDialog();

        //서버에 연결
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://202.30.23.51/~sap16t10/index.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다. 환영합니다!", Toast.LENGTH_SHORT).show();

                        String regMsg = jObj.getString("reg_msg");
                        Toast.makeText(getApplicationContext(),
                                regMsg, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                user_join.this,
                                MainActivity.class);
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
                params.put("tag", "register");
                params.put("id", id);
                params.put("password", password);
                params.put("name", name);
                params.put("phonenumber", phonenumber);
                params.put("address", address);

                //회원정보 서버로 보내기


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
        switch (v.getId()) {

            case R.id.button8:
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
                editText4.setText("");
                editText5.setText("");
                editText6.setText("");

                break;

            case R.id.button3:

                String id = editText1.getText().toString();
                String password = editText2.getText().toString();
                String name = editText3.getText().toString();
                String phonenumber = editText4.getText().toString();
                String address = editText5.getText().toString();
                String password2 = editText6.getText().toString();


                if (!(password.equals(password2)))
                    Toast.makeText(user_join.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                else {

                    if (!id.isEmpty() && !name.isEmpty() && !password.isEmpty()&&!phonenumber.isEmpty()&&!address.isEmpty()) {
                        registerUser(id,password,name,phonenumber,address);
                    } else {
                        Toast.makeText(user_join.this, "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }
}
