package com.wallet.exception;

import com.wallet.exception.dto.AdminErrorDTO;
import com.wallet.exception.dto.AdminErrorUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminException extends RuntimeException{

    private AdminErrorUpdateDTO adminErrorUpdateDTO;

    private AdminErrorDTO adminErrorDTO;

    public AdminException(String message, AdminErrorUpdateDTO adminErrorUpdateDTO, AdminErrorDTO adminErrorDTO) {
        super(message);
        this.adminErrorUpdateDTO = adminErrorUpdateDTO;
        this.adminErrorDTO = adminErrorDTO;
    }
}
