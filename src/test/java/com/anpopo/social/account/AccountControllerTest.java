//package com.anpopo.social.account;
//
//import com.anpopo.social.account.domain.Account;
//import com.anpopo.social.account.repository.AccountRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Transactional
//@SpringBootTest
//@AutoConfigureMockMvc
//class AccountControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    AccountRepository accountRepository;
//
//    @DisplayName("회원가입 폼 요청")
//    @Test
//    void signUpForm () throws Exception {
//        mockMvc.perform(get("/sign-up"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("signUpForm"))
//                .andExpect(view().name("account/sign-up"));
//    }
//
//    @DisplayName("회원 가입 - 입력 값 오류")
//    @Test
//    void sgnUpForm_incorrect() throws Exception {
//        mockMvc.perform(post("/sign-up")
//                .param("nickname", "anpopo")
//                .param("email", "anpopo")
//                .param("password", "1234123")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("signUpForm"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원 가입 - 입력 값 정상")
//    @Test
//    void signUpForm_correct() throws Exception{
//        String email = "dkstpgud@gmail.com";
//
//        mockMvc.perform(post("/sign-up")
//                .param("nickname", "anpopo")
//                .param("email", email)
//                .param("password", "12341234")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/"));
//
//        Account findAccount = accountRepository.findByEmail(email);
//
//        assertNotNull(findAccount);
//
//        assertNotEquals(findAccount.getPassword(), "123456789");
//        // 유저 확인
//        assertTrue(accountRepository.existsByEmail(email));
//        assertNotNull(findAccount.getEmailCheckToken());
//
//    }
//
//
//    @DisplayName("인증 메일 확인 - 입력값 오류")
//    @Test
//    void checkEmailToken_with_wrong_input() throws Exception {
//
//        mockMvc.perform(get("/email-check-token")
//                .param("token", "asdfasdf")
//                .param("email", "email@email.com"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("error"))
//                .andExpect(view().name("account/checked-email"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("인증 메일 확인 - 입력값 정상")
//    @Test
//    void checkEmailToken() throws Exception {
//
////        // 유저저장
////        Account account = Account.builder()
////                .email("test@email.com")
////                .password("12345678")
////                .nickname("anpopo")
////                .build();
//
//        Account newAccount = accountRepository.save(account);
//
//        // 토큰생성
//        newAccount.generateEmailCheckToken();
//
//        // 테스트 수행
//        mockMvc.perform(get("/email-check-token")
//                .param("token", newAccount.getEmailCheckToken())
//                .param("email", newAccount.getEmail()))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeDoesNotExist("error"))
//                .andExpect(model().attributeExists("numberOfUser"))
//                .andExpect(model().attributeExists("nickname"))
//                .andExpect(view().name("account/checked-email"));
//    }
//
//
//
//
//}