package com.vuhlog.money_keeper.service;

import com.nimbusds.jose.JOSEException;
import com.vuhlog.money_keeper.dto.request.AuthenticationRequest;
import com.vuhlog.money_keeper.dto.request.IntrospectRequest;
import com.vuhlog.money_keeper.dto.request.LogoutRequest;
import com.vuhlog.money_keeper.dto.request.RefreshRequest;
import com.vuhlog.money_keeper.dto.response.AuthenticationResponse;
import com.vuhlog.money_keeper.dto.response.IntrospectResponse;
import com.vuhlog.money_keeper.entity.Users;

import java.text.ParseException;
import java.util.Map;

public interface AuthenticationService {

    //verify token
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    //check username, password -> generate token
    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse authenticateGoogle(Map<String,String> body);

    public String generateToken(Users user);

    public void logout(LogoutRequest request) throws JOSEException, ParseException;

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
