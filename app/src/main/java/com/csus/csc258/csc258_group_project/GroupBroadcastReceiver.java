package com.csus.csc258.csc258_group_project;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 2/18/2016.
 */
public class GroupBroadcastReceiver extends BroadcastReceiver implements
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.PeerListListener {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private ExchangeGroupsClient mExchangeGroupsClient;
    private ExchangeGroupsServer mExchangeGroupsServer;
    private String mDeviceName = "";

    // For debug
    private static final String TAG = "GroupBroadcastReceiver";

    private List<WifiP2pDevice> mPeers = new ArrayList();

    public GroupBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                  MainActivity activity) {
        super();
        mManager = manager;
        mChannel = channel;
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        NetworkInfo networkInfo;

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.setIsWifiP2PEnabled(true);
            } else {
                mActivity.setIsWifiP2PEnabled(false);
            }
            Log.d(TAG, "Wifi State: " + (mActivity.getIsWifiP2PEnabled() ? "Enabled" : "Disabled"));
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mManager.requestPeers(mChannel, this);
            Log.d(TAG, "P2P peers changed");

            // Respond to new connection or disconnections
            networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo != null && networkInfo.isConnected()) {
                // We are connected with the other device, request connection info
                // to find group owner IP
                Log.d(TAG, "P2P peers changed and connected, requesting info");
                mManager.requestConnectionInfo(mChannel, this);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Respond to new connection or disconnections
            networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo != null && networkInfo.isConnected()) {
                // We are connected with the other device, request connection info
                // to find group owner IP
                Log.d(TAG, "We ARE connected with other devices, requesting info");
                mManager.requestConnectionInfo(mChannel, this);
            }
            else {
                Log.d(TAG, "Not connected with other devices");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Set current device name
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            mDeviceName = device.deviceName;
        }
    }

    public void connectToDevice(WifiP2pDevice device, WifiP2pManager.ActionListener listener) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0; // We want to connect as the client, not owner

        mManager.connect(mChannel, config, /*new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully connected to peer " + device.deviceName);
            }
            @Override

            public void onFailure(int reason) {
                Toast.makeText(mActivity, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        }*/ listener);

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        // Is there an open server socket? Cancel it if group owner so we can create a new one
        if (mExchangeGroupsServer != null && !mExchangeGroupsServer.isCancelled() && info.isGroupOwner) {
            Log.d(TAG, "Found open server socket, cancelling");
            mExchangeGroupsServer.cancel(true);
        }

        // After the group negotiation, we can determine the group owner.
        if(info.groupFormed) {
            if (info.isGroupOwner) {
                Log.d(TAG, "Current device is group owner, setting up server");
                mExchangeGroupsServer = new ExchangeGroupsServer(8888, mActivity, mDeviceName);
                mExchangeGroupsServer.execute();

            } else {
                Log.d(TAG, "Current device is not group owner, connecting to owner as client");
                mExchangeGroupsClient = new ExchangeGroupsClient(info.groupOwnerAddress, 8888, mActivity);
                mExchangeGroupsClient.execute();
            }
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        mPeers.clear();
        if(peerList != null && peerList.getDeviceList() != null) {
            mPeers.addAll(peerList.getDeviceList());
            mActivity.updateGroups(mPeers);
        }

        if(mPeers.size() == 0)
            Log.d(TAG, "No devices found");
    }
}
