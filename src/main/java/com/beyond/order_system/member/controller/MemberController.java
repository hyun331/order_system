package com.beyond.order_system.member.controller;

import com.beyond.order_system.common.dto.CommonResDto;
import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.member.dto.MemberDetResDto;
import com.beyond.order_system.member.dto.MemberListResDto;
import com.beyond.order_system.member.dto.MemberSaveReqDto;
import com.beyond.order_system.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/create") //@Valid - 이 데이터를 validation 해볼거다
    public ResponseEntity<CommonResDto> createMember(@Valid @RequestBody MemberSaveReqDto memberSaveReqDto){
        MemberDetResDto memberDetResDto = memberService.memberCreate(memberSaveReqDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "member is successfully created", memberDetResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<CommonResDto> memberList(@PageableDefault(size = 10, sort = "createdTime", direction = Sort.Direction.DESC)Pageable pageable){
        Page<MemberListResDto> memberListResDtos = memberService.memberList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "members are found", memberListResDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
