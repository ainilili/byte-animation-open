package org.nico.byteanimation.frame;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.nico.byteanimation.component.CacheComponent;
import org.nico.byteanimation.component.OssComponent;
import org.nico.byteanimation.consts.CacheKeys;
import org.nico.byteanimation.consts.DelFlag;
import org.nico.byteanimation.consts.FrameHandleStatus;
import org.nico.byteanimation.mapper.AnimationMapper;
import org.nico.byteanimation.model.po.Animation;
import org.nico.byteanimation.utils.GenerateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultFrameHandlerTrack implements FrameHandlerTrack{

    @Autowired
    private CacheComponent cacheComponent;
    
    @Autowired
    private OssComponent ossComponent;
    
    @Autowired
    private AnimationMapper animationMapper;
    
    @Override
    public void trace(long id, double p) {
        cacheComponent.setEx(CacheKeys.FRAME_HANDLE_PROGRESS + id, String.valueOf(p), 2, TimeUnit.MINUTES);
    }

    @Override
    public void finished(long id, InputStream processedStream, InputStream coverStream) {
        Animation animation = animationMapper.selectById(id);
        if(animation == null) return;
        
        FrameHandleStatus status = processedStream != null ? FrameHandleStatus.SUCCESS : FrameHandleStatus.FAILURE;
        boolean successed = status == FrameHandleStatus.SUCCESS;
        
        String videoKey = GenerateUtils.createOssKey(id, "." + animation.getNativeSuffix());
        String coverKey = GenerateUtils.createOssKey(id, ".jpg");
        
        if(successed) {
            try {
                ossComponent.uploadProcessedFile(processedStream, videoKey);
                if(coverStream != null) {
                    ossComponent.uploadProcessedFile(coverStream, coverKey);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        cacheComponent.delete(CacheKeys.FRAME_HANDLE_PROGRESS + id);
        cacheComponent.setEx(CacheKeys.FRAME_HANDLE_STATUS + id, status.toString(), 10, TimeUnit.MINUTES);
        
        Animation updater = new Animation();
        updater.setId(id);
        updater.setProcessedKey(videoKey);
        if(coverStream != null) {
            updater.setCoverKey(coverKey);
        }
        updater.setStatus(status.getCode());
        if(! successed) {
            updater.setDeleted(DelFlag.DELED.getCode());    
        }
        animationMapper.updateSelective(updater);
        
    }

}
