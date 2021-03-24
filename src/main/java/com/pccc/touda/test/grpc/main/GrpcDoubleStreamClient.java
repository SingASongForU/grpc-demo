package com.pccc.touda.test.grpc.main;

import com.google.common.collect.Lists;
import com.pccc.touda.grpc.test.HelloDoubleStreamServiceGrpc;
import com.pccc.touda.grpc.test.HelloServerStreamRequest;
import com.pccc.touda.grpc.test.HelloServerStreamResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrpcDoubleStreamClient {
    private Channel channel;
    private HelloDoubleStreamServiceGrpc.HelloDoubleStreamServiceStub helloDoubleStreamServiceStub;
    public GrpcDoubleStreamClient(String ip,int port){
        this.channel= ManagedChannelBuilder.forAddress(ip,port).usePlaintext().build();
        this.helloDoubleStreamServiceStub=HelloDoubleStreamServiceGrpc.newStub(channel);
    }
    public void getBooksBySizes(List<Integer> sizeList) throws InterruptedException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        StreamObserver<HelloServerStreamRequest> streamObserver=helloDoubleStreamServiceStub
                .withDeadlineAfter(5, TimeUnit.SECONDS)
                .getBooksBySizes(new StreamObserver<HelloServerStreamResponse>() {
                    @Override
                    public void onNext(HelloServerStreamResponse helloServerStreamResponse) {
                        System.err.println("Receive book name:"+helloServerStreamResponse.getBookname());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        countDownLatch.countDown();
                        System.err.println("receive complete.");
                    }
                });
        for(Integer size:sizeList){
            HelloServerStreamRequest request=HelloServerStreamRequest.newBuilder().setSize(size).build();
            streamObserver.onNext(request);
        }
        streamObserver.onCompleted();
        countDownLatch.await(10,TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> sizeList= Lists.newArrayList(2,2,3,4);
        new GrpcDoubleStreamClient("127.0.0.1",8099).getBooksBySizes(sizeList);
    }
}
