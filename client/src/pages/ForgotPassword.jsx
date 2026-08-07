import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { AUTH_URLS } from "@/constants/URLs/backendServices";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import z from "zod";

const ForgotPassword = () => {
  const [isMailSent, setIsMailSent] = useState(false);

  const loginSchema = z.object({
    email: z
      .string()
      .min(1, { message: "Email is required" })
      .regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, {
        message: "Please enter a valid email address",
      }),
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
    try {
      const res = await axios.post(AUTH_URLS.forgotPassword, data);

      if (res.status === 200) {
        setIsMailSent(true);
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
      name="forgotPasswordForm"
      className="flex flex-col gap-6 w-[50%]"
    >
      <div name="greetings" className="">
        <h1 className="text-3xl font-bold">Forgot Password?</h1>
        <p className="text-sm">Please enter your email</p>
      </div>
      <div name="emailField" className="">
        <Label htmlFor="email" className={"mb-2"}>
          Email
        </Label>
        <Input
          type="text"
          {...register("email")}
          id="email"
          placeholder="john@gmail.com"
        />
        {errors.email && (
          <p className="text-red-500 text-sm">{errors.email.message}</p>
        )}
      </div>
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting ? "Loading..." : "Submit"}
      </Button>
      {errors.root && (
        <p className="text-red-500 text-sm">{errors.root.message}</p>
      )}
      {/* TODO: Do this using Modals */}
      {!isSubmitting && isMailSent && (
        <p className="text-green-500">
          A reset link has been sent to your email
        </p>
      )}
    </form>
  );
};

export default ForgotPassword;
