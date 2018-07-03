package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GroupActivity extends AppCompatActivity {
    private String token;
    private String TAG = "GroupActivity";
    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        setupNewGroupButton();
        setupTestButton();

    }

    private void setupNewGroupButton() {
        Button btn = findViewById(R.id.btnNewGroup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToCreate = GroupCreateActivity.makeIntent(GroupActivity.this);
                startActivity(intentToCreate);
            }
        });
    }

    private void setupTestButton() {
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToCreate = GroupTestActivity.makeIntent(GroupActivity.this);
                startActivity(intentToCreate);
            }
        });
    }



    public static Intent makeIntent(Context context){
        return new Intent(context, GroupActivity.class);
    }
}
