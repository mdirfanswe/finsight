import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { TRANSACTION_URLS } from "@/constants/URLs/backendServices";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import EmojiPicker from "emoji-picker-react";
import { Pencil, X } from "lucide-react";

import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const UpdateTransactionDialog = ({ item, type }) => {
  const [showDialog, setShowDialog] = useState(false);
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const [selectedEmoji, setSelectedEmoji] = useState(item.icon);

  const queryClient = useQueryClient();

  const schema = z.object({
    category: z.string().optional(),
    amount: z
      .number("Amount must be a number")
      .positive("Amount must be greater than 0"),
    date: z.string().min(1, "Date is required"),
  });

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(schema),
  });

  const onEmojiClick = (emojiData) => {
    setSelectedEmoji(emojiData.emoji);
    setShowEmojiPicker(false);
  };

  const onSubmit = async (data) => {
    // Add icon field to data
    if (selectedEmoji) {
      data.icon = selectedEmoji;
    }

    // Rename date to expenseDate or incomeDate
    if (type.toLowerCase() === "expense") {
      data.expenseDate = data.date;
    } else {
      data.incomeDate = data.date;
    }
    delete data.date;

    // Send request
    try {
      const URL =
        (type.toLowerCase() === "expense"
          ? TRANSACTION_URLS.expense
          : TRANSACTION_URLS.income) + `/${item.id}`;
      const res = await axios.put(URL, data, {
        withCredentials: true,
      });

      if (res.status === 200) {
        setShowDialog(false);
        queryClient.invalidateQueries([type.toLowerCase()]);
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
    <Dialog open={showDialog} onOpenChange={setShowDialog}>
      <DialogTrigger asChild>
        <div className="rounded-full p-2 sm:p-3 border border-gray-300 bg-white cursor-pointer hover:bg-gray-300">
          <Pencil size={16} />
        </div>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px] sm:max-h-[600px] overflow-auto">
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogHeader>
            <DialogTitle>Update {type}</DialogTitle>
          </DialogHeader>
          <div className="-mx-6 my-4 border-b" />

          <div className="grid gap-4">
            {/* {Icon} */}
            <div className="grid gap-3">
              <div
                className="flex gap-2 items-center cursor-pointer w-fit"
                onClick={() => setShowEmojiPicker(!showEmojiPicker)}
              >
                {selectedEmoji && (
                  <span className="text-2xl">{selectedEmoji}</span>
                )}
                <Label className="cursor-pointer text-xl">Change Icon</Label>
              </div>
              {showEmojiPicker && (
                <div className="relative">
                  <EmojiPicker onEmojiClick={onEmojiClick} />
                  <div
                    className="border border-gray-300 bg-white absolute z-50 right-0 -top-2 flex justify-center items-center rounded-full p-[4px] cursor-pointer"
                    onClick={() => setShowEmojiPicker(false)}
                  >
                    <X size={12} />
                  </div>
                </div>
              )}
            </div>

            {/* {Category} */}
            <div className="grid gap-3">
              <Label htmlFor="category">Category</Label>
              <Input
                id="category"
                placeholder={
                  type.toLowerCase() === "expense"
                    ? "Rent, Groceries, etc."
                    : "Salary, Bonus, etc."
                }
                defaultValue={item.category}
                {...register("category")}
              />
            </div>

            {/* {Amount} */}
            <div className="grid gap-3">
              <Label htmlFor="amount">Amount *</Label>
              <Input
                id="amount"
                type="number"
                step="0.01"
                defaultValue={item.amount}
                {...register("amount", { valueAsNumber: true })}
              />
              {errors.amount && (
                <p className="text-red-500 text-sm">{errors.amount.message}</p>
              )}
            </div>

            {/* {Date} */}
            <div className="grid gap-3">
              <Label htmlFor="transaction-date">Date *</Label>
              <Input
                id="transaction-date"
                type="date"
                defaultValue={
                  type.toLowerCase() === "expense"
                    ? item.expenseDate
                    : item.incomeDate
                }
                {...register("date")}
              />
              {errors.date && (
                <p className="text-red-500 text-sm">{errors.date.message}</p>
              )}
            </div>
          </div>
          <DialogFooter>
            <Button type="submit" className="mt-3">
              Update {type}
            </Button>
          </DialogFooter>
          {errors.root && (
            <p className="text-red-500 text-sm">{errors.root.message}</p>
          )}
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default UpdateTransactionDialog;
