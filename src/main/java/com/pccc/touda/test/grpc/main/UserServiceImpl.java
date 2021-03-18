package com.pccc.touda.test.grpc.main;

import com.pccc.touda.test.grpc.pb.entity.Friend;
import com.pccc.touda.test.grpc.pb.entity.UserRequest;
import com.pccc.touda.test.grpc.pb.entity.UserResponse;
import com.pccc.touda.test.grpc.pb.service.UserServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public void createUser(UserRequest request,
                           io.grpc.stub.StreamObserver<UserResponse> responseObserver) {
        logger.info("Receive create user request : id : {}, name : {}, phone : {}, sex : {}"
                ,request.getId(),request.getName(),request.getPhone(),request.getSex());
        for(Friend friend:request.getFriendsList()){
            logger.info("Friend name is : {}",friend.getName());
        }
        UserResponse userResponse=UserResponse.newBuilder().setId(request.getId())
                .setCode("000000").setMsg("Create user success !").build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
