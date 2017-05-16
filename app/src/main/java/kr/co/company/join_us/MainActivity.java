package kr.co.company.join_us;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button button;
    Button button2;
    EditText id;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id=(EditText)findViewById(R.id.loginid);
        password=(EditText)findViewById(R.id.loginpassword);

        button = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button2:
                Intent i = new Intent(MainActivity.this,user_join.class);//회원가입 페이지로 넘어가기
                startActivity(i);
                finish();
                break;
            case R.id.button:
                String id1=id.getText().toString();
                String password1=password.getText().toString();

                if(!id1.isEmpty()&&!password1.isEmpty()){
                    loginUser(id1,password1);//입력받은 아이디 와 비밀번호 보내기
                }
                else{//아이디와 비밀번호 잘못 입력
                    Toast.makeText(MainActivity.this,"id와 password가 일치 하지 않습니다.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void loginUser(final String id,
                           final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://202.30.23.51/~sap16t10/index.php", new Response.Listener<String>() {//서버에 연결

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String name = jObj.getString("username");
                        String id=jObj.getString("userid");
                        GlobalApplication.setString(getApplicationContext(),"username", name);
                        GlobalApplication.setString(getApplicationContext(),"userid", id);

                        // Launch login activity

                        Intent intentmenu = new Intent(
                                MainActivity.this,
                                menu.class);
                        startActivity(intentmenu);
                        finish();//비밀번호와 아이디가 맞으면 다음페이지로 넘어가기

                    } else {
                        Toast.makeText(MainActivity.this, "id와 password가 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
                        // Error occurred in registration. Get the error
                        // message

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
                params.put("tag", "login");
                params.put("id", id);
                params.put("password", password);//아이디와 비밀번호 받아오기

                return params;
            }

        };

        // Adding request to request queuegg
        GlobalApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
