import BarChartComponent from "@/components/BarChartComponent";
import FilterDialogComponent from "@/components/FilterDialogComponent";
import TransactionItem from "@/components/TransactionItem";
import PaginationComponent from "@/components/PaginationComponent";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import AddTransactionDialog from "@/components/AddTransactionDialog";
import LineChartComponent from "@/components/LineChartComponent";
import { TRANSACTION_URLS } from "@/constants/URLs/backendServices";
import { Pencil } from "lucide-react";

const Expense = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [activePage, setActivePage] = useState(
    Number(searchParams.get("page")) || 1
  );
  const [isEditable, setIsEditable] = useState(false);
  const ITEMS_PER_PAGE = 10;

  useEffect(() => {
    const currentPage = Number(searchParams.get("page")) || 1;
    setActivePage(currentPage);
  }, [searchParams]);

  const { page: pageQueryParam, ...queryParamsWithoutPage } =
    Object.fromEntries(searchParams);

  const {
    data: expenseData,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["expense", queryParamsWithoutPage],
    queryFn: async () => {
      const res = await axios.get(TRANSACTION_URLS.expense, {
        params: queryParamsWithoutPage, // to send as query params
        withCredentials: true,
      });

      return res.data;
    },
  });

  const totalPages = Math.ceil(expenseData?.totalCount / ITEMS_PER_PAGE);

  const filterOptions = [
    {
      name: "lastNDays",
      label: "Last N Days",
      isActive: searchParams.get("lastNDays") ? true : false,
    },
    {
      name: "month",
      label: "Month",
      isActive: searchParams.get("month") ? true : false,
    },
    {
      name: "year",
      label: "Year",
      isActive: searchParams.get("year") ? true : false,
    },
    {
      name: "date",
      label: "Date",
      isActive: searchParams.get("date") ? true : false,
    },
  ];

  const filterFormFields = {
    lastNDays: {
      label: "Enter number of days",
      type: "number",
      placeholder: "e.g. 7",
    },
    date: {
      label: "Enter date",
      type: "date",
    },
    month: {
      label: "Select month",
      type: "month",
    },
    year: {
      label: "Enter year",
      type: "number",
      placeholder: "e.g. 2025",
    },
  };

  const getFilterFormBody = (name) => {
    const field = filterFormFields[name];
    if (!field) return null;

    return (
      <div className="grid gap-3">
        <Label htmlFor={name}>{field.label}</Label>
        <Input
          id={name}
          name={name}
          type={field.type}
          placeholder={field.placeholder}
        />
      </div>
    );
  };

  const handleFilterSubmit = (e, name) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const value = formData.get(name);

    if (value) {
      // Replace all params with only the submitted one
      setSearchParams({ [name]: value, page: 1 });
    }
  };

  const handleClearFilter = () => {
    setSearchParams({});
  };

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;

    setSearchParams({
      ...Object.fromEntries(searchParams),
      page: page.toString(),
    });
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col gap-6">
      <div
        name="expenseChart"
        className="hidden sm:flex flex-col gap-4 rounded-[8px] p-4 bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]"
      >
        <div
          name="header"
          className="flex flex-wrap gap-2 justify-between items-center"
        >
          <div className="">
            <h1 className="text-2xl font-semibold">Expense Overview</h1>
            <p className="text -sm text-gray-500">
              Track your earnings over time and gain insights into where your
              money goes.
            </p>
          </div>
          <AddTransactionDialog type="Expense" />
        </div>
        <div className="flex gap-4">
          {filterOptions.map((option) => (
            <FilterDialogComponent
              key={option.name}
              filterData={option}
              handleFilterSubmit={handleFilterSubmit}
              content={getFilterFormBody(option.name)}
            />
          ))}

          <Button
            variant="ghost"
            className="ml-auto"
            onClick={handleClearFilter}
          >
            Clear Filter
          </Button>
        </div>

        {/* {Chart} */}
        <div name="chartSection" className="">
          {expenseData?.expenseList?.length > 0 ? (
            <LineChartComponent
              dataList={expenseData.expenseList}
              activeFilter={
                filterOptions.find((f) => f.isActive)?.name || "lastNDays"
              }
            />
          ) : (
            <div className="flex justify-center items-center h-[400px]">
              <h1 className="text-2xl">No data to display</h1>
            </div>
          )}
        </div>
      </div>
      <div
        name="expenseList"
        className="flex flex-col gap-4 p-6 rounded-[8px] bg-white drop-shadow-[0_4px_4px_rgba(0,0,0,0.1)]"
      >
        <div name="header" className="flex justify-between items-center">
          <h1 className="text-2xl font-semibold">Expense Sources</h1>
          <Button
            variant={isEditable ? "default" : "outline"}
            onClick={() => setIsEditable((prev) => !prev)}
            className={"!px-4"}
          >
            {" "}
            <Pencil /> Edit
          </Button>
          {/* {Download Button} */}
        </div>

        <div className="grid md900:grid-cols-2 gap-4">
          {expenseData?.expenseList
            ?.slice(
              (activePage - 1) * ITEMS_PER_PAGE,
              activePage * ITEMS_PER_PAGE
            )
            .map((item) => (
              <TransactionItem
                key={item.id}
                item={item}
                type="expense"
                showActions={isEditable}
              />
            ))}
        </div>

        {/* {Pagination} */}
        {totalPages > 0 ? (
          <div name="pagination" className="">
            <PaginationComponent
              activePage={activePage}
              totalPages={totalPages}
              handlePageChange={handlePageChange}
            />
          </div>
        ) : (
          <div className="flex justify-center items-center h-[400px]">
            <h1 className="text-2xl">No data to display</h1>
          </div>
        )}
      </div>
    </div>
  );
};

export default Expense;
