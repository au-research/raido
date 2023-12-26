package au.org.raid.api.factory;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContributorFactoryTest {
    @Mock
    private ContributorPositionFactory positionFactory;

    @Mock
    private ContributorRoleFactory roleFactory;

    @InjectMocks
    private ContributorFactory contributorFactory;

}