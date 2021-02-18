package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne  // 다가 되는 쪽에서 관계의 매핑을 Event의 FK로 가지고 있는걸 젤 많이 씀.
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt; // 선착순이니 순서 정렬 기준이 될 것임

    private boolean accepted;

    private boolean attended;

}
