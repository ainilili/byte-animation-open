package org.nico.byteanimation.model.bo;

import java.io.File;
import java.io.InputStream;

public class FrameBo {

    private InputStream videoStream;

    private InputStream coverStream;
    
    private File videoFile;
    
    private File coverFile;

    public FrameBo(InputStream videoStream, InputStream coverStream, File videoFile, File coverFile) {
        this.videoStream = videoStream;
        this.coverStream = coverStream;
        this.videoFile = videoFile;
        this.coverFile = coverFile;
    }

    public File getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(File coverFile) {
        this.coverFile = coverFile;
    }

    public InputStream getVideoStream() {
        return videoStream;
    }

    public void setVideoStream(InputStream videoStream) {
        this.videoStream = videoStream;
    }

    public InputStream getCoverStream() {
        return coverStream;
    }

    public void setCoverStream(InputStream coverStream) {
        this.coverStream = coverStream;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }


}
