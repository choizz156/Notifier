package io.github.choizz.notifier.persistence.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shedlock")
@Entity
public class ShedlockEntity {

	@Id
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	@Column(name = "lock_until", nullable = false)
	private LocalDateTime lockUntil;

	@Column(name = "locked_at", nullable = false)
	private LocalDateTime lockedAt;

	@Column(name = "locked_by", nullable = false)
	private String lockedBy;

	public ShedlockEntity(String name, LocalDateTime lockUntil, LocalDateTime lockedAt, String lockedBy) {
		this.name = name;
		this.lockUntil = lockUntil;
		this.lockedAt = lockedAt;
		this.lockedBy = lockedBy;
	}
}
