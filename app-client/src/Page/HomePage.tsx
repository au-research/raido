import { NavTransition } from "Design/NavigationProvider";
import React, { SyntheticEvent } from "react";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import { LargeContentMain, SmallContentMain } from "Design/LayoutMain";
import { AuthState, useAuth } from "Auth/AuthProvider";
import { isNoneRole } from "Auth/Role";
import {
  Autocomplete,
  FormControl,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import { PrimaryButton } from "Component/AppButton";
import { HelpPopover } from "Component/HelpPopover";
import { raidoTitle } from "Component/Util";

const log = console;

const pageUrl = "/home";

export function getHomePageLink(): string{
  return pageUrl;
}

export function isHomePagePath(path: String): boolean{
  const normalizedPath = path.toLowerCase();
  return normalizedPath.startsWith(pageUrl) ||
    // use this page as the "default" or "home" page for the app  
    path === "/";
}

export function HomePage(){
  return <NavTransition isPath={isHomePagePath} title={raidoTitle("Home")}>
    <Content/>
  </NavTransition>
}


function Content(){
  const auth: AuthState = useAuth();
  if( isNoneRole(auth) ){
    return <NoRoleContent/>
  }

  return <SmallContentMain>
    <ContainerCard title={"Home"}>
      <TextSpan>This will be the home page of the App.</TextSpan>
    </ContainerCard>
  </SmallContentMain>
}

interface InstData {
  label: string,
  id: number
}

function NoRoleContent(){
  const [institution, setInstitution] = React.useState(
    null as InstData | null);

  const handleChange = (event: SyntheticEvent, value: InstData | null) => {
    setInstitution(value);
  };

  /* LargeContent so margins are removed on mobile
  maxWidth because the form looks ugly if let it spread really wide. */
  return <LargeContentMain style={{maxWidth: "30em"}}>
    <ContainerCard title={"Request RAiD Authorisation"}
      // minHeight so the autocomplete drop box has lots of space
      contentStyle={{minHeight: "70vh"}}
      action={<AuthRequestHelp/>}
    >
      <Typography paragraph>
        You have not been authorised to use the application.
      </Typography>
      <Typography paragraph>
        Please request permission from your institution, select below.
      </Typography>

      <form onSubmit={(e) => {
        e.preventDefault();
        alert("Auth request not yet implemented");
      }}>
        <Stack spacing={2}>
          <FormControl fullWidth focused>
            <Autocomplete id="inst-select"
              selectOnFocus clearOnEscape disablePortal
              multiple={false}
              options={instData}
              value={institution}
              onChange={handleChange}
              renderInput={(params) =>
                <TextField {...params} required label="Institution"
                  autoFocus={true}/>
              }
            />
          </FormControl>
          <FormControl fullWidth>
            <TextField id="reqeust-text" label="Comments / Information"
              multiline rows={4} variant="outlined"/>
          </FormControl>
          <PrimaryButton disabled={!institution} type={"submit"} fullWidth
            style={{marginTop: "2em"}}
          >
            Submit request
          </PrimaryButton>
        </Stack>
      </form>
    </ContainerCard>
  </LargeContentMain>

}


function AuthRequestHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <TextSpan>
        After you've submitted your request, you will need to contact
        your institution administrator to let them know.
        (This process will be automated in the future).
      </TextSpan>
      <TextSpan>
        If you made a mistake when you submitted your request (wrong
        institution or you just want change/add the comment) - re-submit
        and your request will be updated.
      </TextSpan>
    </Stack>
  }/>;
}

// select row_number() over (order by name) as "id", name "label" from raid_v1_import.metadata
const instData: InstData[] =
  [
    {
      "id": 1,
      "label": "American Geophysical Union "
    },
    {
      "id": 2,
      "label": "Analysis and Policy Observatory "
    },
    {
      "id": 3,
      "label": "Atlas of Living Australia"
    },
    {
      "id": 4,
      "label": "Auckland University Centre for eResearch "
    },
    {
      "id": 5,
      "label": "AURIN"
    },
    {
      "id": 6,
      "label": "AuScope"
    },
    {
      "id": 7,
      "label": "Australian National University "
    },
    {
      "id": 8,
      "label": "Australian Plant Phenomics Facility"
    },
    {
      "id": 9,
      "label": "Australian Research Council "
    },
    {
      "id": 10,
      "label": "Australian Research Data Commons"
    },
    {
      "id": 11,
      "label": "Australian Urban Research Infrastructure network"
    },
    {
      "id": 12,
      "label": "Barcelona Supercomputing Center"
    },
    {
      "id": 13,
      "label": "Bioplatforms Australia"
    },
    {
      "id": 14,
      "label": "Brain Cancer Biobanking Australia"
    },
    {
      "id": 15,
      "label": "Cancer Council Queensland "
    },
    {
      "id": 16,
      "label": "CSIRO"
    },
    {
      "id": 17,
      "label": "DataCite"
    },
    {
      "id": 18,
      "label": "Data Life Cycle Framework "
    },
    {
      "id": 19,
      "label": "Data Stewardship Wizard"
    },
    {
      "id": 20,
      "label": "Department of Agriculture, Water and the Environment"
    },
    {
      "id": 21,
      "label": "Digital health CRC"
    },
    {
      "id": 22,
      "label": "European Molecular Biology Laboratory"
    },
    {
      "id": 23,
      "label": "European Plate Observing System"
    },
    {
      "id": 24,
      "label": "Federation University "
    },
    {
      "id": 25,
      "label": "Federation University Australia"
    },
    {
      "id": 26,
      "label": "FigShareFest Hack"
    },
    {
      "id": 27,
      "label": "Garvan Institute of Medical Research "
    },
    {
      "id": 28,
      "label": "Geological Survey of New South Wales"
    },
    {
      "id": 29,
      "label": "Geoscience Australia "
    },
    {
      "id": 30,
      "label": "Griffith University "
    },
    {
      "id": 31,
      "label": "Helmholtz-Zentrum Dresden-Rossendorf"
    },
    {
      "id": 32,
      "label": "Helmholtz-Zentrum Dresden-Rossendorf Library "
    },
    {
      "id": 33,
      "label": "Howard Hughes Medical Insrtitute"
    },
    {
      "id": 34,
      "label": "Incorporated Research Institutions for Seismology (IRIS)"
    },
    {
      "id": 35,
      "label": "Integrated Marine Observing System (IMOS)"
    },
    {
      "id": 36,
      "label": "InterGrain Pty Ltd"
    },
    {
      "id": 37,
      "label": "Janelia Research Campus "
    },
    {
      "id": 38,
      "label": "Japan National Institute of Informatics "
    },
    {
      "id": 39,
      "label": "Joint Information Systems Committee (JISC)"
    },
    {
      "id": 40,
      "label": "Macquarie University (MQU)"
    },
    {
      "id": 41,
      "label": "Microscopy Australia (MA)"
    },
    {
      "id": 42,
      "label": "Monash University "
    },
    {
      "id": 43,
      "label": "More Brains Cooperative"
    },
    {
      "id": 44,
      "label": "National Computational Infrastructure (NCI)"
    },
    {
      "id": 45,
      "label": "National Institute for Health Research "
    },
    {
      "id": 46,
      "label": "National Institute of Health Research "
    },
    {
      "id": 47,
      "label": "Nectar Cloud"
    },
    {
      "id": 48,
      "label": "NIF@UQ"
    },
    {
      "id": 49,
      "label": "NII Research Centre for Open Science and Data Platform"
    },
    {
      "id": 50,
      "label": "NSW Department of Planning, Industry and Environment"
    },
    {
      "id": 51,
      "label": "ORCiD"
    },
    {
      "id": 52,
      "label": "Phenomics Australia "
    },
    {
      "id": 53,
      "label": "Population Health Research Network (PHRN)"
    },
    {
      "id": 54,
      "label": "Queensland University of Technology "
    },
    {
      "id": 55,
      "label": "RDM@UQ"
    },
    {
      "id": 56,
      "label": "Redbox@QCIF"
    },
    {
      "id": 57,
      "label": "ROYAL MELBOURNE INSTITUTE OF TECHNOLOGY UNIVERSITY, RMIT"
    },
    {
      "id": 58,
      "label": "Sax Institute"
    },
    {
      "id": 59,
      "label": "South Australian Research and Development Institute"
    },
    {
      "id": 60,
      "label": "South Western Sydney Local Health District"
    },
    {
      "id": 61,
      "label": "SURF"
    },
    {
      "id": 62,
      "label": "Swinburne University of Technology"
    },
    {
      "id": 63,
      "label": "Sydney Institute of Marine Science"
    },
    {
      "id": 64,
      "label": "TechConnect"
    },
    {
      "id": 65,
      "label": "Telethon Kids Institute"
    },
    {
      "id": 66,
      "label": "Terrestrial Ecosystem Research Network (TERN)"
    },
    {
      "id": 67,
      "label": "The Great Eastern Ranges"
    },
    {
      "id": 68,
      "label": "The Multi-modal Australian ScienceS Imaging and Visualisation Environment (MASSIVE)"
    },
    {
      "id": 69,
      "label": "Thermo Fisher Scientific "
    },
    {
      "id": 70,
      "label": "The University of Melbourne"
    },
    {
      "id": 71,
      "label": "The University of Notre Dame Australia"
    },
    {
      "id": 72,
      "label": "The University of Sydney"
    },
    {
      "id": 73,
      "label": "Threatened Species Recovery Hub (NESP)"
    },
    {
      "id": 74,
      "label": "UniAucklandHack"
    },
    {
      "id": 75,
      "label": "University of Melbourne"
    },
    {
      "id": 76,
      "label": "University of Melbourne "
    },
    {
      "id": 77,
      "label": "University of Newcastle Australia"
    },
    {
      "id": 78,
      "label": "University of New South Wales"
    },
    {
      "id": 79,
      "label": "University of Notre Dame Library"
    },
    {
      "id": 80,
      "label": "University of Queensland "
    },
    {
      "id": 81,
      "label": "University of Sydney's Research Dashboard "
    },
    {
      "id": 82,
      "label": "University of Sydney’s Research Dashboard"
    },
    {
      "id": 83,
      "label": "University of Tasmania"
    },
    {
      "id": 84,
      "label": "University of the Witwatersrand"
    },
    {
      "id": 85,
      "label": "University of the Witwatersrand Library "
    },
    {
      "id": 86,
      "label": "University of Western Australia"
    },
    {
      "id": 87,
      "label": "UQ Centre for Advanced Imaging "
    }
  ]
;

 