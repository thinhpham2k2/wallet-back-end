package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wallet.entity.Customer;
import com.wallet.entity.Program;
import com.wallet.entity.Request;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
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
public class PartnerDTO implements Serializable {

    private Long id;
    private String userName;
    private String fullName;
    private String code;
    private String email;
    private String image;
    private String phone;
    private String address;
    private Boolean state;
    private Boolean status;

}
