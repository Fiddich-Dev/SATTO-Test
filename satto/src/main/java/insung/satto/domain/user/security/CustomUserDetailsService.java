package insung.satto.domain.user.security;



import insung.satto.domain.user.entity.User;
import insung.satto.domain.user.repository.QuerydslUserRepository;
import insung.satto.domain.user.repository.SpringDataJpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final QuerydslUserRepository querydslUserRepository;
    private final SpringDataJpaUserRepository springDataJpaUserRepository;


    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        //DB에서 조회
        User user = springDataJpaUserRepository.findByStudentId(studentId).orElseThrow();
        log.info("user role = {}", user.getRole());
        if (user != null) {
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(user);
        }
        return null;
    }

//    @Override
//    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
//
//        //DB에서 조회
//        User user = userRepository.findByStudentId(studentId);
//        log.info("user role = {}", user.getRole());
//        if (user != null) {
//            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
//            return new CustomUserDetails(user);
//        }
//        return null;
//    }
}
