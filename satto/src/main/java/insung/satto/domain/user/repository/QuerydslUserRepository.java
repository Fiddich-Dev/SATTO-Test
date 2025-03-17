package insung.satto.domain.user.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import insung.satto.domain.user.entity.QUser;
import insung.satto.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuerydslUserRepository {

    private final JPAQueryFactory query;

    public QuerydslUserRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<User> findAll() {
        QUser user = QUser.user;

        List<User> users = query
                .select(user)
                .from(user)
                .fetch();

        return users;
    }

    public boolean existsByStudentId(String studentId) {
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(user.studentId.eq(studentId));

        Long findUsersCount = query
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return findUsersCount >= 1;
    }
}
