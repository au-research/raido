
* [agency-public-data-export](#agency-public-data-export)
  * export public raid data for an entire raid-agency so it can be published
* [sp-private-data-export](#sp-private-data-export)
  * export all raid for for a single service-point so it can be downloaded/used
    directly by the service-point themselves


# agency-public-data-export

Initiated by a raid-agency to export all data for "open" raids and the
publicly available data for "closed" raids (i.e handle, create-date,
access-statement).

The intent of the public-data-export is store the raid data on a public data
repository like Zenodo, so it can be downloaded and used by anybody anywhere.


## Publishing

In the near-term, the job will just upload the export-archive to S3, then a
dev will manually download that and upload it to Zenodo.

In the long-term, we'll use the Zenodo API to publish the export-archive
directly with no manual steps.


## Dealing with large archive size

Short-term, we're not going to worry about it, we're just going to re-export all
the data for the raid-agency every time and upload the new version to Zenodo.

Long-term, we need to figure out a scheme for doing incremental exports, so that
we're not re-exporting all the raids that have already been exported and
haven't been changed.  Before doing this, we'll have to implement actual
tracking of the update date in the raid table - we don't currently do this,
we only store the date the raid was created.


### Indexing

We also plan to use the output of the open-data-export to create the
"search-index" for the global raid "search" functionality.

In the short term we're going to just publish the index of raids on `raid.org`
and publish the index to Google (and other search engines) to make raids
discoverable by using a Google search, e.g.
`https://www.google.com/search?q=site%3Araid.org+title+keywords`

Long-term, we may end up doing some kind of search functionality of our own,
i.e. indexing the data in a product/service like elastic-search and building
our own search functionality.


# sp-private-data-export

Initiated by a service-point to export all data for raids associated with their
service-point.

This will actually include all of the private data for closed raids.

In the short-term, the private-data-export will just upload the export-archive
to S3, then a dev will manually download that and transfer it to the
service-point by whatever means they make available (keeping in mind that the
archive contains closed raid data, so we can't just make it publicly available
to download).

Long-term, we'll probably build some kind of "export" UI for service-points
where they can requests a data-export be done (should be asynchronous) and then
download the archive from that same UI when it's finished.  Download will
probably be via a
[pre-signed URL](https://docs.aws.amazon.com/AmazonS3/latest/userguide/ShareObjectPreSignedURL.html).  
