package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

//회원정보를 담을 객체
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    //회원정보를 중복검증하는 멤소드
    Boolean existsByUsername(String username);

    //username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    UserEntity findByUsername(String username);
}
