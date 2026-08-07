import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { AUTH_URLS } from "@/constants/URLs/backendServices";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Eye, EyeOff } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useLocation, useNavigate } from "react-router-dom";
import z from "zod";

const ResetPassword = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const toggleShowPassword = () => setShowPassword((prev) => !prev);

  const passwordSchema = z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/\d/, "Password must contain at least one number")
    .regex(/[@$!%*?&]/, "Password must contain at least one special character")
    .regex(/^[A-Za-z\d@$!%*?&]+$/, "Password contains invalid characters");

  const loginSchema = z
    .object({
      password: passwordSchema,
      confirmPassword: z.string().min(8, "Please confirm your password"),
    })
    .refine((data) => data.password === data.confirmPassword, {
      path: ["confirmPassword"],
      message: "Passwords do not match",
    });

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data) => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    const dataWithToken = { ...data, token };

    try {
      const res = await axios.post(AUTH_URLS.resetPassword, dataWithToken, {
        withCredentials: true,
      });

      if (res.status === 200) {
        navigate("/login?message=Password reset successfully");
      } else {
        setError("root", {
          message: res.data?.message || "Something went wrong",
        });
      }
    } catch (error) {
      setError("root", {
        message:
          error.response?.data?.message || "Server error, please try again",
      });
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      name="resetPasswordContainer"
      className="flex flex-col gap-6 w-[50%]"
    >
      <div name="greetings" className="">
        <h1 className="text-3xl font-bold">Reset Password</h1>
        <p className="text-sm">Please enter your new password</p>
      </div>
      <div name="passwordField" className="">
        <Label htmlFor="password" className={"mb-2"}>
          Password
        </Label>
        <div className="relative">
          <Input
            type={showPassword ? "text" : "password"}
            {...register("password")}
            id="password"
            className="pr-10"
            placeholder="New Password"
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
          Password
        </Label>
        <div className="relative">
          <Input
            type={showPassword ? "text" : "password"}
            {...register("confirmPassword")}
            id="confirmPassword"
            className="pr-10"
            placeholder="Confirm Password"
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
      <Button type="submit" disabled={isSubmitting}>
        Submit
      </Button>
      {errors.root && (
        <p className="text-red-500 text-sm">{errors.root.message}</p>
      )}
    </form>
  );
};

export default ResetPassword;
