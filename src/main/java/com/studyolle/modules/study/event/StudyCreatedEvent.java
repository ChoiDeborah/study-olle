package com.studyolle.modules.study.event;

import com.studyolle.modules.study.Study;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
