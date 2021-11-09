package csd.roster.repo.util;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

@Component
public class AwsS3Util{
    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1).build();

    public URL upload(final String bucket,
                      final String fileName,
                      final File file){
        try {
            s3.putObject(bucket, fileName, file);
            return s3.getUrl(bucket, fileName);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

}
