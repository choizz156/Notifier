package io.github.choizz.notifier.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.choizz.notifier.core.domain.model.NotificationType;
import io.github.choizz.notifier.persistence.entity.MockUserEntity;

public interface MockUserJpaRepository extends JpaRepository<MockUserEntity, Long> {

	@Query("""
		SELECT u.id 
			FROM MockUserEntity u 
		JOIN u.notificationSettings s 
			WHERE KEY(s) = :type AND VALUE(s) = true
			AND u.id > :lastId
		ORDER BY u.id ASC
		"""
	)
	List<Long> findIdsBySubscribedType(@Param("type") NotificationType type, @Param("lastId") Long lastId, Limit limit);
}
