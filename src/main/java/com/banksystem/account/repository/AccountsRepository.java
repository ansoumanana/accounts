package com.banksystem.account.repository;

import com.banksystem.account.models.Accounts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends CrudRepository<Accounts,Long> {

    Optional<Accounts> findByCustomerId(int customerId);
}
