package com.csus.csc258.csc258_group_project;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TextDialogBox.TextDialogListener {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static String device_id ;
    public static View headerview;

    // For debug
    private static final String TAG = "MainActivity";

    // Used for WiFi framework
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private boolean mWifiP2PEnabled = false;

    // Collection of groups
    private ArrayList<Group> mGroups;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup WiFi framework
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new GroupBroadcastReceiver(mManager, mChannel, this);

        // WiFi intents for the broadcast receiver to look for
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Initialize groups
        mGroups = new ArrayList<>();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerview = navigationView.getHeaderView(0);
        device_id=Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                SharedPreferences setting = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String username = setting.getString("username", device_id);
                TextView user_id = (TextView)headerview.findViewById(R.id.user_id);
                user_id.setText(username);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        //setContentView(R.layout.nav_header_main);

        displayView(R.id.nav_group);
    }

    // Register the broadcast receiver with the intent values to be matched
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    // Unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_group) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.nav_group) {
            displayView(R.id.nav_group);
        } else if (id == R.id.nav_file) {
            displayView(R.id.nav_file);
        } else if (id == R.id.nav_settings) {
            displayView(R.id.nav_settings);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);



        return true;
    }

    public void setIsWifiP2PEnabled(boolean wifiP2PEnabled) {
        mWifiP2PEnabled = wifiP2PEnabled;
    }

    public boolean getIsWifiP2PEnabled() {
        return mWifiP2PEnabled;
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_group:
                fragment = new GroupView();
                title = "Group";
                break;
            case R.id.nav_file:
                fragment = new file();
                title = "File";
                break;
            case R.id.nav_settings:
                fragment = new setting();
                title = "Setting";
                break;

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public void refreshPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully discovered peers");
            }

            @Override
            public void onFailure(int reason) {
                switch (reason) {
                    case WifiP2pManager.BUSY:
                        Log.w(TAG, "Cannot discover peers: Busy");
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.w(TAG, "Cannot discover peers: P2P Unsupported");
                        break;
                    case WifiP2pManager.ERROR:
                        Log.w(TAG, "Cannot discover peers: Error");
                        break;
                    default:
                        Log.w(TAG, "Cannot discover peers: Unknown Issue");
                }
            }
        });
    }

    public ArrayList<Group> getGroups() {
        return mGroups;
    }

    public void deleteGroup(Group group) {
        mGroups.remove(group);
        displayView(R.id.nav_group);
    }

    /**
     * Catches the "OK" event from a TextDialog box. This is used for creating new
     * groups
     * @param dialog Reference to the dialog box
     * @param input The text string that the user input
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String input) {
        mGroups.add(new Group(GroupStatus.OWNED, input,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
        displayView(R.id.nav_group);
    }
}
