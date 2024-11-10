package com.cs203.smucode.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: gav
 * @version: 1.0
 * @since: 24-09-06
 * @description: Utility class for AWS services
 */
@Component
public class AWSUtil {

    private static final Logger logger = LoggerFactory.getLogger(AWSUtil.class);

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final Map<UUID, String> tournamentIdToKeyMap = new HashMap<>();


    public String generatePresignedUrl(UUID tournamentId, String contentType) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            String key = String.format("tournament-pictures/%s-%s", tournamentId, UUID.randomUUID());
            this.tournamentIdToKeyMap.put(tournamentId, key);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(3))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (SdkException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Error with AWS");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Error generating presigned URL");
        }
    }

    public String getObjectUrl(UUID tournamentId) {
        if (this.getKey(tournamentId) == null) {
            throw new IllegalStateException("Tournament ID " + tournamentId + " does not have an existing upload link");
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, Region.AP_SOUTHEAST_1.toString(), this.getKey(tournamentId));
    }

    public String getKey(UUID tournamentId) {
        return tournamentIdToKeyMap.get(tournamentId);
    }
}
