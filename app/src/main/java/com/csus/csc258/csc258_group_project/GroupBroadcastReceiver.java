package com.csus.csc258.csc258_group_project;

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

    // For debug
    private static final String TAG = "GroupBroadcastReceiver";

    private List mPeers = new ArrayList();

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
            updatePeers();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo != null && networkInfo.isConnected()) {
                // We are connected with the other device, request connection info
                // to find group owner IP
                mManager.requestConnectionInfo(mChannel, this);
            }
            else {
                Log.d(TAG, "Connection Closed");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    private void updatePeers() {
        for(Object o : mPeers) {
            final WifiP2pDevice device = (WifiP2pDevice) o;

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Successfully connected to peer " + device.deviceName);
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(mActivity, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Create server thread

        } else if (info.groupFormed) {
            // Create client thread

            //new SocketServerTask().doInBackground()
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        mPeers.clear();
        if(peerList != null && peerList.getDeviceList() != null)
            mPeers.addAll(peerList.getDeviceList());

        if(mPeers.size() == 0)
            Log.d(TAG, "No devices found");
    }
}
