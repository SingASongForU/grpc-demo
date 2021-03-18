package com.pccc.touda.test.grpc.main;

import com.pccc.touda.test.grpc.pb.entity.Friend;
import com.pccc.touda.test.grpc.pb.entity.UserRequest;
import com.pccc.touda.test.grpc.pb.entity.UserResponse;
import com.pccc.touda.test.grpc.pb.service.UserServiceGrpc;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase{
    @Override
    public void createUser(UserRequest request,
                           io.grpc.stub.StreamObserver<UserResponse> responseObserver) {
        System.err.println("Recieve create user request:"+
                "id:"+request.getId()+ ",name:"+request.getName()+
                        ",phone:"+request.getPhone()+ ",sex:"+request.getSex());
        for(Friend friend:request.getFriendsList()){
            System.err.println("Friend name is:"+friend.getName());
        }
        UserResponse userResponse=UserResponse.newBuilder().setId(request.getId())
                .setCode("000000").setMsg("Create user success !").build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
