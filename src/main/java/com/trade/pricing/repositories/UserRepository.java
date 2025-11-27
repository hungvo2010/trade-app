package com.trade.pricing.repositories;

import com.trade.pricing.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    public List<User> getUsers();

    List<User> findAll();

    User save(User user);
}
