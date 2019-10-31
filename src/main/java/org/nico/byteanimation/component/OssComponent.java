package org.nico.byteanimation.component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.nico.byteanimation.model.bo.PostPolicyBo;
import org.nico.byteanimation.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;

@Component
public class OssComponent {

	@Value("${oss.bucketName}")
	private String bucketName;

	@Value("${oss.endpoint}")
	private String endpoint;

	@Value("${oss.accessKeyId}")
	private String accessKeyId;

	@Value("${oss.accessKeySecret}")
	private String accessKeySecret;

	private final String NATIVE_DIR = "native";

	private final String PROCESSED_DIR = "processed";
	
	private  OSSClient getOSSClient(){
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		return ossClient;
	}

	public void uploadProcessedFile(InputStream stream, String key) throws FileNotFoundException {
        OSSClient ossClient = getOSSClient();
        try {
            ossClient.putObject(bucketName, PROCESSED_DIR + "/" + key , stream);
        }finally {
            ossClient.shutdown();
        }
    }
	
	public String getFileUrl(String dir, String key, long millis) {
        OSSClient ossClient = getOSSClient();
        String url = null;
        try {
            Date expiration = new Date(System.currentTimeMillis() + millis);
            URL urlObj = ossClient.generatePresignedUrl(bucketName, dir + "/" + key,  expiration);
            if(urlObj != null) {
                url = urlObj.toString();
            }
        }finally {
            ossClient.shutdown();
        }
        return url;
    }
	
	public String getProcessedFileUrl(String key, long millis) {
        return getFileUrl(PROCESSED_DIR, key, millis);
	}
	
	public String getProcessedFileUrl(String key) {
        return getProcessedFileUrl(key, 1000 * 60 * 60 * 24);
    }
	
	public String getNativeFileUrl(String key) {
        return getFileUrl(NATIVE_DIR, key, 1000 * 60 * 60 * 5);
    }
	
	public boolean doesNativeExist(String key) {
	    OSSClient ossClient = getOSSClient();
        boolean exist = false;
        try {
            exist = ossClient.doesObjectExist(bucketName, NATIVE_DIR + "/" + key);
        }finally {
            ossClient.shutdown();
        }
        return exist;
	}


	public PostPolicyBo createPolicy() {
		OSSClient ossClient = getOSSClient();
		try {
			PolicyConditions conds = new PolicyConditions();
			conds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 167772160);
			conds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, NATIVE_DIR);

			Date expireTime = DateUtils.randomDate(DateUtils.ONE_DAY);
			String postPolicy = ossClient.generatePostPolicy(expireTime, conds);

			byte[] binaryData = postPolicy.getBytes("utf-8");
			String encodedPolicy = BinaryUtil.toBase64String(binaryData);
			String postSignature = ossClient.calculatePostSignature(postPolicy);

			PostPolicyBo postPolicyBo = new PostPolicyBo();
			postPolicyBo.setAccessId(accessKeyId);
			postPolicyBo.setPolicy(encodedPolicy);
			postPolicyBo.setSignature(postSignature);
			postPolicyBo.setDir(NATIVE_DIR);
			String host = StringUtils.join(endpoint.split("//"), "//" + bucketName + ".");
			postPolicyBo.setHost(host);
			postPolicyBo.setBucket(bucketName);
			postPolicyBo.setRegion(endpoint);
			postPolicyBo.setExpire(String.valueOf(expireTime.getTime() / 1000));
			return postPolicyBo;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			ossClient.shutdown();
		}
	}

}
