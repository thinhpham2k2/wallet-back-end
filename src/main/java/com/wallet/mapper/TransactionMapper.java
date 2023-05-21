package com.wallet.mapper;

import com.wallet.dto.TransactionDTO;
import com.wallet.entity.Request;
import com.wallet.entity.Transaction;
import com.wallet.entity.Type;
import com.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "type", source = "type.type")
    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "requestId", source = "request.id")
    TransactionDTO toDTO(Transaction entity);

    @Mapping(target = "type", source = "typeId", qualifiedByName = "mapType")
    @Mapping(target = "wallet", source = "walletId", qualifiedByName = "mapWallet")
    @Mapping(target = "request", source = "requestId", qualifiedByName = "mapRequest")
    Transaction toEntity(TransactionDTO dto);

    @Named("mapType")
    default Type mapType(Long id) {
        Type type = new Type();
        type.setId(id);
        return type;
    }

    @Named("mapWallet")
    default Wallet mapWallet(Long id) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        return wallet;
    }

    @Named("mapRequest")
    default Request mapRequest(Long id) {
        Request request = new Request();
        request.setId(id);
        return request;
    }
}
