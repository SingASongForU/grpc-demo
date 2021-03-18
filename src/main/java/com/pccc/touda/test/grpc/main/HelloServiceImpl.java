package com.pccc.touda.test.grpc.main;

import com.pccc.touda.test.grpc.pb.entity.HelloRequest;
import com.pccc.touda.test.grpc.pb.entity.HelloResponse;
import com.pccc.touda.test.grpc.pb.service.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * Grpc server handle implement
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase{
    private String serverName;
    public HelloServiceImpl(String serverName){
        this.serverName=serverName;
    }
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = HelloResponse.newBuilder().setResponseCode("000000").setResponseMessage(
                "request msg is :"+req.getRequestMsg()
                        +", response:"+"hello world!"
                        +", I am server:"+serverName
        ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
