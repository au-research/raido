import { RaidDto } from "@/generated/raid";
import getErrorCardStyles from "@/utils/style-utils/card-styles";
import { AddBox as AddBoxIcon } from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  Stack,
  Typography,
} from "@mui/material";
import { Fragment, memo, useCallback, useState } from "react";
import {
  Control,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { DynamicFormItem } from "@/components/dynamic-item-form";
import { ChildConfig } from "@/types";

export const DynamicForm = memo(
  ({
    control,
    errors,
    trigger,
    label,
    labelPlural,
    entityKey,
    DetailsFormComponent,
    generator,
    childConfigs,
  }: {
    control: Control<RaidDto>;
    errors?: FieldErrors<RaidDto>;
    trigger: UseFormTrigger<RaidDto>;
    label: string;
    labelPlural: string;
    entityKey: string;
    DetailsFormComponent: React.ComponentType<{ index: number }>;
    generator: () => any;
    childConfigs?: ChildConfig[];
  }) => {
    const NoItemsMessage = memo(({ type }: { type: string }) => (
      <Typography variant="body2" color="text.secondary" textAlign="center">
        No {type} defined
      </Typography>
    ));

    const { fields, append, remove } = useFieldArray({
      control,
      name: entityKey as any,
    });

    const handleAddItem = useCallback(() => {
      const newEntry = generator();
      append(newEntry);
      trigger(entityKey as keyof RaidDto);
    }, [append, trigger, entityKey, generator]);

    const [isRowHighlighted, setIsRowHighlighted] = useState(false);

    return (
      <Card
        sx={getErrorCardStyles(errors && errors[entityKey as keyof RaidDto])}
      >
        <CardHeader title={labelPlural} />
        <CardContent>
          <Box>
            {fields.length === 0 && <NoItemsMessage type={labelPlural} />}
          </Box>
          <Stack
            gap={2}
            divider={<Divider />}
            className={isRowHighlighted ? "add" : ""}
          >
            {fields.map((field, parentIndex) => (
              <Fragment key={field.id}>
                <DynamicFormItem
                  index={parentIndex}
                  onRemove={remove}
                  label={label}
                >
                  <DetailsFormComponent index={parentIndex} />

                  {childConfigs &&
                    childConfigs.map((childConfig) => (
                      <ChildSection
                        key={childConfig.fieldKey}
                        control={control}
                        errors={errors || {}}
                        trigger={trigger}
                        parentIndex={parentIndex}
                        parentKey={entityKey}
                        {...childConfig}
                      />
                    ))}
                </DynamicFormItem>
              </Fragment>
            ))}
          </Stack>
        </CardContent>
        <CardActions>
          <Button
            variant="outlined"
            color="success"
            size="small"
            startIcon={<AddBoxIcon />}
            sx={{ textTransform: "none" }}
            onClick={handleAddItem}
            onMouseEnter={() => setIsRowHighlighted(true)}
            onMouseLeave={() => setIsRowHighlighted(false)}
          >
            Add {label}
          </Button>
        </CardActions>
      </Card>
    );
  }
);

const ChildSection = memo(
  ({
    control,
    errors,
    trigger,
    parentIndex,
    parentKey,
    fieldKey,
    label,
    labelPlural,
    DetailsComponent,
    generator,
  }: {
    control: Control<RaidDto>;
    errors: FieldErrors<RaidDto>;
    trigger: UseFormTrigger<RaidDto>;
    parentIndex: number;
    parentKey: string;
    fieldKey: string;
    label: string;
    labelPlural: string;
    DetailsComponent: React.ComponentType<{
      parentIndex: number;
      index: number;
    }>;
    generator: () => any;
  }) => {
    const fieldName = `${parentKey}.${parentIndex}.${fieldKey}` as const;

    const { fields, append, remove } = useFieldArray({
      control,
      name: fieldName as any,
    });

    const handleAddChild = useCallback(() => {
      const newEntry = generator();
      append(newEntry);
      trigger(fieldName as any);
      setIsRowHighlighted(false);
    }, [append, trigger, fieldName]);

    const [isRowHighlighted, setIsRowHighlighted] = useState(false);

    return (
      <Box sx={{ ml: 4, mt: 2 }}>
        <Typography variant="body1" gutterBottom>
          {labelPlural}
        </Typography>
        {fields.length === 0 ? (
          <Typography variant="body1" color="text.secondary">
            No {labelPlural.toLowerCase()} defined
          </Typography>
        ) : (
          <Stack
            gap={2}
            divider={<Divider />}
            className={isRowHighlighted ? "add" : ""}
          >
            {fields.map((field, index) => (
              <DynamicFormItem
                key={field.id}
                index={index}
                onRemove={remove}
                label={label}
              >
                <DetailsComponent parentIndex={parentIndex} index={index} />
              </DynamicFormItem>
            ))}
          </Stack>
        )}
        <Button
          variant="outlined"
          color="success"
          size="small"
          startIcon={<AddBoxIcon />}
          sx={{ textTransform: "none", mt: 3 }}
          onClick={handleAddChild}
          onMouseEnter={() => setIsRowHighlighted(true)}
          onMouseLeave={() => setIsRowHighlighted(false)}
        >
          Add {label}
        </Button>
      </Box>
    );
  }
);

DynamicForm.displayName = "OneLevelFormComponent";
ChildSection.displayName = "ChildSection";
