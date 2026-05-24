package io.github.choizz.notifier.scheduler.spring.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SpringSchedulerConfig.class)
class SpringSchedulerConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @DisplayName("스프링 스케줄러 설정이 정상적으로 로드되는지 확인한다.")
    @Test
    void test1() {
        SpringSchedulerConfig config = applicationContext.getBean(SpringSchedulerConfig.class);
        assertThat(config).isNotNull();
    }
}
