package org.nico.byteanimation.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.nico.byteanimation.component.CacheComponent;
import org.nico.byteanimation.consts.CacheKeys;
import org.nico.byteanimation.consts.MailType;
import org.nico.byteanimation.consts.RespCode;
import org.nico.byteanimation.mapper.UserMapper;
import org.nico.byteanimation.model.bo.UserTokenBo;
import org.nico.byteanimation.model.po.User;
import org.nico.byteanimation.model.vo.RespVo;
import org.nico.byteanimation.model.vo.UserLoginInVo;
import org.nico.byteanimation.model.vo.UserLoginOutVo;
import org.nico.byteanimation.model.vo.UserRegisterInVo;
import org.nico.byteanimation.utils.EncryptUtils;
import org.nico.byteanimation.utils.GenerateUtils;
import org.nico.byteanimation.utils.JsonUtils;
import org.nico.byteanimation.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CacheComponent cacheComponent;
    
    public RespVo<?> register(UserRegisterInVo registerVo){
        String realCode;
        String mailCodeKey = CacheKeys.MAIL_CODE + MailType.REGISTER.getCode() + registerVo.getUsername();
        String mailCountKey = CacheKeys.MAIL_AUTH_COUNT + MailType.REGISTER.getCode() + registerVo.getUsername();
        if(StringUtils.isBlank(realCode = cacheComponent.get(mailCodeKey))) {
            return RespVo.failure(RespCode.REGISTER_CODE_INVALID);
        }
        
        if(! realCode.equalsIgnoreCase(registerVo.getCode())) {
            long count = cacheComponent.incrBy(mailCountKey, 1);
            if(count >= 6) {
                cacheComponent.delete(mailCodeKey);
                cacheComponent.delete(mailCountKey);
                return RespVo.failure(RespCode.MAIL_CODE_VALIDATION_LIMIT);
            }
            return RespVo.failure(RespCode.REGISTER_CODE_ERROR, 6 - count);
        }
        
        if(userMapper.selectEntity(new User().setUsername(registerVo.getUsername())) != null) {
            return RespVo.failure(RespCode.USERNAME_ALREADY_EXIST);
        }
        
        User user = new User();
        user.setUsername(registerVo.getUsername());
        user.setPassword(EncryptUtils.encode(registerVo.getPassword()));
        user.setNickname(registerVo.getUsername());
        if(userMapper.insertSelective(user) > 0) {
            return RespVo.success();
        }else {
            return RespVo.failure(RespCode.UPDATE_FAILURE);
        }
    }
    
    public RespVo<UserLoginOutVo> login(UserLoginInVo loginVo){
        User user = userMapper.selectEntity(new User().setUsername(loginVo.getUsername()).setPassword(EncryptUtils.encode(loginVo.getPassword())));
        if(user != null) {
            String token = GenerateUtils.createToken();
            
            UserLoginOutVo loginOutVo = ModelUtils.convert(user, UserLoginOutVo.class);
            loginOutVo.setToken(token);
            
            UserTokenBo userTokenBo = ModelUtils.convert(user, UserTokenBo.class);
            userTokenBo.setToken(token);
            
            cacheComponent.setEx(CacheKeys.USER_TOKEN + token, JsonUtils.toString(userTokenBo), 24, TimeUnit.HOURS);
            
            return RespVo.success(loginOutVo);
        }else {
            return RespVo.failure(RespCode.USERNAME_OR_PASSWORD_ERROR);
        }
    }
    
    public RespVo<?> exit(){
        return RespVo.success();
    }
    
    
}
