package com.wallet.exception;

import com.wallet.exception.dto.PartnerErrorDTO;
import com.wallet.exception.dto.PartnerErrorUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartnerException extends RuntimeException {

    private PartnerErrorDTO partnerErrorDTO;

    private PartnerErrorUpdateDTO partnerErrorUpdateDTO;

    public PartnerException(String message, PartnerErrorDTO partnerErrorDTO, PartnerErrorUpdateDTO partnerErrorUpdateDTO) {
        super(message);
        this.partnerErrorDTO = partnerErrorDTO;
        this.partnerErrorUpdateDTO = partnerErrorUpdateDTO;
    }
}
