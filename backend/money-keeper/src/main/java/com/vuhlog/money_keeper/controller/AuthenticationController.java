package com.vuhlog.money_keeper.controller;

import com.nimbusds.jose.JOSEException;
import com.vuhlog.money_keeper.dto.request.AuthenticationRequest;
import com.vuhlog.money_keeper.dto.request.IntrospectRequest;
import com.vuhlog.money_keeper.dto.request.LogoutRequest;
import com.vuhlog.money_keeper.dto.request.RefreshRequest;
import com.vuhlog.money_keeper.dto.response.ApiResponse;
import com.vuhlog.money_keeper.dto.response.AuthenticationResponse;
import com.vuhlog.money_keeper.dto.response.IntrospectResponse;
import com.vuhlog.money_keeper.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var res = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

    @PostMapping("/google")
    public ApiResponse<AuthenticationResponse> googleLogin(@RequestBody Map<String, String> body) {
        var res = authenticationService.authenticateGoogle(body);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var res = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(res)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var res = authenticationService.refreshToken(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> authenticate(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiResponse.<Void>builder()
                .build();
    }
}
