package com.pccc.touda.test.grpc.main;

import com.google.common.collect.Lists;
import com.pccc.touda.grpc.test.HelloDoubleStreamServiceGrpc;
import com.pccc.touda.grpc.test.HelloServerStreamRequest;
import com.pccc.touda.grpc.test.HelloServerStreamResponse;
import io.grpc.stub.StreamObserver;

import java.util.List;


public class HelloDoubleStreamImpl extends HelloDoubleStreamServiceGrpc.HelloDoubleStreamServiceImplBase{
    public StreamObserver<HelloServerStreamRequest> getBooksBySizes(
            StreamObserver<HelloServerStreamResponse> responseObserver) {
        return new StreamObserver<HelloServerStreamRequest>(){
            private List<String> bookNameList;

            @Override
            public void onNext(HelloServerStreamRequest helloServerStreamRequest) {
                if(bookNameList==null){
                    bookNameList= Lists.newArrayList();
                }
                int size=helloServerStreamRequest.getSize();
                List<String> tempList=BookFactory.getINSTANCE().generateRandBookNameBySize(size);
                bookNameList.addAll(tempList);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                for(int i=0;i<bookNameList.size();i++){
                    HelloServerStreamResponse response=HelloServerStreamResponse.newBuilder().setBookname(bookNameList.get(i)).build();
                    responseObserver.onNext(response);
                }
                responseObserver.onCompleted();
                System.err.println("Get books by size complete. size:"+bookNameList.size());
            }
        };
    }


}
