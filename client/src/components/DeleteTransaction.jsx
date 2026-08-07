import { TRANSACTION_URLS } from "@/constants/URLs/backendServices";
import { useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { Trash2 } from "lucide-react";
import React, { useState } from "react";

const DeleteTransaction = ({ id, type }) => {
  const [error, setError] = useState(null);

  const queryClient = useQueryClient();
  const handleDeleteTransaction = async () => {
    try {
      const URL =
        type.toLowerCase() === "expense"
          ? TRANSACTION_URLS.expense
          : TRANSACTION_URLS.income + `/${id}`;
      const res = await axios.delete(URL, {
        withCredentials: true,
      });

      if (res.status === 200) {
        queryClient.invalidateQueries([type.toLowerCase()]);
        setError(null);
      } else {
        setError("Something went wrong.");
      }
    } catch (error) {
      setError("Server error, please try again.");
    }
  };

  return (
    <div
      className="rounded-full p-2 border border-gray-300 bg-white hover:bg-red-100 cursor-pointer"
      onClick={handleDeleteTransaction}
    >
      <Trash2 stroke="red" size={20} />
    </div>
  );
};

export default DeleteTransaction;
