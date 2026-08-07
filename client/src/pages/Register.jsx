import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import axios from "axios";
import React, { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff } from "lucide-react";
import currencyData from "../constants/currencyData.json";
import { AUTH_URLS } from "@/constants/URLs/backendServices";

const Register = () => {
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const toggleShowPassword = () => setShowPassword((prev) => !prev);

  const passwordSchema = z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/\d/, "Password must contain at least one number")
    .regex(/[@$!%*?&]/, "Password must contain at least one special character")
    .regex(/^[A-Za-z\d@$!%*?&]+$/, "Password contains invalid characters");

  const registerSchema = z
    .object({
      firstName: z.string().min(1, "First name is required"),
      lastName: z.string().min(1, "Last name is required"),
      username: z.string().min(3, "Username must be at least 3 characters"),
      email: z
        .string()
        .min(1, { message: "Email is required" })
        .regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, {
          message: "Please enter a valid email address",
        }),
      password: passwordSchema,
      confirmPassword: z.string().min(8, "Please confirm your password"),
      currency: z.string().min(1, "Currency is required"),
    })
    .refine((data) => data.password === data.confirmPassword, {
      path: ["confirmPassword"],
      message: "Passwords do not match",
    });

  const {
    register,
    handleSubmit,
    control,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data) => {
    try {
      const res = await axios.post(AUTH_URLS.register, data);

      if (res.status === 201) {
        navigate("/login");
      } else {
        setError("root", {
          message: res.data?.message || "Something went wrong",
        });
      }
    } catch (err) {
      setError("root", {
        message:
          err.response?.data?.message || "Server error, please try again",
      });
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      name="loginContainer"
      className="flex flex-col gap-4 w-[80%]"
    >
      <div name="greetings" className="mb-4">
        <h1 className="text-3xl font-bold">Create an Account</h1>
        <p className="text-sm">Join us today by entering your details below</p>
      </div>
      <div name="nameField" className="sm:flex sm:gap-6">
        <div className="sm:flex-1 mb-4 sm:mb-0">
          <Label htmlFor="firstName" className={"mb-2"}>
            First Name
          </Label>
          <Input
            type="text"
            id="firstName"
            placeholder="John"
            {...register("firstName")}
          />
          {errors.firstName && (
            <p className="text-red-500 text-sm">{errors.firstName.message}</p>
          )}
        </div>
        <div className="sm:flex-1">
          <Label htmlFor="lastName" className={"mb-2"}>
            Last Name
          </Label>
          <Input
            type="text"
            id="lastName"
            placeholder="Doe"
            {...register("lastName")}
          />
          {errors.lastName && (
            <p className="text-red-500 text-sm">{errors.lastName.message}</p>
          )}
        </div>
      </div>
      <div name="usernameField" className="">
        <Label htmlFor="username" className={"mb-2"}>
          Username
        </Label>
        <Input
          type="text"
          id="username"
          placeholder="john123"
          {...register("username")}
        />
        {errors.username && (
          <p className="text-red-500 text-sm">{errors.username.message}</p>
        )}
      </div>
      <div name="emailField" className="">
        <Label htmlFor="email" className={"mb-2"}>
          Email
        </Label>
        <Input
          type="text"
          id="email"
          placeholder="john@gmail.com"
          {...register("email")}
        />
        {errors.email && (
          <p className="text-red-500 text-sm">{errors.email.message}</p>
        )}
      </div>
      <div name="passwordField" className="">
        <Label htmlFor="password" className={"mb-2"}>
          Password
        </Label>
        <div className="relative">
          <Input
            type={showPassword ? "text" : "password"}
            id="password"
            placeholder="Min 8 characters"
            className="pr-10"
            {...register("password")}
          />
          {showPassword ? (
            <Eye
              className="absolute top-1/2 right-3 -translate-y-1/2 cursor-pointer"
              onClick={toggleShowPassword}
            />
          ) : (
            <EyeOff
              className="absolute top-1/2 right-3 -translate-y-1/2 cursor-pointer"
              onClick={toggleShowPassword}
            />
          )}
        </div>
        {errors.password && (
          <p className="text-red-500 text-sm">{errors.password.message}</p>
        )}
      </div>
      <div name="confirmPasswordField" className="">
        <Label htmlFor="confirmPassword" className={"mb-2"}>
          Confirm Password
        </Label>
        <div className="relative">
          <Input
            type={showPassword ? "text" : "password"}
            id="confirmPassword"
            placeholder="Min 8 characters"
            className="pr-10"
            {...register("confirmPassword")}
          />
          {showPassword ? (
            <Eye
              className="absolute top-1/2 right-3 -translate-y-1/2 cursor-pointer"
              onClick={toggleShowPassword}
            />
          ) : (
            <EyeOff
              className="absolute top-1/2 right-3 -translate-y-1/2 cursor-pointer"
              onClick={toggleShowPassword}
            />
          )}
        </div>
        {errors.confirmPassword && (
          <p className="text-red-500 text-sm">
            {errors.confirmPassword.message}
          </p>
        )}
      </div>
      <div name="currencyField" className="">
        <Label htmlFor="currency" className={"mb-2"}>
          Currency
        </Label>
        <Controller
          name="currency"
          control={control}
          defaultValue="INR"
          render={({ field }) => (
            <Select onValueChange={field.onChange} value={field.value}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Select currency" />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Currency</SelectLabel>
                  {currencyData.map((currency) => (
                    <SelectItem key={currency.code} value={currency.code}>
                      {currency.name}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
          )}
        />

        {errors.currency && (
          <p className="text-red-500 text-sm">{errors.currency.message}</p>
        )}
      </div>
      <Button disabled={isSubmitting} type="submit">
        Sign Up
      </Button>
      {errors.root && (
        <p className="text-red-500 text-sm">{errors.root.message}</p>
      )}
      <p>
        Already have an account?{" "}
        <Link to="/login" className="underline">
          Login
        </Link>
      </p>
    </form>
  );
};

export default Register;
