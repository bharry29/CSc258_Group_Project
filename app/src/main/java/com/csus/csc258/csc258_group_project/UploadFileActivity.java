package com.csus.csc258.csc258_group_project;

        import android.app.Activity;
        import android.content.Intent;
        import android.content.IntentSender;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.PendingResult;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.common.api.ResultCallbacks;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.drive.Drive;
        import com.google.android.gms.drive.DriveApi;
        import com.google.android.gms.drive.DriveApi.DriveContentsResult;
        import com.google.android.gms.drive.DriveContents;

        import com.google.android.gms.drive.DriveFile;
        import com.google.android.gms.drive.DriveFolder.DriveFileResult;
        import com.google.android.gms.drive.DriveId;
        import com.google.android.gms.drive.DriveResource;
        import com.google.android.gms.drive.Metadata;
        import com.google.android.gms.drive.MetadataBuffer;
        import com.google.android.gms.drive.MetadataChangeSet;
        import com.google.android.gms.drive.query.Filters;
        import com.google.android.gms.drive.query.Query;
        import com.google.android.gms.drive.query.SearchableField;


        import java.io.BufferedInputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.OutputStream;
/**
 * upload a text file from device to Drive
 */
public class UploadFileActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "upload_file";
    private static final int REQUEST_CODE = 101;
    private GoogleApiClient googleApiClient;
    public static String dirPath;
    public static String backup_dirPath;
    public static String drive_id;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static DriveId driveID;
    public static String  rid;
    public static String id;
    private File Upload_File;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Legal requirements if you use Google Drive in your app: "
                        + GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this)
        );
        Bundle receiveBundle = this.getIntent().getExtras();
        dirPath = receiveBundle.getString("dirPath");
        backup_dirPath = receiveBundle.getString("backPath");
        File backup_projDir = new File(backup_dirPath);
        File_Compression fc = new File_Compression();
        fc.deleteDirectory(backup_projDir);
        backup_projDir.mkdir();
        fc.zipFileAtPath(dirPath, backup_dirPath+"/CSC258_backup.zip");
        //Google Drive Api Initialization
        // the text file in our device's Download folder
         Upload_File = new File(backup_dirPath+"/CSC258_backup.zip");
         /*build the api client*/
         buildGoogleApiClient();

        //finish();
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
    /*Handles onConnectionFailed callbacks*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.i(TAG, "In onActivityResult() - connecting...");
            googleApiClient.connect();
        }
    }
    /* *//*handles connection callbacks*/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "in onConnected() - we're connected, let's do the work in the background...");

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "CSc258_backup.zip"))
                .build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {


                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        // Iterate over the matching Metadata instances in mdResultSet
                        Log.i(TAG, "Query success");
                        if (result != null && result.getStatus().isSuccess()) {
                            MetadataBuffer mdb = null;
                            try {
                                mdb = result.getMetadataBuffer();
                                if (mdb != null) for (Metadata md : mdb) {
                                    if (md == null || !md.isDataValid()) continue;
                                    Log.i(TAG, md.getTitle());
                                    if (md.getDriveId() != null)
                                        id = md.getDriveId().toString();
                                    Log.i(TAG, id);
                                    //md.get.....();
                                    DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient, DriveId.decodeFromString(id));
                                    // Call to delete file.
                                    driveFile.delete(googleApiClient);
                                }
                            } finally {
                                if (mdb != null) mdb.close();
                            }
                        }
                       }
                });



        Drive.DriveApi.newDriveContents(googleApiClient)
        .setResultCallback(driveContentsCallback);
    }
    /*handles suspended connection callbacks*/
    @Override
    public void onConnectionSuspended(int cause) {
        switch (cause) {
            case 1:
                Log.i(TAG, "Connection suspended - Cause: " + "Service disconnected");
                break;
            case 2:
                Log.i(TAG, "Connection suspended - Cause: " + "Connection lost");
                break;
            default:
                Log.i(TAG, "Connection suspended - Cause: " + "Unknown");
                break;
        }
    }
    /*callback on getting the drive contents, contained in result*/
    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Error creating new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();
                    new Thread() {
                        @Override
                        public void run() {
                            OutputStream outputStream = driveContents.getOutputStream();
                            addTextfileToOutputStream(outputStream);
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("CSc258_backup.zip")
                                    .setMimeType("application/zip")
                                    .setDescription("This is a zip file uploaded from device")
                                    .setStarred(true).build();
                            Drive.DriveApi.getRootFolder(googleApiClient)
                                    .createFile(googleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };
    /*get input stream from text file, read it and put into the output stream*/
    private void addTextfileToOutputStream(OutputStream outputStream) {
        Log.i(TAG, "adding text file to outputstream...");
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            BufferedInputStream inputStream = new BufferedInputStream(
                    new FileInputStream(Upload_File));
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.i(TAG, "problem converting input stream to output stream: " + e);
            e.printStackTrace();
        }
    }
    /*callback after creating the file, can get file info out of the result*/
    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
                @Override
                public void onResult(DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Error creating the file");
                        Toast.makeText(UploadFileActivity.this,
                                "Error adding file to Drive", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, "File added to Drive");
                    Log.i(TAG, "Created a file with content: "
                            + result.getDriveFile().getDriveId());




                   /* SharedPreferences setting = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    if(setting.getString("Resource_id", null) == null){


                        String pageToken = null;
                        do {
                            FileList result = driveService.files().list()
                                    .setQ("mimeType='image/jpeg'")
                                    .setSpaces("drive")
                                    .setFields("nextPageToken, files(id, name)")
                                    .setPageToken(pageToken)
                                    .execute();
                            for(File file: result.getFiles()) {
                                System.out.printf("Found file: %s (%s)\n",
                                        file.getName(), file.getId());
                            }
                            pageToken = result.getNextPageToken();
                        } while (pageToken != null);



                        Log.i(TAG, "Previous Version not found.");
                        SharedPreferences.Editor editor = setting.edit();
                        editor.putString("Resource_id", result.getDriveFile().getDriveId().toString());
                        editor.commit();
                    } else {
                        Log.i(TAG, "Previous Version deleted.");
                        String temp = setting.getString("Resource_id", null);
                        DriveFile driveFile = Drive.DriveApi.getFile(googleApiClient,
                                DriveId.decodeFromString(temp));
                        // Call to delete file.
                        driveFile.trash(googleApiClient).setResultCallback(new ResultCallbacks<Status>() {
                            @Override
                            public void onSuccess(Status status) {
                                Log.i(TAG, "Deletion success");
                            }

                            @Override
                            public void onFailure(Status status) {
                                Log.i(TAG, "Deletion failed");
                            }
                        });
                    }*/



                    Toast.makeText(UploadFileActivity.this,
                            "File successfully added to Drive", Toast.LENGTH_SHORT).show();
                    final PendingResult<DriveResource.MetadataResult> metadata
                            = result.getDriveFile().getMetadata(googleApiClient);
                    metadata.setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(DriveResource.MetadataResult metadataResult) {
                        Metadata data = metadataResult.getMetadata();
                        Log.i(TAG, "Title: " + data.getTitle());
                        drive_id = data.getDriveId().encodeToString();
                        Log.i(TAG, "DrivId: " + drive_id);
                        driveID = data.getDriveId();
                        Log.i(TAG, "Description: " + data.getDescription().toString());
                        Log.i(TAG, "MimeType: " + data.getMimeType());
                        Log.i(TAG, "File size: " + String.valueOf(data.getFileSize()));
                    }
                    });
                    onStop();
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