package com.myurlname.stepup;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is a bean which holds both an input stream (data) and String (file type)
 * information for an image.  Useful for the ProfilePictureFactory as a return
 * object.
 * @author Gabriel
 */
public class ProfilePicDataAndType {
    private InputStream data;
    private String contentType;
    
    public ProfilePicDataAndType (InputStream data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }    
    
    public ProfilePicDataAndType () {
        
    }
    
    /** Attempts to close the input stream whilst ignoring any and all exceptions.
     * Currently, close() does nothing on InputStream, but as this is an innocuous call
     * and is good from a hygiene perspective, it stays implemented here.
     */    
    public void close () {        
        try {data.close();} catch (IOException ignored) {}        
    }
    
    /**
     * @return the data
     */
    public InputStream getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(InputStream data) {
        this.data = data;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
}
