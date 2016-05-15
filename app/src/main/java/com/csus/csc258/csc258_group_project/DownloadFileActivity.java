package com.csus.csc258.csc258_group_project;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import org.apache.commons.io.IOUtils;
import java.io.File;
import java.util.Arrays;

/*downloads a file contents in a new activity*/
public class DownloadFileActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    /*get this id from your google drive on the web*/
    public String file_id;
    private static final int REQUEST_CODE = 102;
    private GoogleApiClient googleApiClient;
    public static String dirPath;
    public static String backup_dirPath;
    private static final String TAG = "Downloading--";

    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle receiveBundle = this.getIntent().getExtras();
        dirPath = receiveBundle.getString("dirPath");
        backup_dirPath = receiveBundle.getString("backPath");
        File backup_projDir = new File(backup_dirPath);
        File_Compression fc = new File_Compression();
        fc.deleteDirectory(backup_projDir);
        backup_projDir.mkdir();

     /*build the api client*/
        buildGoogleApiClient();
    }
 /*connect client to Google Play Services*/
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "In onStart() - connecting...");
        googleApiClient.connect();
    }
    /*close connection to Google Play Services*/
    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            Log.i(TAG, "In onStop() - disConnecting...");
            googleApiClient.disconnect();
        }
    }
    /*handles onConnectionFailed callbacks*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.i(TAG, "In onActivityResult() - connecting...");
            googleApiClient.connect();
        }
    }
    /*handles connection callbacks*/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG,"Query Started");
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "CSc258_backup.zip"))
                .build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        String temp = null;
                        // Iterate over the matching Metadata instances in mdResultSet
                        Log.i(TAG, "Query success");
                        if (result != null && result.getStatus().isSuccess()) {
                            MetadataBuffer mdb = null;
                            try {
                                mdb = result.getMetadataBuffer();
                                if (mdb != null) for (Metadata md : mdb) {
                                    if (md == null || !md.isDataValid()) continue;
                                    Log.i(TAG, md.getTitle());
                                    if (md.getDriveId() != null) {
                                        temp = md.getWebContentLink().toString();
                                    }
                                    if (temp != null) {

                                        SharedPreferences settings = getApplicationContext().getSharedPreferences(MyPREFERENCES, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("id", temp);
                                        Log.i(TAG, "content link: " + temp);
                                        editor.commit();
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                if (mdb != null) mdb.close();
                            }
                        }
                    }
                });
        SharedPreferences settings = getSharedPreferences(MyPREFERENCES, 0);
        String temp = settings.getString("id","1");
        Log.i(TAG, "content link: "+temp);
        String[] tempp = temp.split("id=");
        String temppp = tempp[1];
        String[] tempppp = temppp.split("&export=");
        file_id = tempppp[0];

        Drive.DriveApi.fetchDriveId(googleApiClient, file_id).setResultCallback(idCallback);
    }

    /*handles suspended connection callbacks*/
    @Override
    public void onConnectionSuspended(int i) {
        Drive.DriveApi.fetchDriveId(googleApiClient, file_id)
                .setResultCallback(idCallback);
    }
    /*callback on getting the drive id, contained in result*/
    final private ResultCallback<DriveIdResult> idCallback = new
            ResultCallback<DriveIdResult>() {
                @Override
                public void onResult(DriveIdResult result) {
                    Log.i(TAG,"Dowloading process starts.");
                    DriveFile file = Drive.DriveApi.getFile(googleApiClient, result.getDriveId());
                    file.open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
                            .setResultCallback(contentsOpenedCallback);
                }
            };
    final  ResultCallback<DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        // display an error saying file can't be opened
                        return;
                    }
                    // DriveContents object contains pointers
                    // to the actual byte stream
                    DriveContents contents = result.getDriveContents();
                    try {
                        byte[] data =  IOUtils.toByteArray(contents.getInputStream());
                        Log.i(TAG,Arrays.toString(data));
                        File_Compression fc = new File_Compression();
                        fc.write(dirPath,data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Toast.makeText(DownloadFileActivity.this,
                            "Rollback Completed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            };

    /*callback when there there's an error connecting the client to the service.*/
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed");
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            Log.i(TAG, "trying to resolve the Connection failed error...");
            result.startResolutionForResult(this, REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }
    /*build the google api client*/
    private void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }
}
