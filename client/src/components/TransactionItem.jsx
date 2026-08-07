import { TrendingDown, TrendingUp } from "lucide-react";
import React from "react";
import UpdateTransactionDialog from "./UpdateTransactionDialog";
import DeleteTransaction from "./DeleteTransaction";

const TransactionItem = ({ item, type, showActions = true }) => {
  return (
    <div
      //   key={item.id}
      name="item"
      className="group relative flex justify-between items-center sm:px-4 py-2 hover:bg-gray-100 rounded-[4px]"
    >
      {/* Delete button */}
      {showActions && (
        <div name="deleteTransaction" className="absolute -top-2 -right-3 z-50">
          <DeleteTransaction id={item.id} type={type} />
        </div>
      )}
      {showActions && (
        <div
          name="updateTransaction"
          className="absolute sm:hidden -top-1 -left-3 z-50"
        >
          <UpdateTransactionDialog type={type} item={item} />
        </div>
      )}

      {/* Transaction details */}
      <div className="flex items-center gap-4">
        <div
          className="rounded-full p-3 text-2xl bg-[oklch(0.94_0.01_261.49)]"
          name="icon"
        >
          {item.icon}
        </div>
        <div className="">
          <p name="typeOfTransaction">{item.category}</p>
          <p name="dateOfTransation" className="text-gray-400 text-sm">
            {new Date(
              type.toLowerCase() === "expense"
                ? item.expenseDate
                : item.incomeDate
            ).toLocaleDateString("en-GB")}
          </p>
          <p
            name="typeOfTransaction"
            className={`xs375:hidden ${
              type.toLowerCase() === "expense"
                ? "text-[#f32626]"
                : "text-[#00be00]"
            } font-bold`}
          >
            {type.toLowerCase() === "income" ? "+" : "-"} {item.amount}
          </p>
        </div>
      </div>
      <div className="flex gap-3 sm:gap-4 items-center">
        {showActions && (
          <div name="updateTransaction" className="hidden sm:block">
            <UpdateTransactionDialog type={type} item={item} />
          </div>
        )}
        <div
          name="amount"
          className={`p-2 border text-md ${
            type.toLowerCase() === "expense"
              ? "text-[#f32626] bg-[#f3262633]"
              : "text-[#00be00] bg-[#62d26233]"
          }  font-bold rounded-[4px] hidden xs375:flex justify-center items-center gap-2`}
        >
          {type.toLowerCase() === "income" ? "+" : "-"} {item.amount}
          {type.toLowerCase() === "income" && <TrendingUp stroke="#00be00" />}
          {type.toLowerCase() === "expense" && (
            <TrendingDown stroke="#ef4444" />
          )}
        </div>
      </div>
    </div>
  );
};

export default TransactionItem;
