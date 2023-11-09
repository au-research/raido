BEGIN TRANSACTION;

-- RELATED RAID TYPE

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/continues.json'
WHERE name = 'Continues';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/has-part.json'
WHERE name = 'HasPart';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/is-continued-by.json'
WHERE name = 'IsContinuedBy';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/is-derived-from.json'
WHERE name = 'IsDerivedFrom';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/is-identical-to.json'
WHERE name = 'IsIdenticalTo';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/is-obsoleted-by.json'
WHERE name = 'IsObsoletedBy';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/is-part-of.json'
WHERE name = 'IsPartOf';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/is-source-of.json'
WHERE name = 'IsSourceOf';

UPDATE raido.api_svc.related_raid_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/obsoletes.json'
WHERE name = 'Obsoletes';

-- RELATED OBJECT TYPE

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/audiovisual.json'
WHERE name = 'Audiovisual';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json'
WHERE name = 'Book Chapter';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book.json'
WHERE name = 'Book';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/computational-notebook.json'
WHERE name = 'Computational Notebook';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/conference-paper.json'
WHERE name = 'Conference Paper';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/conference-poster.json'
WHERE name = 'Conference Poster';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/conference-proceeding.json'
WHERE name = 'Conference Proceeding';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/data-paper.json'
WHERE name = 'Data Paper';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/dataset.json'
WHERE name = 'Dataset';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/dissertation.json'
WHERE name = 'Dissertation';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/educational-material.json'
WHERE name = 'Educational Material';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/event.json'
WHERE name = 'Event';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/funding.json'
WHERE name = 'Funding';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/image.json'
WHERE name = 'Image';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/instrument.json'
WHERE name = 'Instrument';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/journal-article.json'
WHERE name = 'Journal Article';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/model.json'
WHERE name = 'Model';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/output-management-plan.json'
WHERE name = 'Output Management Plan';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/physical-object.json'
WHERE name = 'Physical Object';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/preprint.json'
WHERE name = 'Preprint';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/prize.json'
WHERE name = 'Prize';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/report.json'
WHERE name = 'Report';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/service.json'
WHERE name = 'Service';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/software.json'
WHERE name = 'Software';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/sound.json'
WHERE name = 'Sound';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/standard.json'
WHERE name = 'Standard';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/text.json'
WHERE name = 'Text';

UPDATE raido.api_svc.related_object_type
SET uri = 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/workflow.json'
WHERE name = 'Workflow';

UPDATE raido.api_svc.related_object_type_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/'
WHERE id = 1;

UPDATE raido.api_svc.related_raid_type_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/related-raid/type/v1/'
WHERE id = 1;

UPDATE raido.api_svc.related_object_category_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/category/v1/'
WHERE id = 1;

UPDATE raido.api_svc.organisation_role_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1/'
WHERE id = 1;

UPDATE raido.api_svc.contributor_position_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/'
WHERE id = 1;

UPDATE raido.api_svc.access_type_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1/'
WHERE id = 1;

UPDATE raido.api_svc.description_type_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1/'
WHERE id = 1;

UPDATE raido.api_svc.title_type_scheme
SET uri = 'https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/'
WHERE id = 1;

END TRANSACTION;