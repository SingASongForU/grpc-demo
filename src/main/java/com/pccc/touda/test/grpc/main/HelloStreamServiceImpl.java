package com.pccc.touda.test.grpc.main;


import com.google.protobuf.ByteString;
import com.pccc.touda.grpc.test.HelloStreamRequest;
import com.pccc.touda.grpc.test.HelloStreamResponse;
import com.pccc.touda.grpc.test.HelloStreamServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 客户端发送流信息，
 * 服务端返回单个响应
 */
public class HelloStreamServiceImpl extends HelloStreamServiceGrpc.HelloStreamServiceImplBase{
    private static final String PATH_FILE_SERVER="src/server-file/";

   @Override
    public StreamObserver<HelloStreamRequest> saveImage(
            StreamObserver<HelloStreamResponse> responseObserver) {
       return new StreamObserver<HelloStreamRequest>() {
           private int totalSize;
           private ByteArrayOutputStream imageDataOutputStream;
           private String fileType;
           @Override
           public void onNext(HelloStreamRequest helloStreamRequest) {
               if(imageDataOutputStream==null){
                   imageDataOutputStream=new ByteArrayOutputStream();
                   totalSize=0;
                   fileType=helloStreamRequest.getFileType();
               }
               ByteString chunk_data=helloStreamRequest.getChunkData();
               System.err.println("Receive chunk_data_size:"+chunk_data.size());
               System.err.println("Receive chunk_data_index:"+helloStreamRequest.getId());
               try {
                   chunk_data.writeTo(imageDataOutputStream);
                   totalSize+=chunk_data.size();
               } catch (IOException e) {
                   responseObserver.onError(Status.INTERNAL.withDescription("cannot write chunk data:"+e.getMessage()).asRuntimeException());
               }
           }

           @Override
           public void onError(Throwable throwable) {
               throwable.printStackTrace();
           }

           @Override
           public void onCompleted() {
               String uuid= UUID.randomUUID().toString();
               String path= PATH_FILE_SERVER+uuid+fileType;


               File file=new File(path);
               OutputStream outputStream=null;
               try {
                   outputStream=new FileOutputStream(file);
                   outputStream.write(imageDataOutputStream.toByteArray());
                   outputStream.flush();
               } catch (FileNotFoundException e) {
                   responseObserver.onError(Status.INTERNAL.withDescription("cannot save image:"+e.getMessage()).asRuntimeException());
               } catch (IOException e) {
                   responseObserver.onError(Status.INTERNAL.withDescription("cannot save image:"+e.getMessage()).asRuntimeException());
               }finally {
                   try {
                       outputStream.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               HelloStreamResponse helloStreamResponse=HelloStreamResponse.newBuilder()
                       .setResponseCode("000000")
                       .setResponseMessage("save image success")
                       .setImageId(uuid)
                       .setImagePath(path)
                       .setTotalSize(totalSize+"")
                       .build();
               responseObserver.onNext(helloStreamResponse);
               responseObserver.onCompleted();
           }
       };
    }


}
