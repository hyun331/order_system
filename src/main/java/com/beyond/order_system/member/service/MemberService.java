package com.beyond.order_system.member.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.member.dto.MemberDetResDto;
import com.beyond.order_system.member.dto.MemberListResDto;
import com.beyond.order_system.member.dto.MemberSaveReqDto;
import com.beyond.order_system.member.repository.MemberRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;

    }

    public MemberDetResDto memberCreate(MemberSaveReqDto memberSaveReqDto){
        if(memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Member member = memberSaveReqDto.toEntity();

        return memberRepository.save(member).detFromEntity();

    }


    public Page<MemberListResDto> memberList(Pageable pageable) {
        Page<Member> memberList = memberRepository.findAll(pageable);
        Page<MemberListResDto> memberListResDtos = memberList.map(a->a.listFromEntity());
        return memberListResDtos;
    }
}
