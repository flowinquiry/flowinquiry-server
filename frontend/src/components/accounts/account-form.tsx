"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import React from "react";
import { useFormState } from "react-dom";
import { useForm } from "react-hook-form";

import AccountIndustriesSelect from "@/components/accounts/account-industries-select";
import AccountTypesSelect from "@/components/accounts/account-types-select";
import { Heading } from "@/components/heading";
import {
  ExtInputField,
  FormProps,
  SubmitButton,
} from "@/components/ui/ext-form";
import ValuesSelect from "@/components/ui/ext-select-values";
import { Form } from "@/components/ui/form";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/components/ui/use-toast";
import { saveOrUpdateAccount } from "@/lib/actions/accounts.action";
import { Account, AccountSchema, accountSchema } from "@/types/accounts";
import { ActionResult } from "@/types/commons";

export const AccountForm: React.FC<FormProps<Account>> = ({ initialData }) => {
  const { toast } = useToast();

  const defaultValues = initialData
    ? initialData
    : {
        accountName: "",
      };

  const form = useForm<AccountSchema>({
    resolver: zodResolver(accountSchema),
    defaultValues,
  });

  const saveAccountClientAction = async (
    prevState: ActionResult,
    formData: FormData,
  ) => {
    form.clearErrors();
    const validation = accountSchema.safeParse(
      Object.fromEntries(formData.entries()),
    );
    if (validation.error) {
      validation.error.issues.forEach((issue) => {
        form.setError(issue.path[0], { message: issue.message });
      });
      setTimeout(() => {
        toast({
          variant: "destructive",
          title: "Error",
          description:
            "Invalid values. Please fix them before submitting again",
        });
      }, 2000);
    }

    return await saveOrUpdateAccount(prevState, isEdit, formData);
  };

  const [formState, formAction] = useFormState(saveAccountClientAction, {
    status: "default",
  });

  const isEdit = !!initialData;
  const title = isEdit ? "Edit account" : "Create account";
  const description = isEdit ? "Edit account" : "Add a new account";
  const submitText = isEdit ? "Save changes" : "Create";
  const submitTextWhileLoading = isEdit ? "Saving changes ..." : "Creating ...";

  return (
    <>
      <div className="flex items-center justify-between">
        <Heading title={title} description={description} />
      </div>
      <Separator />
      <Form {...form}>
        <form className="space-y-6" action={formAction}>
          <ExtInputField
            form={form}
            required={true}
            fieldName="accountName"
            label="Name"
            placeholder="Account Name"
          />
          <AccountTypesSelect form={form} required={true} />
          <AccountIndustriesSelect form={form} required={true} />
          <ExtInputField
            form={form}
            fieldName="website"
            label="Website"
            placeholder="https://example.com"
          />
          <ExtInputField
            form={form}
            fieldName="phoneNumber"
            label="Phone"
            placeholder="Phone number"
          />
          <ValuesSelect
            form={form}
            fieldName="status"
            label="Status"
            placeholder="Select status"
            required={true}
            values={["Active", "Inactive"]}
          />
          <SubmitButton
            label={submitText}
            labelWhileLoading={submitTextWhileLoading}
          />
        </form>
      </Form>
    </>
  );
};

export default AccountForm;
