package com.pccc.touda.test.grpc.main;

import com.google.common.collect.Lists;
import com.pccc.touda.test.grpc.pb.entity.*;
import com.pccc.touda.test.grpc.pb.service.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcUserClient {
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

    private static final Logger logger = Logger.getLogger(GrpcUserClient.class.getName());

    public GrpcUserClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();

        blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createUser(String id, String name, String phone, String sex, List<Friend> friendList){

        UserRequest.Builder requestBuilder = UserRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setPhone(phone)
                .setSex(sex);
        for(Friend friend:friendList){
            requestBuilder.addFriends(friend);
        }
        UserRequest request=requestBuilder.build();
        UserResponse response;
        try{
            response = blockingStub.createUser(request);
        } catch (StatusRuntimeException e)
        {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("User ID :"+response.getId()+",Response msg : "+response.getMsg());
    }

    public static void main(String[] args) throws InterruptedException {
        GrpcUserClient client = new GrpcUserClient("127.0.0.1",clientLoadBanlance());
        try{
            client.createUser("0001","Sherlocked","221","male"
                    , Lists.newArrayList(
                            Friend.newBuilder().setName("Tom").build(),
                            Friend.newBuilder().setName("Jerry").build(),
                            Friend.newBuilder().setName("Watshon").build()
                    )
            );
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
