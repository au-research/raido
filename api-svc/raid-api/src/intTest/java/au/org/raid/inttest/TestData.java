package au.org.raid.inttest;

import au.org.raid.idl.raidv1.model.RaidCreateModel;
import au.org.raid.idl.raidv1.model.RaidCreateModelMeta;
import net.bytebuddy.utility.RandomString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestData {
    public static RaidCreateModel raidCreateModel() {
        return new RaidCreateModel()
                .contentPath("http://localhost/" + RandomString.make())
                .startDate(LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .meta(new RaidCreateModelMeta()
                        .name(RandomString.make()))
                ;
    }
}