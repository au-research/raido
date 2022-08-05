package raid.ddb_migration

class DdbS3ItemTable {
  static void guardS3ExportRaidTableItem(
    Object iJson,
    boolean strict = true
  ) {
    assert iJson.Item
    assert iJson.Item.handle
    assert iJson.Item.contentPath
    assert iJson.Item.contentIndex

    assert iJson.Item.startDate
    assert iJson.Item.owner
    assert iJson.Item.creationDate

    if( strict ){
      assert iJson.Item.meta: "no meta element present"
      assert iJson.Item.meta.name
      assert iJson.Item.meta.description: "no meta.description element present"
    } else{
      /* I reckon the fix for these is to populate some sensibe defaults on the
      way in to the schema,probably jsut  whatever the UI defaults are. */
      //   about 2 rows that don't have a meta  
      if( iJson.Item.meta ){
        assert iJson.Item.meta
        assert iJson.Item.meta.name

        // there were about 15 rows that don't have a description
        if( iJson.Item.meta.description ){
          assert iJson.Item.meta.description
        }
      }
    }
  }

  static void guardS3ExportAssocTableItem(
    Object iJson,
    boolean strict = true
  ) {
    assert iJson.Item : "no item value"
    assert iJson.Item.handle : "no handle value"
    assert iJson.Item.type : "no type value"
    assert iJson.Item.name : "no name value"
    assert iJson.Item.raidName : "no raidName value"
    assert iJson.Item.startDate : "no startDate value"

    // I think this isn't "bad data", it's an "institution" owned raid?
//    if( strict ){
//      assert iJson.Item.role : "no role value"
//    }
  }

  static void guardS3ExportMetadataTableItem(
    Object iJson,
    boolean strict = true
  ) {
    assert iJson.Item : "no item value"
    assert iJson.Item.type
    assert iJson.Item.name

//    assert iJson.Item.adminContactEmailAddress
//    assert iJson.Item.technicalContactEmailAddress
//    assert iJson.Item.grid
//    assert iJson.Item.isni
  }
  
  static void guardS3ExportTokenTableItem(
    Object iJson,
    boolean strict = true
  ) {
    assert iJson.Item : "no item value"
    assert iJson.Item.name
    assert iJson.Item.environment
    assert iJson.Item.dateCreated
    assert iJson.Item.token
  }
  
}

/*
metadata table
{
  Item: {
    adminContactEmailAddress: "quinton.newell@nihr.ac.uk",
    grid: "grid.451056.3",
    isni: "0000000121163923 ",
    name: "National Institute for Health Research ",
    technicalContactEmailAddress: "quinton.newell@nihr.ac.uk",
    type: "institution"
  }
}

associ-index-table
{
  "Item": {
    "handle": {
      "S": "102.100.100/70557"
    },
    "handle-name": {
      "S": "102.100.100/70557-RDM@UQ"
    },
    "handle-type": {
      "S": "102.100.100/70557-service"
    },
    "name": {
      "S": "RDM@UQ"
    },
    "name-role": {
      "S": "RDM@UQ-owner"
    },
    "raidName": {
      "S": "UQRDM Project"
    },
    "role": {
      "S": "owner"
    },
    "startDate": {
      "S": "2016-02-26 00:00:00"
    },
    "type": {
      "S": "service"
    }
  }
}

raid-table
{
  "Item": {
    "handle": {
      "S": "102.100.100/432142"
    },
    "contentPath": {
      "S": "https://rdm.uq.edu.au/pid/a01feb6a-b665-5843-9202-958bbfd316de"
    },
    "contentIndex": {
      "S": "1"
    },
    "meta": {
      "M": {
        "name": {
          "S": "UQRDM Project"
        },
        "description": {
          "S": "RAiD created by 'RDM@UQ' at '2021-11-29 17:01:07'"
        }
      }
    },
    "startDate": {
      "S": "2021-11-29 00:00:00"
    },
    "owner": {
      "S": "RDM@UQ"
    },
    "creationDate": {
      "S": "2021-11-29 17:01:07"
    }
  }
}


 */

