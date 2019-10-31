package org.nico.byteanimation.frame;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.nico.byteanimation.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("imageHandler")
public class ImageFrameHandler extends AbstractFrameHandler{

	@Autowired
    private FrameHandlerTrack track;

    @Override
    public void handler(long id, String url, String suffix) {
        InputStream imageStream = null;
        InputStream coverStream = null;
        ByteArrayOutputStream outStream = null;
        try {
            URL urlObj = new URL(url);
            BufferedImage image = ImageIO.read(urlObj);
            
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();
            
            int characterWidth = (int) (imgWidth / TEXT_IMAGE_WIDTH_RATIO);
            int characterHeight = (int) (imgHeight / TEXT_IMAGE_HEIGHT_RATIO);
            
            String str = RenderUtils.renderChar(image, characterWidth, characterHeight);
            image = RenderUtils.renderImage(str, imgWidth, imgHeight);
            
            outStream = new ByteArrayOutputStream();
            ImageIO.write(image, suffix, outStream);
            
            track.trace(id, 1);
            
            imageStream = new ByteArrayInputStream(outStream.toByteArray());
            coverStream = urlObj.openStream();
        }catch(Throwable ex) {
            ex.printStackTrace();
        }finally {
            track.finished(id, imageStream, coverStream);	
            closeStream(outStream);
            closeStream(imageStream);
            closeStream(coverStream);
        }
    }
    
}
