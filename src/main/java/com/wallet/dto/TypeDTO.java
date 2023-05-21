package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wallet.entity.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeDTO implements Serializable {

    private Long id;
    private Boolean type;
    private Boolean status;

}
