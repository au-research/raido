CREATE TABLE related_raid_type
(
    name        varchar(255) NOT NULL primary key,
    description varchar(255) NOT NULL,
    url         varchar(255) NOT NULL
);


INSERT INTO related_raid_type (name, description, url) values
('Continues', 'Continues the related RAiD.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/continues.json'),
    ('HasPart', 'Has part of the related RAiD.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/has-part.json'),
    ('IsContinuedBy', 'Is continued by the related RAiD.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-continued-by.json'),
    ('IsDerivedFrom', 'Is derived from the related RAiD.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-derived-from.json'),
    ('IsIdenticalTo', 'Is identical to the related RAiD.','https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-identical-to.json'),
    ('IsObsoletedBy', 'Is obsoleted by the related RAiD.','https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-obsoleted-by.json'),
    ('IsPartOf', 'Is part of the related RAiD.','https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-part-of.json'),
    ('IsSourceOf', 'Is the source of the related RAiD.','https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-source-of.json'),
    ('Obsoletes', 'Obsoletes the related RAiD.','https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/obsoletes.json');

