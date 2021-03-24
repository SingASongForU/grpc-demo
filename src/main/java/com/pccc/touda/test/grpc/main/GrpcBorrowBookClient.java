package com.pccc.touda.test.grpc.main;

import com.pccc.touda.grpc.test.HelloServerStreamRequest;
import com.pccc.touda.grpc.test.HelloServerStreamResponse;
import com.pccc.touda.grpc.test.HelloServerStreamServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

/**
 * 客户端发送单个请求
 * 服务端返回书名流
 */
public class GrpcBorrowBookClient {
    private Channel channel;
    private HelloServerStreamServiceGrpc.HelloServerStreamServiceBlockingStub helloServerStreamServiceStub;

    public GrpcBorrowBookClient(String ip, int port) {
        this.channel = ManagedChannelBuilder.forAddress(ip, port)
                .usePlaintext()
                .build();
        this.helloServerStreamServiceStub = HelloServerStreamServiceGrpc.newBlockingStub(channel);
    }

    public void getBooksBySize(int size) {
        HelloServerStreamRequest request = HelloServerStreamRequest.newBuilder().setSize(size).build();
        Iterator<HelloServerStreamResponse> helloServerStreamResponseIterator = helloServerStreamServiceStub
                .getBooksBySize(request);
        while (helloServerStreamResponseIterator.hasNext()) {
            HelloServerStreamResponse response = helloServerStreamResponseIterator.next();
            System.err.println(response.getBookname());
        }
    }

    public static void main(String[] args) {
        new GrpcBorrowBookClient("127.0.0.1", 8099).getBooksBySize(10);
    }
}
