package com.anpopo.social.module.settings.validator;

import com.anpopo.social.module.account.repository.AccountRepository;
import com.anpopo.social.module.settings.form.AccountForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class AccountFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccountForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountForm accountForm = (AccountForm) target;

        if (accountRepository.existsByNickname(accountForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", "해당 닉네임은 사용할 수 없습니다.");
        }
    }
}
