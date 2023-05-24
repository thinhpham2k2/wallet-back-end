package com.wallet.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {

    private String token;

    private PartnerDTO partnerDTO;

    private AdminDTO adminDTO;


    @Override
    public String toString() {
        if(this.partnerDTO != null){
            return "Partner: " + this.partnerDTO +", token: " + token;
        } else if(this.adminDTO != null) {
            return "Admin: " + this.adminDTO +", token: " + token;
        } else {
            return null;
        }
    }
}

