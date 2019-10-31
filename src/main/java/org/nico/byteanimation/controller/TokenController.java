package org.nico.byteanimation.controller;

import org.apache.commons.lang3.StringUtils;
import org.nico.byteanimation.consts.AppConsts;
import org.nico.byteanimation.consts.RespCode;
import org.nico.byteanimation.model.vo.RespVo;
import org.nico.byteanimation.model.vo.UserLoginInVo;
import org.nico.byteanimation.model.vo.UserLoginOutVo;
import org.nico.byteanimation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @Autowired
    private UserService userService;
    
    @ApiOperation(value = "登录")
    @PostMapping("/")
    public RespVo<UserLoginOutVo> login(
            @RequestBody UserLoginInVo loginVo
            ){
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        if(StringUtils.isBlank(username) || ! username.matches(AppConsts.REGEX_USERNAME)) {
            return RespVo.failure(RespCode.PARAMS_ERROR, "username");
        }
        if(StringUtils.isBlank(password) || password.length() < 6) {
            return RespVo.failure(RespCode.PARAMS_ERROR, "password");
        }
        return userService.login(loginVo);
    }
    
    @ApiOperation(value = "退出")
    @DeleteMapping("/")
    public RespVo<?> exit(){
        return userService.exit();
    }
}
