package com.example.demo.service;

import com.example.demo.dto.JoinDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

//회원가입
@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 아이디 중복 확인 로직
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);  // 존재하면 false 반환
    }

    public void joinProcess(JoinDTO joinDTO) {

        String company = joinDTO.getCompany();
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        String zipcode = joinDTO.getZipcode();
        String address1 = joinDTO.getAddress1();
        String address2 = joinDTO.getAddress2();
        String phone = joinDTO.getPhone();

        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");
        data.setCompany(company);
        data.setZipcode(zipcode);
        data.setAddress1(address1);
        data.setAddress2(address2);
        data.setPhone(phone);
        data.setJoinDate(LocalDate.now());

        userRepository.save(data);
    }
}
