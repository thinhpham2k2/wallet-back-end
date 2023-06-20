package com.wallet.repository;

import com.wallet.entity.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTypeRepository extends JpaRepository<WalletType, Long> {

    List<WalletType> getAllByStatus(boolean status);

    WalletType getWalletTypeByStatusAndType(boolean status, String type);
}
