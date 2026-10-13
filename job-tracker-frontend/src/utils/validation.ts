import { z } from "zod";

export const signupSchema = z.object({
  name: z.string().min(2, "Name is required"),
  email: z.string().email("Enter a valid email"),
  password: z.string().min(6, "Password must contain at least 6 characters"),
  phone: z.string().optional()
});

export const loginSchema = z.object({
  email: z.string().email("Enter a valid email"),
  password: z.string().min(1, "Password is required")
});

export const applicationSchema = z.object({
  companyName: z.string().min(1, "Company is required"),
  jobTitle: z.string().min(1, "Job title is required"),
  jobUrl: z.string().optional(),
  location: z.string().optional(),
  salary: z.string().optional(),
  applicationDate: z.string().min(1, "Date is required"),
  status: z.string().min(1),
  notes: z.string().optional()
});
