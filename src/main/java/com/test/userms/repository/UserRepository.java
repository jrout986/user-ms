package com.test.userms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.userms.bean.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
