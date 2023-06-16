package fon.master.repository;

import fon.master.model.SimpleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimpleDataRepository extends JpaRepository<SimpleData, Integer> {
    List<SimpleData> findByValue(String value);

    @Query(value = "select max(id) from SimpleData")
    Optional<Integer> getMaxId();
}
