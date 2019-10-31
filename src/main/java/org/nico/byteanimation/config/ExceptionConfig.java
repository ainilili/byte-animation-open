package org.nico.byteanimation.config;

import org.nico.byteanimation.consts.RespCode;
import org.nico.byteanimation.exception.ByteAnimationException;
import org.nico.byteanimation.model.vo.RespVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class ExceptionConfig {

	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public RespVo<?> errorHandler(Exception ex) {
		ex.printStackTrace();
		return RespVo.failure(RespCode.FAILURE);
	}
	
	@ResponseBody
	@ExceptionHandler(value = ByteAnimationException.class)
	public RespVo<?> errorHandler(ByteAnimationException ex) {
		ex.printStackTrace();
		return RespVo.failure(ex.getRespCode());
	}
	
}
