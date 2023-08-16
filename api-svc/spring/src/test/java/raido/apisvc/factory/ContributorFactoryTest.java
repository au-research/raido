package raido.apisvc.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContributorFactoryTest {
    @Mock
    private ContributorPositionFactory positionFactory;

    @Mock
    private ContributorRoleFactory roleFactory;

    @InjectMocks
    private ContributorFactory contributorFactory;

    @Test
    @DisplayName("If ContributorBlock is null return null")
    void returnsNull() {
        assertThat(contributorFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If ContributorBlock has empty fields return empty fields")
    void returnsEmptyFields() {
        assertThat(contributorFactory.create(new ContributorBlock()), is(new Contributor()));
    }

    @Test
    @DisplayName("All fields are set")
    void setsAllFields() {
        final var orcid = "0000-0002-6492-9025";
        final var position1 = new ContributorPosition();
        final var position2 = new ContributorPosition();
        final var position3 = new ContributorPositionWithSchemeUri();
        final var position4 = new ContributorPositionWithSchemeUri();

        final var role1 = new ContributorRole();
        final var role2 = new ContributorRole();
        final var role3 = new ContributorRoleWithSchemeUri();
        final var role4 = new ContributorRoleWithSchemeUri();

        when(positionFactory.create(position1)).thenReturn(position3);
        when(positionFactory.create(position2)).thenReturn(position4);
        when(roleFactory.create(role1)).thenReturn(role3);
        when(roleFactory.create(role2)).thenReturn(role4);

        final var contributor = new ContributorBlock()
            .id(orcid)
            .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_)
            .positions(List.of(position1, position2))
            .roles(List.of(role1, role2));

        final var result = contributorFactory.create(contributor);

        assertThat(result, is(new Contributor()
            .id(orcid)
            .identifierSchemeUri("https://orcid.org/")
            .positions(List.of(position3, position4))
            .roles(List.of(role3, role4))
        ));
    }
}