package com.serverless;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

//public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
public class Handler implements RequestHandler<Map<String, Object>, String> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
   //public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
	public String handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		System.out.println("New Version!!");
		String bucketName = "dcarrascal75";
		
try {
        
    System.out.println("Starting s3client....!");
    AmazonS3 s3Client =AmazonS3ClientBuilder.defaultClient();
    
//	
//    System.out.println("Starting cres....!");
//    AWSCredentials credentials = new BasicAWSCredentials(
//              "AKI...", 
//              "ltVqsYDKvD3...."
//            );
    
//        AmazonS3 s3Client = AmazonS3ClientBuilder
//                  .standard()
//                  .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                  .withRegion(Regions.EU_WEST_1)
//                  .build();
		
        List<Bucket> buckets = s3Client.listBuckets();
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        } 
		
		
        
        System.out.println("Listing objects");
        
        // maxKeys is set to 2 to demonstrate the use of
        // ListObjectsV2Result.getNextContinuationToken()
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(10);
        ListObjectsV2Result result;

        System.out.println("Step1.............");
        do {
            result = s3Client.listObjectsV2(req);
            System.out.println("Step2");
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                 	System.out.printf(" - %s (size: %d)  (Date: %s) \n", objectSummary.getKey(), objectSummary.getSize(),  new SimpleDateFormat("yyyy-MM-dd").format(objectSummary.getLastModified()) );
                 	System.out.println("ver si borro..........");
                 	if(objectSummary.getKey().equals("MyFunction.zip")) {
                 	// Delete the  objects
                        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName)
                                                                                .withKeys(objectSummary.getKey())
                                                                                .withQuiet(false);
                        System.out.println("borro..........");
                        
                     // Verify that the objects were deleted successfully.
                        DeleteObjectsResult delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
                        int successfulDeletes = delObjRes.getDeletedObjects().size();
                        System.out.println(successfulDeletes + " objects successfully deleted.");
                 	}
                 	
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            String token = result.getNextContinuationToken();
            System.out.println("Next Continuation Token: " + token);
            req.setContinuationToken(token);
        } while (result.isTruncated());
    }
    catch(AmazonServiceException e) {
        // The call was transmitted successfully, but Amazon S3 couldn't process 
        // it, so it returned an error response.
        e.printStackTrace();
    }
    catch(SdkClientException e) {
        // Amazon S3 couldn't be contacted for a response, or the client
        // couldn't parse the response from Amazon S3.
        e.printStackTrace();
    }
		

//		Response responseBody = new Response("Go Serverless v1.x! Your function executed successfully!", input);
//		return ApiGatewayResponse.builder()
//				.setStatusCode(200)
//				.setObjectBody(responseBody)
//				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
//				.build();
	    return "OK";
	}
}
