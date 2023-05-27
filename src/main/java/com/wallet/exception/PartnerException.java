package com.wallet.exception;

import com.wallet.dto.PartnerDTO;
import com.wallet.exception.dto.PartnerErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PartnerException extends RuntimeException{

    private PartnerErrorDTO partnerErrorDTO;

    public PartnerException(String message, PartnerErrorDTO partnerErrorDTO) {
        super(message);
        this.partnerErrorDTO = partnerErrorDTO;
    }

    public PartnerErrorDTO getPartnerErrorDTO(){
        return partnerErrorDTO;
    }
}
