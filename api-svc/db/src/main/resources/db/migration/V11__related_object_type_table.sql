CREATE TABLE related_object_type
(
    name        varchar(255) NOT NULL primary key,
    description varchar(255) NOT NULL,
    url         varchar(255) NOT NULL
);


INSERT INTO related_object_type (name, description, url) values
    ('Audiovisual', 'An audiovisual related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/audiovisual.json'),
    ('Book', 'A book related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/book.json'),
    ('Book Chapter', 'A book chapter related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/book-chapter.json'),
    ('Computational Notebook', 'A computational notebook related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/computational-notebook.json'),
    ('Conference Paper', 'A conference paper related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/conference-paper.json'),
    ('Conference Poster', 'A conference poster related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/conference-poster.json'),
    ('Conference Proceeding', 'A conference proceeding related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/conference-proceeding.json'),
    ('Data Paper', 'A data paper related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/data-paper.json'),
    ('Dataset', 'A dataset related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/dataset.json'),
    ('Dissertation', 'A dissertation related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/dissertation.json'),
    ('Educational Material', 'An educational material related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/educational-material.json'),
    ('Event', 'An event related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/event.json'),
    ('Funding', 'A funding related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/funding.json'),
    ('Image', 'An image related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/image.json'),
    ('Instrument', 'An instrument related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/instrument.json'),
    ('Journal Article', 'A journal article related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/journal-article.json'),
    ('Model', 'A model related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/model.json'),
    ('Output Management Plan', 'An output management plan related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/output-management-plan.json'),
    ('Physical Object', 'A physical object related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/physical-object.json'),
    ('Preprint', 'A preprint related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/preprint.json'),
    ('Prize', 'A Prize related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/prize.json'),
    ('Report', 'A report related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/report.json'),
    ('Service', 'A service related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/service.json'),
    ('Software', 'A software related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/software.json'),
    ('Sound', 'A sound related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/sound.json'),
    ('Standard', 'A standard related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/standard.json'),
    ('Text', 'A text related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/text.json'),
    ('Workflow', 'A workflow related object.', 'https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/workflow.json');
