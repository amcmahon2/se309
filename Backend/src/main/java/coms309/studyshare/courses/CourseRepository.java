package coms309.studyshare.courses;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findCourseByJoinCode(String joinCode);
    Course findCourseByCourseName(String name);
    Course findByCourseID(UUID id);

    @EntityGraph(attributePaths = "users")
    List<Course> findAll();

    @Transactional
    void deleteByCourseName(String name);

    @Transactional
    void deleteCourseByJoinCode(String joinCode);

    @Transactional
    void deleteAll();

}
