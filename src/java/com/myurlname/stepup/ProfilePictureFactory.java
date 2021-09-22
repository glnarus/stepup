
package com.myurlname.stepup;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;

/**
 * ProfilePictureFactory is a helper class that enforces uploading a maximum total size and maximum
 * dimensions of a square image used for profile pictures.  This Factory provides a downsizing method
 * to allow larger images to be coerced to maximums stated previously.  If the image is within size
 * limits, the method returns the image unaltered.  
 * 
 * Future work: Add method for providing a tiny version of a profile picture for use in chat/discussion streams.
 * 
 * @author Gabriel
 */
public class ProfilePictureFactory {
    
    /**
     * !Warning -method assumes filepart input is an image.  
     * Takes in a filepart (from a HTTP post request) and tests against total file size
     * and pixel dimension limit arguments.  If under limits, returns a ProfilePicDataAndType object with
     * unaltered filepart InputStream and contentType strings.  
     * 
     * If over filesize or dimension limits, resamples the image using Affine transform to adhere
     * to limits and returns the ProfilePicDataAndType coerced image in a PNG file format
     *
     * @param filePart This is the Part object as provided by Container from POST method
     * @param maxSize maximum file size (in bytes)
     * @param maxPixelsOnEdge maximum number of pixels on an edge (square) allowed for the image
     * @return ProfilePicDataAndType contains the input stream data and the content type
     * @throws IOException if an I/O error occurs
     */    
    public static ProfilePicDataAndType getEnforcedSizePicture (Part filePart, int maxSize, int maxPixelsOnEdge) 
                throws IOException {      
        long sizeInBytes = filePart.getSize();    
        BufferedImage imBuff = ImageIO.read(filePart.getInputStream());  
        ProfilePicDataAndType pic = new ProfilePicDataAndType();

        if (sizeInBytes > maxSize || imBuff.getHeight() > maxPixelsOnEdge ||
                                     imBuff.getWidth() > maxPixelsOnEdge ) {
            int newWidth = 0;
            int newHeight = 0;
            double scaleY, scaleX;
            if (Math.max(imBuff.getHeight(), imBuff.getWidth()) == imBuff.getHeight()) {
                newHeight = 250;
                scaleY = 250.0/(double)imBuff.getHeight();
                scaleX = scaleY;
                newWidth = (int)((double)imBuff.getWidth()*scaleX);
            }
            else {
                newWidth = 250;
                scaleX = 250.0/(double)imBuff.getWidth();
                scaleY = scaleX;
                newHeight = (int)((double)imBuff.getHeight()*scaleY);
            }
            AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
            AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR); //if CPU perf is not a concern, consider moving to TYPE_BICUBIC
            BufferedImage scaledImage = bilinearScaleOp.filter(imBuff, new BufferedImage(newWidth, newHeight, imBuff.getType()));   
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());            
            pic.setData(is);
            pic.setContentType("image/png");
        }
        else {
           pic.setData(filePart.getInputStream());
           pic.setContentType(filePart.getContentType());
        }     
       return pic;     
    }
}
