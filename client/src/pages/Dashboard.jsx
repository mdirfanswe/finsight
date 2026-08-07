import { Button } from "@/components/ui/button";
import { ArrowRight, HandCoins, PiggyBank, WalletMinimal } from "lucide-react";
import React, { useEffect, useState } from "react";
import PieChartComponent from "@/components/PieChartComponent";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import TransactionItem from "@/components/TransactionItem";
import { Link } from "react-router-dom";
import { TRANSACTION_URLS } from "@/constants/URLs/backendServices";

const Dashboard = () => {
  const {
    data: summaryData,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["summary"],
    queryFn: async () => {
      const res = await axios.get(TRANSACTION_URLS.summary, {
        withCredentials: true,
      });

      return res.data;
    },
  });

  const recentTransactionList = [
    ...(summaryData?.expenseSummary?.expenseList || []),
    ...(summaryData?.incomeSummary?.incomeList || []),
  ].sort((a, b) => {
    const dateA = new Date(a.incomeDate || a.expenseDate || 0);
    const dateB = new Date(b.incomeDate || b.expenseDate || 0);
    return dateB - dateA;
  });

  const totalExpense = summaryData?.expenseSummary.totalAmount;
  const totalIncome = summaryData?.incomeSummary.totalAmount;
  const totalBalance = parseFloat((totalIncome - totalExpense).toFixed(2));

  // Data for pie chart
  const summaryChartData = [
    {
      name: "Total Balance",
      value: totalBalance,
      color: "#7A52F2",
    },
    { name: "Total Income", value: totalIncome, color: "#FB5E00" },
    { name: "Total Expense", value: totalExpense, color: "#ef4444" },
  ];

  // Custom legend for pie chart
  const summaryRenderLegend = (props) => {
    const { payload } = props;
    return (
      <ul className="flex justify-center gap-6 mt-4">
        {payload.map((entry, index) => (
          <li key={`item-${index}`} className="flex items-center gap-2">
            <span
              className="w-3 h-3 rounded-full inline-block"
              style={{ backgroundColor: entry.color }}
            ></span>
            <span>{entry.value}</span>
          </li>
        ))}
      </ul>
    );
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col gap-4">
      {/* {First Row} */}
      <div
        name="summary"
        className="flex flex-col md900:flex-row gap-4 items-center"
      >
        <div className="flex-1 w-full p-4 rounded-[8px] flex gap-4 items-center bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          <div className="flex rounded-full p-4 bg-[#7A52F2]">
            <PiggyBank stroke="white" size={24} />
          </div>
          <div className="">
            <p>Total Balance</p>
            <h2>${totalBalance}</h2>
          </div>
        </div>
        <div className="flex-1 w-full p-4 rounded-[8px] flex gap-4 items-center bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          <div className="rounded-full p-4 bg-[#FB5E00]">
            <WalletMinimal stroke="white" size={24} />
          </div>
          <div className="">
            <p>Total Income</p>
            <h2>${totalIncome}</h2>
          </div>
        </div>
        <div className="flex-1 w-full p-4 rounded-[8px] flex gap-4 items-center bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          <div className="rounded-full p-4 bg-[#ef4444]">
            <HandCoins stroke="white" size={24} />
          </div>
          <div className="">
            <p>Total Expense</p>
            <h2>${totalExpense}</h2>
          </div>
        </div>
      </div>

      {/* {Second Row} */}
      <div className="flex flex-col md900:flex-row gap-4">
        <div className="flex-1 flex flex-col gap-4 p-4 rounded-[8px] bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          <div name="heading" className="flex justify-between items-center">
            <h2 className="text-lg font-semibold">Recent Transactions</h2>
            {/* <Button>
              See all <ArrowRight />
            </Button> */}
          </div>
          <div className="grid gap-4">
            {recentTransactionList?.slice(0, 5).map((item) => (
              <TransactionItem
                key={item.id}
                item={item}
                type={item.expenseDate ? "Expense" : "Income"}
                showActions={false}
              />
            ))}
          </div>
        </div>
        <div className="hidden flex-1 sm:flex flex-col gap-4 p-4 rounded-[8px] bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          {/* {Chart} */}
          <h2 className="text-lg font-semibold">Financial Overview</h2>
          <div className="flex justify-center items-center flex-1">
            <PieChartComponent
              data={summaryChartData}
              renderLegend={summaryRenderLegend}
              centerTextHeading={"Total Balance"}
              centerTextValue={`$${totalBalance}`}
            />
          </div>
        </div>
      </div>

      {/* {Third Row} */}
      <div className="flex flex-col md900:flex-row gap-4">
        <div className="flex-1 flex flex-col gap-4 p-4 rounded-[8px] bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          <div name="heading" className="flex justify-between items-center">
            <h2 className="text-lg font-semibold">Recent Income</h2>
            <Button>
              <Link to="/dashboard/income" className="flex items-center gap-2">
                See all <ArrowRight />
              </Link>
            </Button>
          </div>
          <div className="grid gap-4">
            {summaryData?.incomeSummary?.incomeList.slice(0, 5).map((item) => (
              <TransactionItem
                key={item.id}
                item={item}
                type="Income"
                showActions={false}
              />
            ))}
          </div>
        </div>
        <div className="flex-1 flex flex-col gap-4 p-4 rounded-[8px] bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]">
          <div name="heading" className="flex justify-between items-center">
            <h2 className="text-lg font-semibold">Recent Expense</h2>
            <Button>
              <Link to="/dashboard/expense" className="flex items-center gap-2">
                See all <ArrowRight />
              </Link>
            </Button>
          </div>
          <div className="grid gap-4">
            {recentTransactionList?.slice(0, 5).map((item) => (
              <TransactionItem
                key={item.id}
                item={item}
                type="Expense"
                showActions={false}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
