package au.org.raid.api.service;

import au.org.raid.api.factory.RelatedRaidFactory;
import au.org.raid.api.factory.record.RelatedRaidRecordFactory;
import au.org.raid.api.repository.RelatedRaidRepository;
import au.org.raid.api.repository.RelatedRaidTypeRepository;
import au.org.raid.api.repository.RelatedRaidTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.RelatedRaid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RelatedRaidService {
    private final RelatedRaidRepository relatedRaidRepository;
    private final RelatedRaidRecordFactory relatedRaidRecordFactory;
    private final RelatedRaidTypeService relatedRaidTypeService;
    private final RelatedRaidFactory relatedRaidFactory;
    public void create(final List<RelatedRaid> relatedRaids, final String handle) {
        if (relatedRaids == null) {
            return;
        }

        for (final var relatedRaid : relatedRaids) {
            final var typeId = relatedRaidTypeService.findId(relatedRaid.getType());
            final var relatedRaidRecord = relatedRaidRecordFactory.create(handle, relatedRaid.getId(), typeId);
            relatedRaidRepository.create(relatedRaidRecord);
        }
    }

    public List<RelatedRaid> findAllByHandle(final String handle) {
        final var relatedRaids = new ArrayList<RelatedRaid>();
        final var records = relatedRaidRepository.findAllByHandle(handle);

        for (final var record : records) {
            final var type = relatedRaidTypeService.findById(record.getRelatedRaidTypeId());
            relatedRaidFactory.create(record.getRelatedRaidHandle(), type);
        }

        return relatedRaids;
    }

    public void update(final List<RelatedRaid> relatedRaids, final String handle) {
        relatedRaidRepository.deleteAllByHandle(handle);
        create(relatedRaids, handle);
    }
}