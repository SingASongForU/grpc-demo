package com.pccc.touda.test.grpc.main;

import com.pccc.touda.test.grpc.pb.entity.HelloRequest;
import com.pccc.touda.test.grpc.pb.entity.HelloResponse;
import com.pccc.touda.test.grpc.pb.service.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {
    private final ManagedChannel channel;
    private final HelloServiceGrpc.HelloServiceBlockingStub blockingStub;

    private static final Logger logger = Logger.getLogger(ToudaGrpcClient.class.getName());

    public GrpcClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();

        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sayHello(String requestMsg){
        HelloRequest request = HelloRequest.newBuilder().setRequestMsg(requestMsg).build();
        HelloResponse response;
        try{
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e)
        {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Response code :"+response.getResponseCode()+",Response msg : "+response.getResponseMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        GrpcClient client = new GrpcClient("127.0.0.1",clientLoadBanlance());
        try{
            String reqMsg = "I am grpc client!";
            client.sayHello(reqMsg);
        }finally {
            client.shutdown();
        }
    }

    private static int clientLoadBanlance() {
        int port=8099;
        Random random=new Random();
        if(random.nextBoolean()){
            port=8098;
        }
        return port;
    }
}
