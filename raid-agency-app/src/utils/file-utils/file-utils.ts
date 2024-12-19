import { SnackbarContextInterface } from "@/components/snackbar";

export const downloadJson = ({
  data,
  filename = "data.json",
  snackbar,
  prefix,
  suffix,
  labelPlural,
}: {
  data: any;
  filename?: string;
  snackbar: SnackbarContextInterface;
  prefix?: string;
  suffix?: string;
  labelPlural?: string;
}) => {
  // Create a blob with the JSON data
  const blob = new Blob([JSON.stringify(data, null, 2)], {
    type: "application/json",
  });

  // Create a URL for the blob
  const url = window.URL.createObjectURL(blob);

  // Create a temporary anchor element
  const link = document.createElement("a");
  link.href = url;
  link.download = `${prefix}-${suffix}-${labelPlural?.toLowerCase()}-${filename}`;

  // Programmatically click the link to trigger download
  document.body.appendChild(link);
  link.click();

  // Clean up
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);

  snackbar.openSnackbar(`✅ Downloaded JSON file`);
};
