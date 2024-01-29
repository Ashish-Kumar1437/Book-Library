package com.Library.Library.Repository;
import com.Library.Library.Entities.BookDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface BookRepository extends JpaRepository<BookDTO,Long>, JpaSpecificationExecutor<BookDTO> {

    List<BookDTO> findAll(Specification<BookDTO> specification);

}
