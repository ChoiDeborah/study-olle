package com.studyolle.modules.account;

import com.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.infra.MockMvcTest;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest extends AbstractContainerBaseTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;

    @MockBean
    EmailService emailService;

    @DisplayName("인증 메일 확인 - 입력 값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "eeeeeeefadf")
                .param("email", "aaa@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 입력 값 정상")
    @Test
    void checkEmailToken_with_right_input() throws Exception {
        Account account = Account.builder()
                .email("aaa@gmail.com")
                .password("123455678")
                .nickname("abc")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail())
                .param("nickname", newAccount.getNickname()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("abc"));
    }

    @DisplayName("회원가입 화면 보이는 지 테스트")
    @Test
    void SignUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                    .param("nickname", "jiminchoi")
                    .param("email", "jiminchoi@gmail.com")
                    .param("password", "12345678")
                    .with(csrf()))  // csrf 토큰
                .andExpect(status().is3xxRedirection()) // CSRF가 활성화 되어 있다
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("jiminchoi@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(account.getEmailCheckToken());
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "jimin")
                .param("email", "jimin@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("jimin"));

        Account account = accountRepository.findByEmail("jimin@email.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(account.getEmailCheckToken());
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }
}