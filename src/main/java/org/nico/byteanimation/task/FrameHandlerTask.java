package org.nico.byteanimation.task;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.nico.byteanimation.model.bo.FrameHandlerBo;

public class FrameHandlerTask extends FutureTask<FrameHandlerBo>{

    public FrameHandlerTask(Callable<FrameHandlerBo> callable) {
        super(callable);
    }

}
