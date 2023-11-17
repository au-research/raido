const fetch = (...args) =>
  import("node-fetch").then(({ default: fetch }) => fetch(...args));

const fs = require("fs");

// Define an asynchronous function to download an image
async function downloadImage(url, filePath) {
  try {
    const response = await fetch(url);

    if (!response.ok)
      throw new Error(`Unexpected response ${response.statusText}`);

    const writer = fs.createWriteStream(filePath);
    response.body.pipe(writer);

    return new Promise((resolve, reject) => {
      writer.on("finish", resolve);
      writer.on("error", (error) => {
        writer.close();
        reject(error);
      });
    });
  } catch (error) {
    console.error("Error downloading the image:", error);
    throw error; // Rethrow the error for the caller to handle
  }
}

const dataRaw = fs.readFileSync("./data/raids.json", "utf-8");
const raids = JSON.parse(dataRaw);

const media = new Map();
for (const raid of raids) {
  if (raid.relatedObject && raid.relatedObject.length > 0) {
    for (const ro of raid.relatedObject) {
      const mediaType = ro.type.id.split("/").pop().replace(".json", "");
      if (media.has(mediaType)) {
        const count = media.get(mediaType) || 0;
        media.set(mediaType, count + 1);
      } else {
        media.set(mediaType, 1);
      }
    }
  }
}

const years = new Map();
for (const raid of raids) {
  const year = raid.date.startDate.split("-")[0];
  if (year.length === 4) {
    if (years.has(year)) {
      const count = years.get(year) || 0;
      years.set(year, count + 1);
    } else {
      years.set(year, 1);
    }
  }
}

// sort map by key
const sortedYears = new Map(
  [...years.entries()].sort((a, b) => parseInt(a[0]) - parseInt(b[0]))
);

const sortedMedia = new Map([...media.entries()].sort((a, b) => b[1] - a[1]));

const yearsKeys = `%27${[...sortedYears.keys()].join("%27,%27")}%27`;
const yearsData = [...sortedYears.values()].join(",");

const mediaKeys = `%27${[...sortedMedia.keys()].join("%27,%27")}%27`;
const mediaData = [...sortedMedia.values()].join(",");

const yearsImageUrl = `https://quickchart.io/chart?c={type:%27pie%27,data:{labels:[${yearsKeys}], datasets:[{label:%27RAiD start years%27,data:[${yearsData}]}]}}`;
const yearsSavePath = "./public/years-chart.jpg";
downloadImage(yearsImageUrl, yearsSavePath)
  .then(() => console.log("Image downloaded successfully"))
  .catch((error) => console.error("Failed to download image", error));

const mediaImageUrl = `https://quickchart.io/chart?c={type:%27bar%27,data:{labels:[${mediaKeys}], datasets:[{label:%27Related media%27,data:[${mediaData}]}]}}`;
const mediaSavePath = "./public/media-chart.jpg";
downloadImage(mediaImageUrl, mediaSavePath)
  .then(() => console.log("Image downloaded successfully"))
  .catch((error) => console.error("Failed to download image", error));
