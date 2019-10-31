package org.nico.byteanimation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.nico.byteanimation.component.CacheComponent;
import org.nico.byteanimation.component.OssComponent;
import org.nico.byteanimation.consts.CacheKeys;
import org.nico.byteanimation.consts.DelFlag;
import org.nico.byteanimation.consts.FrameHandleStatus;
import org.nico.byteanimation.consts.RespCode;
import org.nico.byteanimation.frame.AbstractFrameHandler;
import org.nico.byteanimation.frame.AdapterFrameHandler;
import org.nico.byteanimation.mapper.AnimationMapper;
import org.nico.byteanimation.model.po.Animation;
import org.nico.byteanimation.model.vo.AnimationInVo;
import org.nico.byteanimation.model.vo.AnimationOutVo;
import org.nico.byteanimation.model.vo.ListVo;
import org.nico.byteanimation.model.vo.RespVo;
import org.nico.byteanimation.utils.FileUtils;
import org.nico.byteanimation.utils.HttpContextUtils;
import org.nico.byteanimation.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class AnimationService {

    @Autowired
    private CacheComponent cacheComponent;
    
    @Autowired
    private OssComponent ossComponent;
    
    @Autowired
    private AnimationMapper animationMapper;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    @Qualifier("adapterHandler")
    private AdapterFrameHandler adapterHandler;
    
    private final static ThreadPoolExecutor TPE = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    
    public RespVo<ListVo<AnimationOutVo>> listOfPage(Animation query, int page, int size){
        query.setStatus(FrameHandleStatus.SUCCESS.getCode());
        Page<Animation> pr = PageHelper.startPage(page, size, "create_time desc");
        animationMapper.selectList(query);
        
        List<AnimationOutVo> animationOutVoList = new ArrayList<AnimationOutVo>();
        if(! CollectionUtils.isEmpty(pr.getResult())) {
        	pr.getResult().forEach(an -> {
        		AnimationOutVo animationOutVo = ModelUtils.convert(an, AnimationOutVo.class);
        		animationOutVo.setNativeUrl(cacheService.getNativeUrl(an.getNativeKey()));
        		if(StringUtils.isNotBlank(an.getCoverKey())) {
        			animationOutVo.setCoverUrl(cacheService.getProcessedUrl(an.getCoverKey()));
        		}
        		if(StringUtils.isNotBlank(an.getProcessedKey())) {
        			animationOutVo.setProcessedUrl(cacheService.getProcessedUrl(an.getProcessedKey()));
        		}
        		if(StringUtils.isNotBlank(an.getNativeKey())) {
        			animationOutVo.setNativeUrl(cacheService.getNativeUrl(an.getNativeKey()));
        		}
        		animationOutVoList.add(animationOutVo);
        	});
        }
        ListVo<AnimationOutVo> listVo = ListVo.list(animationOutVoList, pr.getTotal());
        return RespVo.success(listVo);
    }
    
    public RespVo<AnimationOutVo> get(Long id){
        Animation an = animationMapper.selectById(id);
        if(an == null) {
            return RespVo.failure(RespCode.ANIMATION_NOT_FOUND);
        }
        
        AnimationOutVo animationOutVo = ModelUtils.convert(an, AnimationOutVo.class);
		if(StringUtils.isNotBlank(an.getCoverKey())) {
			animationOutVo.setCoverUrl(cacheService.getProcessedUrl(an.getCoverKey()));
		}
		if(StringUtils.isNotBlank(an.getProcessedKey())) {
			animationOutVo.setProcessedUrl(cacheService.getProcessedUrl(an.getProcessedKey()));
		}
		if(StringUtils.isNotBlank(an.getNativeKey())) {
			animationOutVo.setNativeUrl(cacheService.getNativeUrl(an.getNativeKey()));
		}
		animationOutVo.setProcessedUrl(cacheService.getProcessedUrl(an.getProcessedKey()));
        return RespVo.success(animationOutVo);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public RespVo<AnimationOutVo> publish(AnimationInVo animationVo){
        String nativeUrl = ossComponent.getNativeFileUrl(animationVo.getNativeKey());
        String suffix = FileUtils.parseUrlAndFileSuffix(nativeUrl);
        
        if(! adapterHandler.isSupport(suffix)) {
        	return RespVo.failure(RespCode.FRAME_HANDLER_NOT_FOUND);
        }
        
        Animation animation = new Animation();
        animation.setTitle(animationVo.getTitle());
        animation.setDescribe(animationVo.getDescribe());
        animation.setNativeKey(animationVo.getNativeKey());
        animation.setNativeSuffix(suffix);
        animation.setCoverKey(animationVo.getCoverKey());
        animation.setStatus(FrameHandleStatus.PROCESSING.getCode());
        animation.setAuthorId(HttpContextUtils.getUserId());
        animation.setDeleted(DelFlag.NOTDEL.getCode());
        
        if(animationMapper.insertSelective(animation) > 0) {
            cacheComponent.setEx(CacheKeys.FRAME_HANDLE_STATUS + animation.getId(), FrameHandleStatus.PROCESSING.toString(), 10, TimeUnit.MINUTES);
            
            TPE.execute(new FutureTask<Void>(()->{
                adapterHandler.handler(animation.getId(), nativeUrl, suffix);
                return null;
            }));
            
            AnimationOutVo animationOutVo = ModelUtils.convert(animation, AnimationOutVo.class);
            animationOutVo.setStatus(FrameHandleStatus.PROCESSING.getCode());
            return RespVo.success(animationOutVo);    
        }else {
            return RespVo.failure(RespCode.INSERT_FAILURE);
        }
        
    }
}
