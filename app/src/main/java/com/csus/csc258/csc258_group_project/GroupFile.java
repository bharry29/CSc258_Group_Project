package com.csus.csc258.csc258_group_project;

import android.view.View;

/**
 * Created by bharr on 3/1/2016.
 */
    public class GroupFile
    {
        private String mFileName;

        // The id of the file
        private int mFId;

        /**
         * Create a new file object
         * @param name The name of the file
         */
        public GroupFile(String name) {
            mFileName = name;
            mFId = View.generateViewId();
        }

        /**
         * Gets the name of the group
         * @return The name of the group
         */
        public String getFileName() {
            return mFileName;
        }

        public int getFileId() { return mFId; }
    }
