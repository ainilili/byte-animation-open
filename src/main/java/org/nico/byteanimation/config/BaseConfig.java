package org.nico.byteanimation.config;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {

    public static int pageMaxLength = 20;

    public static String tempFilePath;
    
    @Value("${app.pageMaxLength}")
    public void setPageMaxLength(int pageMaxLength) {
        BaseConfig.pageMaxLength = pageMaxLength;
    }

    @Value("${app.tempFilePath}")
    public void setTempFilePath(String tempFilePath) {
        String[] paths = tempFilePath.split("\\,");
        
        if(File.separator.equals("/")) {
            BaseConfig.tempFilePath = paths[1];
        }else {
            BaseConfig.tempFilePath = paths[0];
        }
    }

    @PostConstruct
    public void init() {
        File dir = new File(tempFilePath);
        if(! dir.exists()) {
            dir.mkdirs();
        }
    }
    
    
}
