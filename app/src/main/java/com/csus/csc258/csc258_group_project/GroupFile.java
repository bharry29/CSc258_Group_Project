package com.csus.csc258.csc258_group_project;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by bharr on 3/1/2016.
 */
    public class GroupFile
    {
        private String mFileName;
        private String mPath;
        private MainActivity mActivity;

        // The id of the file
        private int mFId;

        /**
         * Create a new file object
         * @param name The name of the file
         * @param activity The main activity window (used for displaying Toast messages)
         */
        public GroupFile(String name, MainActivity activity) {
            mFileName = name;
            mActivity = activity;
            mPath = mActivity.getFilesDir().getAbsolutePath() + File.separator + getDeviceId(mActivity);
            mFId = View.generateViewId();
            writeFileOnInternalStorage("Sample Body");
        }

        /**
         * Gets the name of the group
         * @return The name of the group
         */
        public String getFileName() {
            return mFileName;
        }

        /**
         * Returns the path to the file
         * @return The path to the file
         */
        public String getFilePath() { return mPath; }

        public int getFileId() { return mFId; }

        /**
         * Deletes the file on the file system
         */
        public void deleteFile() {
            deleteFileFromInternalStorage();
        }

        private void writeFileOnInternalStorage(String sBody){
            //File samplefiledir = new File(mcoContext.getFilesDir(),"sampledata");
            File samplefiledir = new File(mPath);
            Boolean write_successful = false;
            if(!samplefiledir.exists()){
                samplefiledir.mkdir();
            }
            try{
                File samplefile = new File(samplefiledir, mFileName);
                FileWriter writer = new FileWriter(samplefile);
                writer.append(sBody);
                writer.flush();
                writer.close();
                write_successful = true;
                Toast.makeText(mActivity.getApplicationContext(), "Sample File Created Successfully in : " + samplefiledir + "\t with the name:\t" + mFileName, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR:---", "Could not write file" + e.getMessage());
                write_successful = false;
            }
        }

        private void deleteFileFromInternalStorage(){
            //File samplefiledir = new File(mcoContext.getFilesDir(),"sampledata");
            File samplefiledir = new File(mPath);
            Boolean isDeleted = null;
            File f = new File(mPath,mFileName);
            try {
                if(f.exists()) {
                    f.delete();
                    isDeleted = true;
                    Toast.makeText(mActivity.getApplicationContext(), "Sample File Deleted Successfully from : " + samplefiledir + "\t with the name:\t" + mFileName, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    isDeleted = false;
                    Toast.makeText(mActivity.getApplicationContext(), "Could not delete file", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                Toast.makeText(mActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                isDeleted = false;
                Log.e("ERROR:---", "Could not delete file" + e.getMessage());
            }
        }

        //TODO
        private void renameGroupFile(String sFilename){
            Context mcoContext = mActivity.getApplicationContext();
            //File samplefiledir = new File(mcoContext.getFilesDir(),sFilename);
            String device_id = getDeviceId(mcoContext);
            File samplefiledir = new File(mcoContext.getFilesDir().getAbsolutePath() + File.separator + device_id);
            samplefiledir.renameTo(samplefiledir);
        }

        private String getDeviceId (Context context)
        {
            String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return device_id;
        }
    }
