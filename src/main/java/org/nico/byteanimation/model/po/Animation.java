package org.nico.byteanimation.model.po;

import java.util.Date;

import org.nico.ourbatis.annotation.RenderName;
import org.nico.ourbatis.annotation.RenderPrimary;

@RenderName("animation")
public class Animation {

    @RenderPrimary
    private Long id;
    
    private String title;
    
    private String describe;
    
    private String coverKey;
    
    private String nativeKey;
    
    private String nativeSuffix;
    
    private String processedKey;
    
    private Long authorId;
    
    private Integer status;
    
    private Integer deleted;
    
    private Date createTime;
    
    private Date updateTime;
    
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getCoverKey() {
        return coverKey;
    }

    public void setCoverKey(String coverKey) {
        this.coverKey = coverKey;
    }

    public String getNativeKey() {
        return nativeKey;
    }

    public void setNativeKey(String nativeKey) {
        this.nativeKey = nativeKey;
    }

    public String getProcessedKey() {
        return processedKey;
    }

    public void setProcessedKey(String processedKey) {
        this.processedKey = processedKey;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNativeSuffix() {
        return nativeSuffix;
    }

    public void setNativeSuffix(String nativeSuffix) {
        this.nativeSuffix = nativeSuffix;
    }

}
