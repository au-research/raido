package raido.apisvc.service.raid;

import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.TitleType;

import java.util.List;

public class RaidoSchemaV1Util {

  public static List<TitleBlock> getPrimaryTitles(List<TitleBlock> titles) {
    return titles.stream().
      filter(i->i.getType() == TitleType.PRIMARY_TITLE).toList();
  }


}
