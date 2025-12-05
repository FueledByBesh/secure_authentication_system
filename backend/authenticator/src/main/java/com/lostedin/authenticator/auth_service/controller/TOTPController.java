package com.lostedin.authenticator.auth_service.controller;


import com.lostedin.authenticator.auth_service.constants.TOTPSecret;
import com.lostedin.authenticator.auth_service.model.QrUtil;
import com.lostedin.authenticator.auth_service.model.two_fa.TOTP;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/totp")
@RequiredArgsConstructor
public class TOTPController {

    private final TOTP totp;
    private final TOTPSecret secret;

    @GetMapping("verify-code")
    protected ResponseEntity<@NonNull String> verify_code(@RequestParam(value = "code") String code){

        var a = this.totp.verifyCode(this.secret.getTotpSecret(),code,2);
        if(a){
            return ResponseEntity.ok().body("Code verified");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Code not verified");

    }

    @GetMapping(value = "/totp-qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> totp_qr(@RequestParam(value = "size", required = false, defaultValue = "256") int size) {
        String totp_uri = totp.buildOtpAuthUri("Olzhas","FueledByBesh",secret.getTotpSecret());

        byte[] png = QrUtil.generatePng(totp_uri, size);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }


    @GetMapping("/generate-secret")
    protected String generate_secret(){
        return totp.generateSecret(20);
    }

}
