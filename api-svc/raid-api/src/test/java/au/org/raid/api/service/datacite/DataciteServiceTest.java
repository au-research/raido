package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.datacite.DataciteRequestFactory;
import au.org.raid.api.spring.config.DataciteProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class DataciteServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private DataciteProperties properties;
    @Mock
    private DataciteRequestFactory dataciteRequestFactory;
    @InjectMocks
    private DataciteService dataciteService;


}
