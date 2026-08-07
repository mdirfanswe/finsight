import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { AUTH_URLS } from "@/constants/URLs/backendServices";
import { useAuth } from "@/hooks/useAuth";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Eye, EyeOff } from "lucide-react";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import z from "zod";

const Login = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const toggleShowPassword = () => setShowPassword((prev) => !prev);

  const { setToken } = useAuth();

  const passwordSchema = z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/\d/, "Password must contain at least one number")
    .regex(/[@$!%*?&]/, "Password must contain at least one special character")
    .regex(/^[A-Za-z\d@$!%*?&]+$/, "Password contains invalid characters");

  const loginSchema = z.object({
    username: z.string().min(3, "Username must be at least 3 characters"),
    password: passwordSchema,
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
      const res = await axios.post(AUTH_URLS.login, data);

      const jwt = res.data;
      if (res.status === 200) {
        setToken(jwt);
        navigate("/dashboard");
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
      name="loginContainer"
      className="flex flex-col gap-6 w-[80%] sm:w-[50%]"
    >
      <div name="greetings" className="">
        <h1 className="text-3xl font-bold">Welcome Back</h1>
        <p className="text-sm">Please Enter your details to login</p>
      </div>
      <div name="usernameField" className="">
        <Label htmlFor="username" className={"mb-2"}>
          Username
        </Label>
        <Input
          type="text"
          {...register("username")}
          id="username"
          placeholder="john123"
        />
        {errors.username && (
          <p className="text-red-500 text-sm">{errors.username.message}</p>
        )}
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
            placeholder="Min 8 characters"
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
        <div className="flex justify-end">
          <Link to="/forgot-password" className="underline">
            Forgot Password?
          </Link>
        </div>
      </div>
      <Button type="submit" disabled={isSubmitting}>
        Login
      </Button>
      <p>
        Don't have an account?{" "}
        <Link to="/register" className="underline">
          Sign Up
        </Link>
      </p>
      {errors.root && (
        <p className="text-red-500 text-sm">{errors.root.message}</p>
      )}
    </form>
  );
};

export default Login;
