package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MonitoringActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_Monitoring = 01;
    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        user = User.getInstance();

        setAddBtn();
        registerClickCallback();
        userId = dataToGet.getLong("userId", 0);
        populateListView();
    }

    private void setAddBtn() {
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.makeIntent(MonitoringActivity.this, "monitoring");
                startActivityForResult(intent,REQUEST_CODE_Monitoring);
            }
        });
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Call<Void> remove = proxy.removeFromMonitorsUsers(userId, monitorsUsers.get(position).getId());
                ProxyBuilder.callProxy(MonitoringActivity.this, remove, returnedUser -> responseRemove());
                Call<User> caller = proxy.getUserByEmail(user.getEmail());
                ProxyBuilder.callProxy(MonitoringActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    private void responseRemove() {
    }

    private void response(User user) {
        populateListView();
    }


    private void populateListView() {
        Call<List<User>> caller_list = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(MonitoringActivity.this, caller_list, returnedUser -> responseForMonitoring(returnedUser));
    }

    private void responseForMonitoring(List<User> users) {
        monitorsUsers = users;
        String[] items = new String[monitorsUsers.size()];
        for (int i = 0; i < monitorsUsers.size(); i++) {
            items[i] = monitorsUsers.get(i).getName() + " - " + monitorsUsers.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.monitoring, items);
        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
        list.setAdapter(adapter);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, MonitoringActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_Monitoring:
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
                    token = dataToGet.getString("userToken","");
                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                    user = User.getInstance();
                    Call<User> caller = proxy.getUserByEmail(user.getEmail());
                    ProxyBuilder.callProxy(MonitoringActivity.this, caller, returnedUser -> response(returnedUser));
                }
        }
    }
}

