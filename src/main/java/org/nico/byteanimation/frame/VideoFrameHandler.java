package org.nico.byteanimation.frame;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.nico.byteanimation.config.BaseConfig;
import org.nico.byteanimation.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("videoHandler")
public class VideoFrameHandler extends AbstractFrameHandler{
    
    private static Java2DFrameConverter converter = new Java2DFrameConverter();
    
    @Autowired
    private FrameHandlerTrack track;
    
    @Override
    public void handler(long id, String url, String suffix) {
        FFmpegFrameGrabber ffg = new FFmpegFrameGrabber(url); 
        FFmpegFrameRecorder ffr = null;
        FFmpegFrameFilter fff = null;
        
        InputStream videoStream = null;
        InputStream coverStream = null;
        
        ByteArrayOutputStream outStream = null;
        
        File video = null;
        
        boolean coverSaved = false;
        
        try {
            ffg.start();
            
            int videoWidth = ffg.getImageWidth();
            int videoHeight = ffg.getImageHeight();
            
            int characterWidth = (int) (videoWidth / TEXT_IMAGE_WIDTH_RATIO);
            int characterHeight = (int) (videoHeight / TEXT_IMAGE_HEIGHT_RATIO);

            video = fileOption(new File(BaseConfig.tempFilePath + File.separator + id + "." + suffix));

            ffr = new FFmpegFrameRecorder(video, videoWidth, videoHeight, ffg.getAudioChannels());
            buildRecorder(ffg, ffr);
            ffr.start();

            Frame f = null;
            long duration = ffg.getLengthInTime();
            int count = 0;
            long t = 0;
            while ((f = ffg.grab()) != null) {
                if(f != null && f.image != null) {
                    t = f.timestamp;
                    if(count % 60 == 0) {
                        track.trace(id, t / (double) duration);
                    }
                    if (t > ffr.getTimestamp()) {
                        ffr.setTimestamp(t);
                    }
                    
                    if(! coverSaved) {
                        coverSaved = true;
                        outStream = new ByteArrayOutputStream();
                        ImageIO.write(converter.convert(f), COVER_SUFFIX, outStream);
                    }
                    
                    String str = RenderUtils.renderChar(converter.convert(f), characterWidth, characterHeight);
                    BufferedImage image = RenderUtils.renderImage(str, videoWidth, videoHeight);
                    ffr.record(converter.convert(image));

                }
                try {
                    if(f != null) {
                        ffr.recordSamples(f.samples);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
                count ++;
            }
            track.trace(id, t / (double) duration);

            if(video != null && video.exists()) {
                videoStream = new FileInputStream(video);
            }
            if(outStream != null) {
                coverStream = new ByteArrayInputStream(outStream.toByteArray());
            }
        }catch(Throwable e) {
            e.printStackTrace();
        }finally {
            try {
                closeGrabber(ffg);
                closeRecorder(ffr);
                closeFilter(fff);
                track.finished(id, videoStream, coverStream);
                closeStream(videoStream);
                closeStream(coverStream);
                closeStream(outStream);
            }catch(Throwable e) {
                e.printStackTrace();
            }finally {
                fileDelete(video);
            }
        }
    }

    public static void buildRecorder(FFmpegFrameGrabber ffg, FFmpegFrameRecorder ffr) {
        ffr.setSampleRate(ffg.getSampleRate());
        ffr.setFrameRate(ffg.getFrameRate());

        ffr.setVideoBitrate(ffg.getVideoBitrate());
        ffr.setVideoCodec(ffg.getVideoCodec());
        ffr.setVideoCodecName(ffg.getVideoCodecName());
        ffr.setVideoMetadata(ffg.getVideoMetadata());
        ffr.setVideoOptions(ffg.getVideoOptions());

        ffr.setAspectRatio(ffg.getAspectRatio());
        ffr.setAudioBitrate(ffg.getAudioBitrate());
        ffr.setAudioChannels(ffg.getAudioChannels());
        ffr.setAudioQuality(0);

        ffr.setAudioCodec(ffg.getAudioCodec());
        ffr.setAudioCodecName(ffg.getAudioCodecName());
        ffr.setAudioOptions(ffg.getAudioOptions());

        ffr.setMaxDelay(ffg.getMaxDelay());
    }

    public void closeFilter(FrameFilter filter) throws Exception {
        if(filter != null) {
            filter.stop();
            filter.release();
            filter.close();
        }
    }

    public void closeRecorder(FrameRecorder recorder) throws Exception {
        if(recorder != null) {
            recorder.stop();
            recorder.release();
            recorder.close();
        }
    }

    public void closeGrabber(FrameGrabber grabber) throws Exception {
        if(grabber != null) {
            grabber.stop();
            grabber.release();
            grabber.close();
        }
    }

}
