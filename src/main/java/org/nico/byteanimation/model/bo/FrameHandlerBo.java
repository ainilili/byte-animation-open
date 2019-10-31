package org.nico.byteanimation.model.bo;

import java.io.File;
import java.io.InputStream;

import org.nico.byteanimation.consts.FrameHandleStatus;

public class FrameHandlerBo {

    private long id;

    private FrameHandleStatus status;
    
    private String suffix;

    private InputStream videoStream;

    private InputStream coverStream;

    private File videoFile;

    private File coverFile;

    public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
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

    public FrameHandleStatus getStatus() {
        return status;
    }

    public void setStatus(FrameHandleStatus status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
