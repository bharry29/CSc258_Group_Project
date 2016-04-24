package com.csus.csc258.csc258_group_project;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
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
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveId;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




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
    private List<Group> mGroups;
    // Google drive parameters
    private GoogleApiClient googleApiClient;
    public static String drive_id;
    public static DriveId driveID;
    private static final int REQUEST_CODE = 101;

    public static String dirPath;
    public static String backup_dirPath;
    public static File projDir;
    public static File backup_projDir;

    public static String groupdirPath;

    private List<GroupFile> mGroupFiles;

    private Group mGroup;

    public List<String> groupNamesList()
    {
        List<String> groupNamesList = new ArrayList<>();
        for (Group g: mGroups) {
            groupNamesList.add(g.getName());
        }
        return groupNamesList;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Check network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Internet Connection")
                    .setMessage("Check your internet connection.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

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

        // Initialize files
        mGroupFiles = new ArrayList<>();
        scanDeviceFolder();

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
        //folder located at /data/data/com.csus.csc258.csc258_group_project/files
        //File foder and backup folder initialization
        dirPath = getFilesDir().getAbsolutePath() + File.separator + device_id;
        projDir = new File(dirPath);
        if (!projDir.exists())
            projDir.mkdirs();

        backup_dirPath = getFilesDir().getAbsolutePath() + File.separator + "backup";
        backup_projDir = new File(backup_dirPath);
        if (!backup_projDir.exists())
            backup_projDir.mkdirs();



        displayView(R.id.nav_group);


//        mFiles = new ArrayList<>();
//        displayView(R.id.nav_file);
    }

    // Register the broadcast receiver with the intent values to be matched
    @Override
    protected void onResume() {
        super.onResume();
        // Register Broadcast Receiver
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
        if (id == R.id.backup) {

            Bundle sendBundle = new Bundle();
            sendBundle.putString("dirPath",dirPath);
            sendBundle.putString("backPath",backup_dirPath);
            Intent intent = new Intent(MainActivity.this, UploadFileActivity.class);
            intent.putExtras(sendBundle);
            startActivity(intent);
            //finish();
            return true;
        }
        if(id == R.id.rollback){
            try{
                File_Compression fc = new File_Compression();
                fc.unzip(new File(backup_dirPath+"/CSC258_backup.zip"), new File(getFilesDir().getAbsolutePath()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

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

    public BroadcastReceiver getReceiver() {
        return mReceiver;
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_group:
                refreshPeers();
                fragment = new GroupView();
                title = "Group";
                break;
            case R.id.nav_file:
                fragment = new GroupFileView();
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

    /**
     * Update the list of groups available to connect to on the group view
     * @param peers the wifi peers that have been discovered
     */
    public void updateGroups(List<WifiP2pDevice> peers) {
        boolean groupsChanged = false;

        // Check each available device
        for(WifiP2pDevice device : peers) {
            boolean newDevice = true;
            // See if we already have the device listed or not
            for(Group g : mGroups)
                newDevice = newDevice && !device.equals(g.getDevice());
            // Add a new group item if it is a new device
            if (newDevice) {
                groupsChanged = true;
                mGroups.add(new Group(device.deviceName, device));
            }
        }

        // Go through each group and see if it is still available
        Iterator<Group> groupIterator = mGroups.iterator();
        while (groupIterator.hasNext()) {
            Group g = groupIterator.next();
            boolean deviceAvailable = false;
            // Check each device
            for (WifiP2pDevice device : peers)
                deviceAvailable = deviceAvailable || device.equals(g.getDevice());

            // If device is no longer available then remove the group
            if (!deviceAvailable) {
                groupsChanged = true;
                groupIterator.remove();
            }
        }

        // Refresh view if there were changes
        if (groupsChanged)
            displayView(R.id.nav_group);
    }

    public void refreshPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully discovered peers");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Cannot discover peers", Toast.LENGTH_SHORT).show();
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

/*        WifiP2pDevice testDevice = new WifiP2pDevice();
        mGroups.add(new Group("test",testDevice));
        mGroups.add(new Group("test2",testDevice));
        mGroups.add(new Group("test3",testDevice));*/

        createDirsForJoinedGroups(getApplicationContext());
    }

    public List<Group> getGroups() {
        return mGroups;
    }

    /**
     * Use for getting the files that are owned by this device
     * @return The files owned by this device
     */
    public List<GroupFile> getGroupFiles() {
        return mGroupFiles;
    }

    public void deleteGroup(Group group) {
        mGroups.remove(group);
        displayView(R.id.nav_group);
    }

    public void addGroup(Group group) {
        mGroups.add(group);
    }

    public void createFile (GroupFile groupfile){mGroupFiles.add(groupfile);}

    public void deleteGroupFile(GroupFile groupfile) {
        groupfile.deleteFile();
        mGroupFiles.remove(groupfile);
        displayView(R.id.nav_file);
    }

    /**
     * Catches the "OK" event from a TextDialog box. This is used for creating new
     * groups
     * @param dialog Reference to the dialog box
     * @param input The text string that the user input
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String input) {
        if (dialog instanceof TextDialogBox) {
            TextDialogBox dialogBox = (TextDialogBox) dialog;
            /* Can no longer create group manually, removing handler logic */
            /*if(dialogBox.getTitle() == getString(R.string.group_create_title)) {
                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                mGroups.add(new Group(GroupStatus.OWNED, input,
                        manager.getConnectionInfo().getMacAddress()));
                displayView(R.id.nav_group);
            }*/
            if(dialogBox.getTitle().equals(getString(R.string.add_file_title))) {
                String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                mGroupFiles.add(new GroupFile(input + ".txt", getApplicationContext(), device_id));
                displayView(R.id.nav_file);
            }
        }
    }

    //To create a new folder in the device after joining a group
    public void createDirsForJoinedGroups(Context mcoContext){
        int grpCount = 0;
        for (Group g: getGroups()) {
            groupdirPath = mcoContext.getFilesDir().getAbsolutePath() + File.separator + Settings.Secure.getString(mcoContext.getContentResolver(), Settings.Secure.ANDROID_ID) + File.separator + mGroups.get(grpCount).getName();
            //groupdirPath = mcoContext.getFilesDir().getAbsolutePath() + File.separator + getPhoneName() + File.separator + mGroups.get(grpCount).getName();
            projDir = new File(groupdirPath);
            if (!projDir.exists())
                projDir.mkdirs();
            grpCount++;
        }
    }

    // This method is added for creating the folder with the device name instead of device id
    public String getPhoneName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        return deviceName;
    }

    /**
     * Looks in the local file system for files that already exist on this device
     * and adds them to the GroupFile list
     */
    private void scanDeviceFolder() {
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        File dir = new File(getApplicationContext().getFilesDir().getAbsolutePath()
                + File.separator + deviceID);
        File[] dirListing = dir.listFiles();
        for (File f : dirListing)
            mGroupFiles.add(new GroupFile(f.getName(), getApplicationContext(), deviceID));
    }
}
