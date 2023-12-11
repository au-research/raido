package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.DatacitePayloadFactory;
import au.org.raid.api.spring.config.DataciteProperties;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import org.mockito.Mock;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class DataciteServiceTest {

    @Mock
    private DataciteProperties mockProperties;
    private DataciteService service;

    @BeforeEach
    void setUp() {

        mockProperties = mock(DataciteProperties.class);
        when(mockProperties.getUser()).thenReturn("AAA.BBBB");
        when(mockProperties.getPassword()).thenReturn("");
        when(mockProperties.getEndpoint()).thenReturn("https://api.test.datacite.org");
        when(mockProperties.getPrefix()).thenReturn("10.82841");

        service = new DataciteService(mockProperties);
    }

    @Test
    public void testCreateDataciteRaid() throws Exception {
        // Arrange
        JsonNode mockInputStreamRootNode = mock(JsonNode.class);
        JsonNode mockDataNode = mock(JsonNode.class);
        RaidCreateRequest mockRequest = mock(RaidCreateRequest.class);

        when(mockInputStreamRootNode.path("data")).thenReturn(mockDataNode);
        when(mockDataNode.path("id")).thenReturn(mockDataNode); // if the id is a child of data
        when(mockDataNode.asText()).thenReturn("yourDesiredValue");

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        InputStream mockInputStream = mock(InputStream.class);
        DatacitePayloadFactory mockPayloadFactory = mock(DatacitePayloadFactory.class);

        when(mockProperties.getEndpoint()).thenReturn("https://api.test.datacite.org/dois");
        when(mockProperties.getPrefix()).thenReturn("10.82841");
        when(mockProperties.getUser()).thenReturn("mockUser");
        when(mockProperties.getPassword()).thenReturn("mockPassword");
        // Add other stubbing as necessary

        // Assuming you can inject these mocks into your service
        DataciteService service = new DataciteService(mockProperties);

        DataciteMintResponse.Identifier.Property property = new DataciteMintResponse.Identifier.Property();
        property.index = 1;
        property.type = "DESC";
        property.value = "RAID handle";

        ServicePoint servicePoint = new ServicePoint();
        servicePoint.setId(20000000L);


        Owner owner = new Owner();
        owner.setId("AAA.BBBB");
        owner.setServicePoint(servicePoint.getId());


        Id id = new Id();
        id.setId("10.82841/12345678");
        id.setLicense("https://creativecommons.org/licenses/by/4.0/");
        id.setOwner(owner);
        id.setGlobalUrl("https://doi.org/10.82841/12345678");

        when(mockRequest.getIdentifier()).thenReturn(id);



        Date date = new Date();
        date.setStartDate("2020-01-01");

        when(mockRequest.getDate()).thenReturn(date);




        DataciteMintResponse.Message message = new DataciteMintResponse.Message();
        message.type = null;
        message.value = null;


        mockRequest.setIdentifier(id);

        // Act
//        String result = service.createDataciteRaid(mockRequest, "mockHandle");
        String responseHandle = mockInputStreamRootNode.path("data").path("id").asText();

        // Assert
        // Verify that the method behaves as expected
        // Verify interactions with mocks
        // Assert on the returned result
    }
    @Test
    void testGetDataciteSuffix_Format() throws Exception {

        // Access the private method using reflection
        Method method = DataciteService.class.getDeclaredMethod("getDataciteSuffix");
        method.setAccessible(true);
        String suffix = (String) method.invoke(service);

        // Check if the suffix matches the UUID format (first part)
        assertTrue(suffix.matches("[a-f0-9]{8}"), "Suffix should match UUID first part format");
    }

    @Test
    void testGetDataciteSuffix_Uniqueness() throws Exception {
        Method method = DataciteService.class.getDeclaredMethod("getDataciteSuffix");
        method.setAccessible(true);

        Set<String> uniqueSuffixes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String suffix = (String) method.invoke(service);
            uniqueSuffixes.add(suffix);
        }

        // Assert that all generated suffixes are unique
        assertEquals(1000, uniqueSuffixes.size(), "All suffixes should be unique");
    }

    @Test
    void testGetDataciteHandle() {
        String handle = service.getDataciteHandle();
        assertTrue(handle.startsWith("10.82841/"), "Handle should start with the prefix followed by '/'");
    }

}
