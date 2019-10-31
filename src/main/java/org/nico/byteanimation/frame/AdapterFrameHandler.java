package org.nico.byteanimation.frame;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("adapterHandler")
public class AdapterFrameHandler extends AbstractFrameHandler{

    @Autowired
    @Qualifier("videoHandler")
    private AbstractFrameHandler videoHandler;
    
    @Autowired
    @Qualifier("gifHandler")
    private AbstractFrameHandler gifHandler;
    
    @Autowired
    @Qualifier("imageHandler")
    private AbstractFrameHandler imageHandler;

    private Set<String> supports = new HashSet<String>();
    
    public AdapterFrameHandler() {
    	supports.add("mp4");
    	supports.add("gif");
    	supports.add("jpg");
    	supports.add("jpeg");
    	supports.add("png");
    }
    
    @Override
    public void handler(long id, String url, String suffix) {
        switch (suffix.toLowerCase()) {
        case "mp4":
            videoHandler.handler(id, url, suffix);
            break;
        case "gif":
            gifHandler.handler(id, url, suffix);
            break;
        case "jpg":
        case "jpeg":
        case "png":
            imageHandler.handler(id, url, suffix);
            break;
        default:
            break;
        }
    }
    
    public boolean isSupport(String suffix) {
    	if(! supports.contains(suffix.toLowerCase())) {
    		return false;
    	}
    	return true;
    }
    
}
