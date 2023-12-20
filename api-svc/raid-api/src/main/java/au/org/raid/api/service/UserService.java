package au.org.raid.api.service;

import au.org.raid.api.dto.UserDto;
import au.org.raid.api.factory.UserDtoFactory;
import au.org.raid.api.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final AppUserRepository userRepository;
    private final UserDtoFactory userDtoFactory;

    public List<UserDto> findAllByServicePointId(final Long id) {
        return userRepository.findAllByServicePointId(id)
                .stream()
                .map(userDtoFactory::create)
                .toList();
    }
}
