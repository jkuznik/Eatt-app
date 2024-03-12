package pl.jkuznik.data.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Test
    void getRecordById(){
        User user = new User();
        BDDMockito.given(userRepository.findById(5L)).willReturn(Optional.of(user));
        Assertions.assertThat(userService.get(5L)).isEqualTo(Optional.of(user));
    }
    @Test
    void updateOneRecord(){
        User user = new User();
//        BDDMockito.given(userRepository.save(user)).getMock();
        BDDMockito.given(userRepository.save(user)).willReturn(user);

        userService.update(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }
    @Test
    void updateListOfRecords(){
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        List<User> users = List.of(user1, user2, user3);

        BDDMockito.given(userRepository.saveAll(users)).willReturn(users);

        userService.updateAll(users);
        userService.updateAll(users);
        userService.updateAll(users);

        Mockito.verify(userRepository, Mockito.times(3)).saveAll(users);
    }

    @Test
    void deleteRecordById(){
        BDDMockito.doNothing().when(userRepository).deleteById(5L);
        userService.delete(5L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(5L);
    }
    @Test
    void getUsersListWhenDataBaseIsEmpty(){
        List<User> users = new ArrayList<>();
        BDDMockito.given(userRepository.findAll()).willReturn(users);

        Assertions.assertThat(userService.list().isEmpty());
    }

    @Test
    void countDataBaseUsersListSize() {
        BDDMockito.given(userRepository.count()).willReturn(10L);
        Assertions.assertThat(userService.count()).isEqualTo(10);
    }
}
