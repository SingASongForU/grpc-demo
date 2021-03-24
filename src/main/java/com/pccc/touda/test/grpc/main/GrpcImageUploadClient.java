package com.pccc.touda.test.grpc.main;

import com.google.protobuf.ByteString;
import com.pccc.touda.grpc.test.HelloStreamRequest;
import com.pccc.touda.grpc.test.HelloStreamResponse;
import com.pccc.touda.grpc.test.HelloStreamServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 客户端发送流信息
 */
public class GrpcImageUploadClient {
    private static final String PATH_FILE_CLIENT="src/client-file/steve-jobs.jpeg";
    private Channel channel;
    private HelloStreamServiceGrpc.HelloStreamServiceStub streamServiceStub;
    public GrpcImageUploadClient(String ip,int port){
        this.channel=ManagedChannelBuilder.forAddress(ip,port)
                .usePlaintext()
                .build();
        this.streamServiceStub=HelloStreamServiceGrpc.newStub(channel);
    }
    public void saveImage() throws InterruptedException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        StreamObserver<HelloStreamRequest> streamRequestStreamObserver=
                streamServiceStub
                        .withDeadlineAfter(5, TimeUnit.SECONDS)
                        .saveImage(new StreamObserver<HelloStreamResponse>() {
                            @Override
                            public void onNext(HelloStreamResponse helloStreamResponse) {
                                System.err.println("ID:"+helloStreamResponse.getImageId());
                                System.err.println("PATH:"+helloStreamResponse.getImagePath());
                                System.err.println("SIZE:"+helloStreamResponse.getTotalSize());
                                System.err.println("code:"+helloStreamResponse.getResponseCode());
                                System.err.println("msg:"+helloStreamResponse.getResponseMessage());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                System.err.println("upload image excpetion:"+throwable.getMessage());
                                countDownLatch.countDown();
                            }

                            @Override
                            public void onCompleted() {
                                countDownLatch.countDown();

                            }
                        });
        readAndAsyncSend(getFileType(PATH_FILE_CLIENT),streamRequestStreamObserver, getFileInputStream(PATH_FILE_CLIENT));
        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    private String getFileType(String pathFileClient) {
        return PATH_FILE_CLIENT.substring(PATH_FILE_CLIENT.lastIndexOf("."));
    }

    private void readAndAsyncSend(String fileType,StreamObserver<HelloStreamRequest> streamRequestStreamObserver, FileInputStream inputStream) {
        byte[] buffer=new byte[1024];
        int i=0;
        while(true){
            int readLength= 0;
            try {
                readLength = inputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(readLength<=0){
                break;
            }
            HelloStreamRequest request=HelloStreamRequest.newBuilder()
                    .setId(i+"")
                    .setFileType(fileType)
                    .setChunkData(ByteString.copyFrom(buffer,0,readLength))
                    .build();
            i++;
            streamRequestStreamObserver.onNext(request);
            System.err.println("send chunck date success,size:"+readLength);
        }
        streamRequestStreamObserver.onCompleted();

    }

    private FileInputStream getFileInputStream(String filePath) {
        FileInputStream inputStream=null;
        try {
            inputStream = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static void main(String[] args) throws Exception {
        new GrpcImageUploadClient("127.0.0.1",8099).saveImage();
    }
}
