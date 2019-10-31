package org.nico.byteanimation.model.vo;

import java.util.Date;

public class AnimationOutVo {

    private Long id;
    
    private String title;
    
    private String coverUrl;
    
    private String nativeUrl;
    
    private String processedUrl;
    
    private String nativeSuffix;
    
    private Long authorId;
    
    private Integer status;
    
    private Date createTime;
    
    private Date updateTime;

    public String getNativeSuffix() {
        return nativeSuffix;
    }

    public void setNativeSuffix(String nativeSuffix) {
        this.nativeSuffix = nativeSuffix;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNativeUrl() {
        return nativeUrl;
    }

    public void setNativeUrl(String nativeUrl) {
        this.nativeUrl = nativeUrl;
    }

    public String getProcessedUrl() {
        return processedUrl;
    }

    public void setProcessedUrl(String processedUrl) {
        this.processedUrl = processedUrl;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
}
