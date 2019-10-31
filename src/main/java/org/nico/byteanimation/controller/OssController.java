package org.nico.byteanimation.controller;

import org.nico.byteanimation.component.OssComponent;
import org.nico.byteanimation.model.bo.PostPolicyBo;
import org.nico.byteanimation.model.vo.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OssComponent ossComponent;
    
    @GetMapping("/police")
    public RespVo<PostPolicyBo> police() {
        return RespVo.success(ossComponent.createPolicy());
    }
    
}
