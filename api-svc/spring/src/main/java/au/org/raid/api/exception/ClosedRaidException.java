package au.org.raid.api.exception;

import au.org.raid.idl.raidv2.model.RaidDto;
import lombok.Getter;

@Getter
public class ClosedRaidException extends RuntimeException {
    private final RaidDto raid;

    public ClosedRaidException(RaidDto raid) {
        super();
        this.raid = raid;
    }
}
