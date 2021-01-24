package com.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom((SignUpForm.class));
    }

    @Override
    public void validate(Object object, Errors errors) {
        // TODO email, nickname duplicated
        SignUpForm signUpForm = (SignUpForm)object;
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invaliad.email", new Object[]{signUpForm.getEmail()}, "이미 사용 중인 이메일 입니다.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("email", "invliad.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용 중인 닉네임 입니다.");
        }

    }
}
