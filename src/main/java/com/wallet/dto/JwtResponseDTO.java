package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {
    private PartnerDTO partnerDTO;

    private AdminDTO adminDTO;

    private String token;

    @Override
    public String toString() {
        if(this.partnerDTO != null){
            return "Partner: " + this.partnerDTO +", token='" + token;
        } else if(this.adminDTO != null) {
            return "Admin: " + this.adminDTO +", token='" + token;
        } else {
            return null;
        }
    }
}

