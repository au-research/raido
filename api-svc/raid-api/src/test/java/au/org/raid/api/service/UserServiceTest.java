package au.org.raid.api.service;

import au.org.raid.api.dto.UserDto;
import au.org.raid.api.factory.UserDtoFactory;
import au.org.raid.api.repository.AppUserRepository;
import au.org.raid.db.jooq.tables.records.AppUserRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private AppUserRepository userRepository;
    @Mock
    private UserDtoFactory userDtoFactory;
    @InjectMocks
    private UserService userService;

    @Test
    void findAllByServicePointId() {
        final var servicePointId = 123L;

        final var record = new AppUserRecord();
        final var user = new UserDto();

        when(userRepository.findAllByServicePointId(servicePointId)).thenReturn(List.of(record));
        when(userDtoFactory.create(record)).thenReturn(user);

        assertThat(userService.findAllByServicePointId(servicePointId), is(List.of(user)));
    }

    @Test
    @DisplayName("findAllByServicePoint() returns empty list of none found")
    void findAllByServicePointIdReturnsEmptyList() {
        final var servicePointId = 123L;

        when(userRepository.findAllByServicePointId(servicePointId)).thenReturn(Collections.emptyList());
        assertThat(userService.findAllByServicePointId(servicePointId), is(Collections.emptyList()));

        verifyNoInteractions(userDtoFactory);
    }
}