package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTypeDTO implements Serializable {

    private Long id;
    private String type;
    private String description;
    private Boolean status;

}
