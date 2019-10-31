package org.nico.byteanimation.model.vo;

import org.nico.byteanimation.consts.FrameHandleStatus;

public class FrameHandlerOutVo {

    private long id;
    
    private double progress;
    
    private FrameHandleStatus status;
    
    public FrameHandlerOutVo(long id, double progress, FrameHandleStatus status) {
        this.id = id;
        this.progress = progress;
        this.status = status;
    }

    public FrameHandlerOutVo() {
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
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
