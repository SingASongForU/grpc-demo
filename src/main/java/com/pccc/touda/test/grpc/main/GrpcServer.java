package com.pccc.touda.test.grpc.main;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());

    private Server server;

    private int port;

    private String serverName;

    public GrpcServer(String serverName,int port){
        this.serverName=serverName;
        this.port=port;
    }

    public void start() throws IOException {
        /* Grpc server listening ports at 8099 */
        server = ServerBuilder.forPort(port)
                .addService(new HelloServiceImpl(serverName))
                .addService(new UserServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            GrpcServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static final void startServerGracefully(final String serverName,final int port){
        new Thread(()->{
            try{
                final GrpcServer server = new GrpcServer(serverName,port);
                server.start();
                server.blockUntilShutdown();
            }catch(Exception e){
                e.printStackTrace();
            }

        }).start();
    }
    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        startServerGracefully("server_8099",8099);
        startServerGracefully("server_8098",8098);
    }

}
