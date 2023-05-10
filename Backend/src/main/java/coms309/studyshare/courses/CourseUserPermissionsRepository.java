package coms309.studyshare.courses;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CourseUserPermissionsRepository extends JpaRepository<CourseUserPermissions, Long> {
    CourseUserPermissions findCourseUserPermissionsByCourseIDAndUserID(UUID courseID, UUID userID);

    @Transactional
    void deleteByCourseIDAndUserID(UUID courseID, UUID userID);

    @Transactional
    void deleteAllByCourseID(UUID courseID);

    @Transactional
    void deleteAllByUserID(UUID userID);

    @Transactional
    void deleteAll();
}
