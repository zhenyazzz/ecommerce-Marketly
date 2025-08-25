package org.com.productservice.repository.redis;


import org.com.productservice.model.Category;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRedisRepository extends CrudRepository<Category, Long> {
}
