package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Show the list of users whom monitors them.
 * Be able to add (jump to AddUserActivity) or remove (click the list) the users who monitors them.
 */

public class MonitorActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_Monitored = 02;
    public static final String MEMBER_ID = "Member_Id";
    private User user;
    private List<User> monitoredByUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long memberId;
    private Session session;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        Intent intent = getIntent();
        memberId = intent.getLongExtra(MEMBER_ID,0);
        if(memberId != 0){
            userId = memberId;

            Button btn = (Button) findViewById(R.id.btnAdd_ed);
            btn.setVisibility(View.GONE);
        }
        populateListView();
        registerClickCallback();
        setAddBtn();
    }




    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.monitor_layout);
        if(bgNumber == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(bgNumber == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(bgNumber == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(bgNumber == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(bgNumber == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(bgNumber == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(bgNumber == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

    }


    private void setAddBtn() {
        Button btn = (Button) findViewById(R.id.btnAdd_ed);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.makeIntent(MonitorActivity.this,"monitored");
                startActivityForResult(intent, REQUEST_CODE_Monitored);
            }
        });
    }

    private void registerClickCallback() {
        TextView tv = (TextView) findViewById(R.id.Inst_monitored);
        if(memberId == 0){
           tv.setText(R.string.click_to_stop_being_monitored);
        }else{
            tv.setText(R.string.click_to_view_monitors_personal_information);
        }
        ListView list = (ListView) findViewById(R.id.listView_Monitored);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(memberId == 0){
                    Call<Void> removeMonitor = proxy.removeFromMonitorsUsers(monitoredByUsers.get(position).getId(),userId);
                    ProxyBuilder.callProxy(MonitorActivity.this, removeMonitor, returnedUser -> responseRemove());
                    Call<User> caller = proxy.getUserById(userId);
                    ProxyBuilder.callProxy(MonitorActivity.this, caller, returnedUser -> response(returnedUser));
                }
                else{
                    Intent intent = UserInfoActivity.makeParentIntent(MonitorActivity.this,monitoredByUsers.get(position).getId());
                    startActivity(intent);
                }
            }
        });
    }

    private void responseRemove() {
        MyToast.makeText(MonitorActivity.this, R.string.requestSend, Toast.LENGTH_LONG).show();
    }

    private void response(User user) {

        populateListView();
    }

    private void populateListView() {
        Call<List<User>> caller_list = proxy.getMonitoredByUsers(userId);
        ProxyBuilder.callProxy(MonitorActivity.this, caller_list, returnedUser -> responseForMonitored(returnedUser));
    }

    private void responseForMonitored(List<User> users) {
        monitoredByUsers = users;
        String[] items = new String[monitoredByUsers.size()];
        for (int i = 0; i < monitoredByUsers.size(); i++) {
            items[i] = monitoredByUsers.get(i).getName() + " - " + monitoredByUsers.get(i).getEmail();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.monitored,items);
        ListView list = (ListView) findViewById(R.id.listView_Monitored);
        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MonitorActivity.class);
    }

    public static Intent makeMemberIntent(Context context, long id) {
        Intent intent =  new Intent(context,MonitorActivity.class);
        intent.putExtra(MEMBER_ID,id);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_Monitored:
                if (resultCode == Activity.RESULT_OK) {
                    session = Session.getInstance();
                    proxy = session.getProxy();
                    user = session.getUser();
                    userId = user.getId();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Call<User> caller = proxy.getUserById(userId);
                    ProxyBuilder.callProxy(MonitorActivity.this, caller, returnedUser -> response(returnedUser));
                }
        }
    }
}
