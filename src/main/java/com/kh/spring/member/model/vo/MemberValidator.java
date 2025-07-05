package com.kh.spring.member.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.kh.spring.member.model.vo.Member;

public class MemberValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Member.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Member member = (Member) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userId", "required", "아이디는 필수 입력입니다.");

        if (member.getUserPwd() == null || member.getUserPwd().trim().isEmpty()) {
            errors.rejectValue("userPwd", "required", "비밀번호는 필수 입력입니다.");
        } else if (member.getUserPwd().length() < 8) {
            errors.rejectValue("userPwd", "length", "비밀번호는 최소 8자 이상이어야 합니다.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "이메일은 필수 입력입니다.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "required", "전화번호는 필수 입력입니다.");
    }
}
