package com.example.upload;

import java.io.IOException;
import java.io.InputStream;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class S3Util {
	
	private static final String BUCKET = "<<bucket name>>";

	public static void uploadFile(String fileName, InputStream inputStream)
			throws S3Exception, AwsServiceException, SdkClientException, IOException {
		S3Client client = S3Client.builder()
							.region(Region.US_EAST_1)
							.credentialsProvider(SystemPropertyCredentialsProvider.create())
							.build();

		PutObjectRequest request = PutObjectRequest.builder()
									.bucket(BUCKET)
									.key(fileName)
									//.contentType("image/png")
									.build();

		client.putObject(request, RequestBody.fromInputStream(inputStream, inputStream.available()));
		
		S3Waiter waiter = client.waiter();
		HeadObjectRequest waitRequest = HeadObjectRequest.builder()
		                    .bucket(BUCKET)
		                    .key(fileName)
		                    .build();
		 
		WaiterResponse<HeadObjectResponse> waitResponse = waiter.waitUntilObjectExists(waitRequest);
		 
		waitResponse.matched().response().ifPresent(x -> {
		    System.out.println("File uploaded successfully");
		});
	}
}
