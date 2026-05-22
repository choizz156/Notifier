package io.github.choizz.adapter;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;

import io.github.choizz.entity.AlarmEntity;
import io.github.choizz.notifier.application.port.out.AlarmPersistencePort;
import io.github.choizz.notifier.domain.model.Alarm;
import io.github.choizz.notifier.domain.model.AlarmStatus;
import io.github.choizz.repository.AlarmJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Repository
public class AlarmPersistenceAdapter implements AlarmPersistencePort {

	private final AlarmJpaRepository alarmJpaRepository;

	@Override
	public Alarm save(Alarm alarm) {

		AlarmEntity entity = AlarmMapper.toEntity(alarm);
		AlarmEntity AlarmEntity = alarmJpaRepository.save(entity);

		return AlarmMapper.toDomain(AlarmEntity);
	}

	@Override
	public void updateStatus(long id, AlarmStatus alarmStatus) {

		AlarmEntity entity = alarmJpaRepository.findById(id).orElseThrow();
		Alarm alarm = AlarmMapper.toDomain(entity);

		switch (alarmStatus) {
			case COMPLETED -> alarm.markAsCompleted();
			case FAILED -> alarm.markAsFailed();
			case RETRYING -> alarm.markAsRetrying();
			case SENDING -> alarm.markAsSending();
		}

		AlarmEntity updatedEntity = AlarmMapper.toEntity(alarm);
		updatedEntity.id(entity.id());
		alarmJpaRepository.save(updatedEntity);
	}

	@Override
	public Alarm findById(Long id) {

		AlarmEntity entity = alarmJpaRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("알람을 찾을 수 없습니다. id=" + id)
			);

		return AlarmMapper.toDomain(entity);
	}

	@Override
	public Alarm findBySubscriberId(Long subscriberId) {

		AlarmEntity entity = alarmJpaRepository.findBySubscriberId(subscriberId)
			.orElseThrow(() ->
				new NoSuchElementException("구독자의 알람을 찾을 수 없습니다. subscriberId=" + subscriberId)
			);

		return AlarmMapper.toDomain(entity);
	}
}
