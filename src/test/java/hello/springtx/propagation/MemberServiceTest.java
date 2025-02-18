package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;


    /*
    *  memberService     tx : off
    * memberRepository   tx : on
    * logRepository      tx : on
    */
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /*
     *  memberService     tx : off
     * memberRepository   tx : on
     * logRepository      tx : on Exception
     */
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /*
     *  memberService     tx : on
     * memberRepository   tx : off
     * logRepository      tx : off
     */
    @Test
    void singleTx() {
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /*
     *  memberService     tx : on
     * memberRepository   tx : on
     * logRepository      tx : on
     */
    @Test
    void outerTxOn_success() {
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /*
     *  memberService     tx : on
     * memberRepository   tx : on
     * logRepository      tx : on Exception
     */
    @Test
    void outerTxOn_fail() {
        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then 모든 데이터 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /*
     *  memberService     tx : on
     * memberRepository   tx : on
     * logRepository      tx : on Exception
     */
    @Test
    void recoverException_fail() {
        // given
        String username = "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /*
     *  memberService     tx : on
     * memberRepository   tx : on
     * logRepository      tx : on(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        // given
        String username = "로그예외_recoverException_success ";

        // when
        memberService.joinV2(username);

        // then member 저장, log 롤백
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }


}