package com.pccc.touda.test.grpc.main;

import com.pccc.touda.test.grpc.pb.entity.HelloRequest;
import com.pccc.touda.test.grpc.pb.entity.HelloResponse;
import com.pccc.touda.test.grpc.pb.service.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Grpc server handle implement
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    private String serverName;
    public HelloServiceImpl(String serverName){
        this.serverName=serverName;
    }
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = HelloResponse.newBuilder()
                .setResponseCode("000000")
                .setResponseMessage("hello world!")
                .build();
        logger.info("I am server : {}, request msg is : {}",serverName,req.getRequestMsg());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
