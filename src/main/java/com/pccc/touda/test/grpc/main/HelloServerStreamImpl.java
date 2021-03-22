package com.pccc.touda.test.grpc.main;

import com.pccc.touda.grpc.test.HelloServerStreamRequest;
import com.pccc.touda.grpc.test.HelloServerStreamResponse;
import com.pccc.touda.grpc.test.HelloServerStreamServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;

/**
 * 客户端单个请求，服务端返回流式响应
 */
public class HelloServerStreamImpl extends HelloServerStreamServiceGrpc.HelloServerStreamServiceImplBase {
    @Override
    public void getBooksBySize(HelloServerStreamRequest request,
                               StreamObserver<HelloServerStreamResponse> responseObserver) {
        int size=request.getSize();
        List<String> bookNameList=BookFactory.getINSTANCE().generateRandBookNameBySize(size);
        for(int i=0;i<size;i++){
            HelloServerStreamResponse helloStreamResponse=HelloServerStreamResponse.newBuilder().setBookname(bookNameList.get(i)).build();
            responseObserver.onNext(helloStreamResponse);
        }
        responseObserver.onCompleted();
        System.err.println("Get books by size complete. size:"+size);
    }

}
