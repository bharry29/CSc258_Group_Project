package com.csus.csc258.csc258_group_project;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Representation of a file that is owned by a group
 */
    public class GroupFile
    {
        private String mFileName;
        private String mPath;
        private Context mContext;

        // The id of the file
        private int mFId;

        private static final String TAG = "GroupFile";

        /**
         * Create a new file object
         * @param name The name of the file
         * @param context The application context (used for toast messages)
         * @param deviceID The name of the device (for subfolder creation)
         */
        public GroupFile(String name, Context context, String deviceID, String content) {
            mFileName = name;
            mContext = context;
            mPath = context.getFilesDir().getAbsolutePath() + File.separator + deviceID;
            mFId = View.generateViewId();
            if (!fileExistsInternalStorage())
                writeFileOnInternalStorage(content);
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

        public File getFileObject() { return new File(mPath, mFileName);}

        public int getFileId() { return mFId; }

        /**
         * Returns true if the file exists on internal storage
         * @return True if the file exists on internal storage, false otherwise
         */
        public boolean fileExistsInternalStorage() {
            File f = new File(mPath, mFileName);
            return f.exists();
        }

        /**
         * Deletes the file on the file system
         * @return True if successful, false otherwise
         */
        public boolean deleteFile() {
            if (fileExistsInternalStorage())
                return deleteFileFromInternalStorage();
            else
                return false;
        }

        public void createFromInputStream(DataInputStream is) {
            File f = new File(mPath, mFileName);
            OutputStream output = null;
            try {
                try {
                    output = new FileOutputStream(f);
                    byte[] buffer = new byte[1024];
                    int read;

                    while((read = is.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                }
                catch (IOException e) {
                    Log.w(TAG, "createFromInputStream: Couldn't create file from stream.");
                } finally {
                    if(output != null)
                        output.close();
                }

            } catch (FileNotFoundException e) {
                Log.w(TAG, "createFromInputStream: File not found " + mFileName);
                e.printStackTrace();
            } catch (IOException e) {
                Log.w(TAG, "createFromInputStream: Error closing file stream " + mFileName);
                e.printStackTrace();
            }
        }

        /**
         * Writes the file to the internal storage
         * @param sBody The text content of the file
         * @return True if successful, false otherwise
         */
        private boolean writeFileOnInternalStorage(String sBody){
            File fileDir = new File(mPath);
            Boolean write_successful = false;
            sBody = sBody == null ? "" : sBody;
            if(!fileDir.exists()){
                fileDir.mkdir();
            }
            try{
                File samplefile = new File(fileDir, mFileName);
                FileWriter writer = new FileWriter(samplefile);
                writer.append(sBody);
                writer.flush();
                writer.close();
                write_successful = true;
                Toast.makeText(mContext, "Sample File Created Successfully in : " + fileDir + "\t with the name:\t" + mFileName, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Log.e(TAG, "Could not write file" + e.getMessage());
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return write_successful;
        }

        private boolean deleteFileFromInternalStorage(){
            File fileDir = new File(mPath);
            Boolean isDeleted = null;
            File f = new File(mPath,mFileName);
            try {
                if(f.exists()) {
                    f.delete();
                    isDeleted = true;
                    Toast.makeText(mContext, "Sample File Deleted Successfully from : " + fileDir + "\t with the name:\t" + mFileName, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    isDeleted = false;
                    Toast.makeText(mContext, "Could not delete file", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                isDeleted = false;
                Log.e(TAG, "Could not delete file" + e.getMessage());
            }

            return isDeleted;
        }

        //TODO
        private void renameGroupFile(String sFilename){
            File fileDir = new File(mPath);
            fileDir.renameTo(fileDir);
        }
    }
