package com.Library.Library.Repository;

import com.Library.Library.Entities.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDTO,Long> {

}
