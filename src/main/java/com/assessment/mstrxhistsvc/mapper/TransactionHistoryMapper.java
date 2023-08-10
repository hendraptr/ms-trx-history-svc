package com.assessment.mstrxhistsvc.mapper;

import com.assessment.mstrxhistsvc.models.entity.TransactionHistory;
import com.assessment.mstrxhistsvc.models.dto.TransactionHistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionHistoryMapper {

    TransactionHistoryMapper INSTANCE = Mappers.getMapper(TransactionHistoryMapper.class);

    TransactionHistoryDTO toDTO(TransactionHistory entity);

    TransactionHistory toEntity(TransactionHistoryDTO dto);


}
