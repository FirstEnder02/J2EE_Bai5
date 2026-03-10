package J2EE_Bai5.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import J2EE_Bai5.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}