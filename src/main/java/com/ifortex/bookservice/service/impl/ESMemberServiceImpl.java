package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.repository.MemberRepository;
import com.ifortex.bookservice.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Attention! It is FORBIDDEN to make any changes in this file!
@Service
public class ESMemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Member findMember() {
        // will be implemented shortly
        return memberRepository.findMemberWithOldestRomanceBook();
    }

    @Override
    public List<Member> findMembers() {
        // will be implemented shortly
        return memberRepository.findMembersRegisteredIn2023WithoutBooks();
    }
}
