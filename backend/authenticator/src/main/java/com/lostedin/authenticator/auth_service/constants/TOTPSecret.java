package com.lostedin.authenticator.auth_service.constants;

import com.lostedin.authenticator.auth_service.model.two_fa.TOTP;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TOTPSecret {

    private final TOTP totp;
    @Getter
    private String totpSecret;


    @PostConstruct
    private void init(){
        this.update_secret();
    }

    public void update_secret(){
        this.totpSecret = totp.generateSecret(20);
    }


}
