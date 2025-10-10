package com.example.wishlist.repository;

import com.example.wishlist.models.User;
import org.springframework.data.repository.CrudRepository;

//Указали в <> с чем работаем и тип данных для PK
public interface UserRepository extends CrudRepository<User, Long> {

}
