package com.example.chatcommon;

import com.example.chatcommon.utils.JwtUtils;

public class jwt_test {
    public static void main(String[] args) {
        boolean b = JwtUtils.isTokenExpired("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVNTg3MTQ1OTY4MzgiLCJ1c2VyVHlwZSI6IjAiLCJwbGF0Zm9ybSI6IjAiLCJpYXQiOjE3MzEzMTM3MzksImV4cCI6MTczMTM3MzczOX0.FoQvVSIfKz3WcxO4y0GFbuh9dLlGpi3NNRbj0BrxrfM");
        System.out.println(b);
    }
}
