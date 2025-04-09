package io.flowinquiry.modules.collab.repository;

import io.flowinquiry.modules.collab.domain.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, String> {}
