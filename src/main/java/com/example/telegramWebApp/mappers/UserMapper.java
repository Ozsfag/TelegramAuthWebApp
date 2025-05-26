package com.example.telegramWebApp.mappers;

import com.example.telegramWebApp.entities.User;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  User dataToUser(Map<String, String> initData);

  User updateUser(User existingUser, @MappingTarget User currentUser);
}
