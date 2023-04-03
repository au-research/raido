import { InputLabel } from "@mui/material";
import React from "react";

export function InputLabelWithProblem({id, label, problem}: {
  id?: string,
  label: string,
  problem?: string
}){
  return <InputLabel id={id}>
    {labelWithProblem(label, problem)}
  </InputLabel>
}

export function labelWithProblem(label: string, problem?: string): string{
  return problem ? `${label} - ${problem}` : label;
}
