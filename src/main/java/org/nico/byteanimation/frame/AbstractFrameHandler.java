package org.nico.byteanimation.frame;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public abstract class AbstractFrameHandler {

    protected final static String COVER_SUFFIX = "jpg";
    
    protected final static double TEXT_IMAGE_WIDTH_RATIO = 3.15;
    
    protected final static double TEXT_IMAGE_HEIGHT_RATIO = 2.95;
    
    public abstract void handler(long id, String url, String suffix);
    
    protected File fileOption(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);
        return file;
    }
    
    protected void fileDelete(File file) {
        if(file != null && file.exists()) {
            file.delete();
        }
    }
    
    public void closeStream(Closeable stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
