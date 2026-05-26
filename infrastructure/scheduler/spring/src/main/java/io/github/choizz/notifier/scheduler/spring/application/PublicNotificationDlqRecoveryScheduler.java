package io.github.choizz.notifier.scheduler.spring.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import io.github.choizz.notifier.core.application.dto.DlqRecoveryTarget;
import io.github.choizz.notifier.core.application.port.out.DlqPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicNotificationDlqRecoveryScheduler {

	private final DlqPort dlqPort;
	private final ApplicationEventPublisher eventPublisher;

	@Scheduled(cron = "${scheduler.dlq-recovery.cron:0 0/5 * * * ?}") // 기본값 5분마다
	@SchedulerLock(name = "dlq_recovery_lock", lockAtLeastFor = "30s", lockAtMostFor = "5m")
	public void recoverDlq() {
		log.info("DLQ 재처리 스케줄러 시작");
		List<DlqRecoveryTarget> targets = dlqPort.findPendingDlqs(100);

		if (targets.isEmpty()) {
			return;
		}

		for (DlqRecoveryTarget target : targets) {
			try {
				dlqPort.markAsResolved(target.dlqId());
				eventPublisher.publishEvent(target.event());
				log.info("DLQ 이벤트 재발행 완료 - DLQ ID: {}", target.dlqId());
			} catch (Exception e) {
				log.warn("DLQ 재처리 실패 - DLQ ID: {}", target.dlqId(), e);
			}
		}
		log.info("DLQ 재처리 스케줄러 완료 - 처리 대상 수: {}", targets.size());
	}
}
