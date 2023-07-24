This is what the sitemap index and sitemaps for raid and raido would look like. 
See [BuildSearchIndex.java](/api-svc/spring/src/main/java/raido/cmdline/BuildSearchIndex.java)

### `sitemap_index.xml`

This isn't part of the "build index process", I'd probably just hardcode it 
into the CDK infra on `raid.org`.

```
<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="https://www.sitemaps.org/schemas/sitemap/0.9">
    <sitemap>
        <loc>https://raid.org/raido/raido_sitemap.xml</loc>
    </sitemap>
    <sitemap>
        <loc>https://raid.org/surf/surf_sitemap.xml</loc>
    </sitemap>
</sitemapindex>
```

### `raido/raido_sitemap.xml`

10K raids per file * 50K link files per index is half a billion raids, per 
registration-agent.

When that starts looking like an issue, we can look at another implementation.  
There's no rule against (and I believe Google explicitly support) nested 
sitemap indexes. One level of that is 25 quadrillion.  

```
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9">
    <url>
        <loc>https://raid.org/raido/raido-raid-link-index-000001.html</loc>
        <lastmod>2023-07-24</lastmod>
        <changefreq>weekly</changefreq>
    </url>
    <url>
        <loc>https://raid.org/raido/raido-raid-link-index-000002.html</loc>
        <lastmod>2023-07-24</lastmod>
        <changefreq>weekly</changefreq>
    </url>
</urlset>
```

### `raido/raido-raid-link-index-000001.html`
```
<html><head>List of raids for raido</head><body>
<br/><a href="https://demo.raid.org.au/10378.1/1712236">https://demo.raid.org.au/10378.1/1712236</a>
<br/><a href="https://demo.raid.org.au/10378.1/1712188">https://demo.raid.org.au/10378.1/1712188</a>
...
</body></html>
```

### `raido/raido-raid-link-index-000002.html`
```
<html><head>List of raids for raido</head><body>
<br/><a href="https://demo.raid.org.au/10378.1/1800001">https://demo.raid.org.au/10378.1/1800001</a>
<br/><a href="https://demo.raid.org.au/10378.1/1800002">https://demo.raid.org.au/10378.1/1800002</a>
...
</body></html>
```
