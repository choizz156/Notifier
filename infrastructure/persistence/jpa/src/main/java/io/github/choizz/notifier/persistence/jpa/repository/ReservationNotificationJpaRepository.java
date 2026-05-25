package io.github.choizz.notifier.persistence.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.choizz.notifier.persistence.jpa.entity.ReservationNotificationEntity;

public interface ReservationNotificationJpaRepository extends JpaRepository<ReservationNotificationEntity, Long> {

	@Query("""
		SELECT r FROM ReservationNotificationEntity r
		WHERE r.isPublished = false
		AND r.reservationTime <= :time
		AND r.id > :lastId
		ORDER BY r.id ASC""")
	List<ReservationNotificationEntity> findUnpublishedBefore(
		@Param("time") LocalDateTime time,
		@Param("lastId") Long lastId,
		Limit limit
	);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ReservationNotificationEntity r SET r.isPublished = true WHERE r.id IN :ids")
	void markAsPublished(@Param("ids") List<Long> ids);
}
