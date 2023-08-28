package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.Rtp;

public interface RtpRepository extends JpaRepository<Rtp, Integer> {
}
