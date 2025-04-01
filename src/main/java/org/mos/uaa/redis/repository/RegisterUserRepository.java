package org.mos.uaa.redis.repository;

import org.mos.uaa.redis.RegisterUserEntity;
import org.springframework.data.repository.CrudRepository;

public interface RegisterUserRepository extends CrudRepository<RegisterUserEntity, String> {
}
