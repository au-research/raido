/* eslint-disable @typescript-eslint/no-explicit-any */
import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import TitlesForm from "./TitlesForm";

// Mock the dependencies
vi.mock("@/entities/title/TitleDetailsForm", () => ({
  TitleDetailsForm: () => <div data-testid="mock-title-details-form" />,
}));

vi.mock("@/entities/title/title-generator", () => ({
  titleGenerator: () => ({ id: "mock-id" }),
}));

// Mock react-hook-form
vi.mock("react-hook-form", () => ({
  useFieldArray: () => ({
    fields: [],
    append: vi.fn(),
    remove: vi.fn(),
  }),
  useForm: () => ({
    control: {},
    formState: { errors: {} },
    trigger: vi.fn(),
  }),
}));

describe("TitlesForm", () => {
  let mockProps: {
    control: any;
    errors: any;
    trigger: any;
  };

  beforeEach(() => {
    mockProps = {
      control: {},
      errors: {},
      trigger: vi.fn(),
    };
  });

  it("renders without crashing", () => {
    render(<TitlesForm {...mockProps} />);
    expect(screen.getByText("Titles")).toBeInTheDocument();
  });

  it('displays "No titles defined" when there are no titles', () => {
    render(<TitlesForm {...mockProps} />);
    expect(screen.getByText("No titles defined")).toBeInTheDocument();
  });

  it("displays an error message when there are title errors", () => {
    const errorMessage = "Title error";
    mockProps.errors = { title: { message: errorMessage } };
    render(<TitlesForm {...mockProps} />);
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  it("renders add title button", () => {
    render(<TitlesForm {...mockProps} />);
    expect(screen.getByLabelText("Add Title")).toBeInTheDocument();
  });

  it("applies error styling when there are errors", () => {
    mockProps.errors = { title: { message: "Error" } };
    const { container } = render(<TitlesForm {...mockProps} />);
    const card = container.firstChild as HTMLElement;
    expect(card).toHaveStyle("border-left-width: 3px");
    expect(card).not.toHaveStyle("border-left-color: #008ccf");
  });
});
