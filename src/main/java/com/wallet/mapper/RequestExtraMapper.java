package com.wallet.mapper;

import com.wallet.dto.RequestExtraDTO;
import com.wallet.dto.TransactionDTO;
import com.wallet.entity.Request;
import com.wallet.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestExtraMapper {

    RequestExtraMapper INSTANCE = Mappers.getMapper(RequestExtraMapper.class);

    @Mapping(target = "partnerId", source = "partner.id")
    @Mapping(target = "partnerName", source = "partner.fullName")
    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "type", source = "type.type")
    @Mapping(target = "transactionList", source = "transactionList", qualifiedByName = "mapTransaction")
    RequestExtraDTO toDTO(Request entity);

    @Named("mapTransaction")
    default List<TransactionDTO> mapTransaction(List<Transaction> transactionEntityList) {
        List<TransactionDTO> transactionList = transactionEntityList.stream().map(TransactionMapper.INSTANCE::toDTO).toList();
        if (!transactionList.isEmpty()) {
            return transactionList;
        }
        return null;
    }
}
