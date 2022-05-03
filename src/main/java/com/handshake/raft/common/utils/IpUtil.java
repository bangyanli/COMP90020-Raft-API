package com.handshake.raft.common.utils;

public class IpUtil {

    public static int getPort(String address){
        String[] split = address.split(":");
        return Integer.parseInt(split[1]);
    }
}
