import ReactDOM from "react-dom/client";
import {
  createBrowserRouter,
  Navigate,
  RouterProvider,
} from "react-router-dom";
import { App } from "./App";
import "./index.css";
import {
  otherRoutes,
  raidPageRoutes,
  servicePointRoutes,
  testRoutes,
} from "./routes";
import "@fontsource/figtree";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <p>Error...</p>,
    children: [
      ...servicePointRoutes,
      ...raidPageRoutes,
      ...otherRoutes,
      ...testRoutes,
      {
        path: "*",
        element: <Navigate to="/" />,
      },
    ],
  },
]);

root.render(<RouterProvider router={router} />);
