package org.nico.byteanimation.frame;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.nico.byteanimation.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;

@Component("gifHandler")
public class GifFrameHandler extends AbstractFrameHandler{

	@Autowired
    private FrameHandlerTrack track;

    @Override
    public void handler(long id, String url, String suffix) {
        GifDecoder decoder = new GifDecoder();
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        
        InputStream gifStream = null;
        InputStream coverStream = null;
        ByteArrayOutputStream gifOutStream = null;
        ByteArrayOutputStream coverOutStream = null;
        
        InputStream nativeStream = null;
        
        try {
            URL urlObj = new URL(url);
            nativeStream = urlObj.openStream();
            gifOutStream = new ByteArrayOutputStream();
            coverOutStream = new ByteArrayOutputStream();
            
            if (decoder.read(nativeStream) != 0) {
                return;
            }
            
            int gifWidth = (int) decoder.getFrameSize().getWidth();
            int gifHeight = (int) decoder.getFrameSize().getHeight();
            
            int characterWidth = (int) (gifWidth / TEXT_IMAGE_WIDTH_RATIO);
            int characterHeight = (int) (gifHeight / TEXT_IMAGE_HEIGHT_RATIO);
            
            e.setRepeat(0);
            e.setSize(gifWidth, gifHeight);
            e.start(gifOutStream);
            
            for (int i = 0; i < decoder.getFrameCount(); i++) {
                BufferedImage frame = decoder.getFrame(i);
                
                String str = RenderUtils.renderChar(frame, characterWidth, characterHeight);
                frame = RenderUtils.renderImage(str, gifWidth, gifHeight);
                int delay = decoder.getDelay(i);
                e.setDelay(delay);
                e.addFrame(frame);
                
                if(i % 3 == 0) {
                	track.trace(id, i / (double) decoder.getFrameCount());
                }
            }
            e.finish();
            
            gifStream = new ByteArrayInputStream(gifOutStream.toByteArray());
            coverStream = urlObj.openStream();
        }catch(Throwable ex) {
            ex.printStackTrace();
        }finally {
        	try {
        		closeStream(nativeStream);
                track.finished(id, gifStream, coverStream);	
                closeStream(gifStream);
                closeStream(coverStream);
                closeStream(gifOutStream);
                closeStream(coverOutStream);
        	}finally {
        	}
        }
    }
    
}
