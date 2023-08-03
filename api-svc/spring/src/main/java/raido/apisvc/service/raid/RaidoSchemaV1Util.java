package raido.apisvc.service.raid;

import raido.idl.raidv2.model.*;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class RaidoSchemaV1Util {

  public static List<TitleBlock> getPrimaryTitles(List<TitleBlock> titles) {
    return titles.stream().
      filter(i->i.getType() == TitleType.PRIMARY_TITLE).toList();
  }

  /**
   Should only call be called with validated metadata.  
   @throws java.util.NoSuchElementException if no primary title is present 
   */
  public static TitleBlock getPrimaryTitle(List<TitleBlock> titles) {
    return titles.stream().
      filter(i->i.getType() == TitleType.PRIMARY_TITLE).
      findFirst().orElseThrow();
  }

  public static Optional<DescriptionBlock> getFirstPrimaryDescription(
    List<DescriptionBlock> descriptions
  ) {
    return descriptions.stream().
      filter(i->i.getType() == DescriptionType.PRIMARY_DESCRIPTION).
      findFirst();
  }

  public static MintResponse mintFailed(List<ValidationFailure> failures){
    return new MintResponse().success(false).failures(failures);
  }

  public static MintResponse mintFailed(ValidationFailure failure){
    return new MintResponse().success(false).failures(singletonList(failure));
  }
  

}