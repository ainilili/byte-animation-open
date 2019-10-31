package org.nico.byteanimation.controller;

import org.apache.commons.lang3.StringUtils;
import org.nico.byteanimation.component.CacheComponent;
import org.nico.byteanimation.component.OssComponent;
import org.nico.byteanimation.config.BaseConfig;
import org.nico.byteanimation.consts.CacheKeys;
import org.nico.byteanimation.consts.FrameHandleStatus;
import org.nico.byteanimation.consts.RespCode;
import org.nico.byteanimation.model.po.Animation;
import org.nico.byteanimation.model.vo.AnimationInVo;
import org.nico.byteanimation.model.vo.AnimationOutVo;
import org.nico.byteanimation.model.vo.FrameHandlerOutVo;
import org.nico.byteanimation.model.vo.ListVo;
import org.nico.byteanimation.model.vo.RespVo;
import org.nico.byteanimation.service.AnimationService;
import org.nico.byteanimation.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/animation")
public class AnimationController {

    @Autowired
    private CacheComponent cacheComponent;
    
    @Autowired
    private OssComponent ossComponent;

    @Autowired
    private AnimationService animationService;

    @PostMapping("/")
    public RespVo<?> publish(@RequestBody AnimationInVo animationVo) {
        if(StringUtils.isBlank(animationVo.getTitle()) || animationVo.getTitle().length() > 200) {
            return RespVo.failure(RespCode.PARAMS_ERROR, "title");
        }
        if(StringUtils.isNotBlank(animationVo.getDescribe()) && animationVo.getDescribe().length() > 1000) {
            return RespVo.failure(RespCode.PARAMS_ERROR, "describe");
        }
        if(! ossComponent.doesNativeExist(animationVo.getNativeKey())) {
            return RespVo.failure(RespCode.OSS_FILE_NOT_EXIST, animationVo.getNativeKey());
        }
        if(StringUtils.isNotBlank(animationVo.getCoverKey()) && ! ossComponent.doesNativeExist(animationVo.getCoverKey())) {
            return RespVo.failure(RespCode.OSS_FILE_NOT_EXIST, animationVo.getCoverKey());
        }
        return animationService.publish(animationVo);
    }

    @GetMapping("/progress/{id}")
    public RespVo<FrameHandlerOutVo> progress(@PathVariable long id) {
        String progress = cacheComponent.get(CacheKeys.FRAME_HANDLE_PROGRESS + id);
        String status = cacheComponent.get(CacheKeys.FRAME_HANDLE_STATUS + id);
        
        if(StringUtils.isBlank(progress) || StringUtils.isBlank(status)) {
            return RespVo.failure(RespCode.FRAME_HANDLER_FINISHED);
        }
        return RespVo.success(new FrameHandlerOutVo(id, Double.valueOf(progress), FrameHandleStatus.valueOf(status)));
    }
    
    @GetMapping("/{id}")
    public RespVo<AnimationOutVo> get(@PathVariable long id) {
        return animationService.get(id);
    }

    @GetMapping("/")
    public RespVo<ListVo<AnimationOutVo>> list(
            @RequestParam(required = false) Long authorId,
            @RequestParam int page, 
            @RequestParam int size){
        if(size > BaseConfig.pageMaxLength) {
            return RespVo.failure(RespCode.PARAMS_OVERFLOW_LIMIT, "size", "0", BaseConfig.pageMaxLength);
        }
        Animation query = new Animation();
        query.setAuthorId(authorId);
        
        return animationService.listOfPage(ModelUtils.convert(query, Animation.class), page, size);
    }
    
}
