package com.pccc.touda.test.grpc.main;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

public class BookFactory {
    private static BookFactory INSTANCE=new BookFactory();

    public static BookFactory getINSTANCE() {
        return INSTANCE;
    }

    public List<String> generateRandBookNameBySize(int size){
        List<String> bookNameList= Lists.newArrayList();
        for(int i=0;i<size;i++){
            String bookName=generateRandBookName();
            bookNameList.add(bookName);
        }
        return bookNameList;
    }

    public String generateRandBookName(){
        int bookNameLength=new Random().nextInt(20)+1;
        StringBuffer buffer=new StringBuffer(bookNameLength);
        for(int i=0;i<bookNameLength;i++){
            char temp=(char)(int)(Math.random()*26+97);
            buffer.append(temp);
        }
        return buffer.toString();
    }
}
