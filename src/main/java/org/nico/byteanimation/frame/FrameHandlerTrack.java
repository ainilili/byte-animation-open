package org.nico.byteanimation.frame;

import java.io.InputStream;

public interface FrameHandlerTrack {

    public void trace(long id, double p);
    
    public void finished(long id, InputStream processedStream, InputStream coverStream);
}
