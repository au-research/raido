package raido.apisvc.service.raid;

import raido.idl.raidv2.model.MintResponse;
import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

import static java.util.Collections.singletonList;

public class RaidoSchemaV1Util {

  public static List<TitleBlock> getPrimaryTitles(List<TitleBlock> titles) {
    return titles.stream().
      filter(i->i.getType() == TitleType.PRIMARY_TITLE).toList();
  }

  public static MintResponse mintFailed(List<ValidationFailure> failures){
    return new MintResponse().success(false).failures(failures);
  }

  public static MintResponse mintFailed(ValidationFailure failure){
    return new MintResponse().success(false).failures(singletonList(failure));
  }
  

}
