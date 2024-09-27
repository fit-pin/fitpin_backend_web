package com.example.demo.controller;


import com.example.demo.dto.JoinDTO;
import com.example.demo.service.JoinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class JoinController {
    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinDTO joinDTO) {
        joinService.joinProcess(joinDTO);

        return "ok";
    }

    @PostMapping("/check-username")
    public boolean checkUsernameAvailability(@RequestBody JoinDTO joinDTO) {
        return joinService.isUsernameAvailable(joinDTO.getUsername());
    }
}
